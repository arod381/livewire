from transformers import AutoTokenizer, AutoModelForCausalLM

# Import FASTAPI framework components used to create HTTP API endpoints
from fastapi import FastAPI, HTTPException

# Import Pydantic's BaseModel for defining and validating request/response schemas
from pydantic import BaseModel

from llama_cpp import Llama

# Optional is imported for fields that may accept None Values
from typing import Optional

from pathlib import Path

# Requests Library is used to communicate with the local Ollama LLM server
import requests

import torch

# Time module is used to track server uptime
import time

import os

# Create the FastAPI application instance
app = FastAPI()

# Store the timestamp when the application starts
SERVER_START_TIME = time.time()

# URL for the local Ollama chat API endpoint
# Ollama must be running locally for the AI requests to succeed
OLLAMA_URL = "http://127.0.0.1:11434/api/chat"

PHI4MINI_MODEL_PATH = "microsoft/Phi-4-mini-instruct"

phi4mini_tokenizer = AutoTokenizer.from_pretrained(PHI4MINI_MODEL_PATH)
phi4mini_model = AutoModelForCausalLM.from_pretrained(PHI4MINI_MODEL_PATH)

# Configuration information describing the active model settings
MODEL_CONFIGS = {

    "phi4-mini": {
        "name": "Phi-4-mini 3.8B",
        "backend": "ollama",
        "path": "microsoft/Phi-4-mini-instruct",
        "temperature": 0.8, # Controls randomness of generated responses
        "top_p": 0.9,       # Controls nucleus sampling probability
        "top_k": 10,        # Limits token selection to the top K choices
        "max_tokens": 350   # Max number of tokens generated per response
    },
    
    "livewire1.0:0": {
        "name": "LiveWire 1.0 1A",
        "backend": "transformers",
        "path": "/home/omegon/Documents/llm_livewire_training/student_model_merged",
        "temperature": 0.8, # Controls randomness of generated responses
        "top_p": 0.9,       # Controls nucleus sampling probability
        "top_k": 10,        # Limits token selection to the top K choices
        "max_tokens": 350   # Max number of tokens generated per response
    }

}

student_config = MODEL_CONFIGS["livewire1.0:0"]

student_tokenizer = AutoTokenizer.from_pretrained(student_config["path"])

student_model = Llama(
    model_path="/home/omegon/Documents/llm_livewire_training/student_model.gguf",
    n_ctx=2048,          # context window
    n_threads=os.cpu_count(),  # use all available CPU cores
    chat_format="chatml",  # Qwen uses ChatML-style formatting (<|im_start|>/<|im_end|>)
)

def get_model_config(model_id: str):

    config = MODEL_CONFIGS.get(model_id)

    if config is None:
        raise ValueError(
            f"Unknown model: {model_id}"
        )
    return config

# Defines the expected structure of diagnostic data sent to /analyze endpoint
# FastAPI automatically validates incoming JSON against this model
class DiagnosticAnalysisRequest(BaseModel):

    model: str

    # Current application health status
    server_status: str

    # How long the monitored applications has been running
    uptime_seconds: float

    # LLM configuration information
    model_name: str
    temperature: float
    top_p: float
    top_k: int
    max_tokens: int

    # Request statistics collected by the application
    total_requests: int
    successful_requests: int
    failed_requests: int

    # Categories of failures tracked by diagnostics
    network_errors: int
    http_errors: int
    parse_errors: int

    # Performance measurements
    average_response_ms: float
    slowest_response_ms: float

    # Historical application events
    # Each dictionary represents an event record
    events: list[dict]


# Health and configuration endpoint
# Returns basic server status information and model settings
@app.get("/diagnostics")
def diagnostics(model: str):

    config = get_model_config(model)
    
    return {
        "server": {
            "status": "healthy",
            "uptime_seconds": round(
                time.time() - SERVER_START_TIME, 2
            )
        },

        "model": {
            "id": model,
            "name": config["name"],
            "backend": config["backend"],
            "temperature": config["temperature"],
            "top_p": config["top_p"],
            "top_k": config["top_k"],
            "max_tokens": config["max_tokens"]
        }
    }

# Diagnostic analysis endpoint
# Sends application diagnostic information to the LLM
# and returns an AI-generated analysis
@app.post("/analyze")
def analyze(request: DiagnosticAnalysisRequest):

    prompt = f"""
You are analyzing the LiveWire application itself.

Review the following diagnostic information and identify
actual problems, abnormal behavior, or potential concerns.

Do not invent problems that are not supported by the data.

SERVER
Status: {request.server_status}
Uptime: {request.uptime_seconds} seconds

MODEL
Name: {request.model_name}
Temperature: {request.temperature}
Top P: {request.top_p}
Top K: {request.top_k}
Max Tokens: {request.max_tokens}

PERFORMANCE
Total Requests: {request.total_requests}
Successful Requests: {request.successful_requests}
Failed Requests: {request.failed_requests}
Network Errors: {request.network_errors}
HTTP Errors: {request.http_errors}
Parse Errors: {request.parse_errors}
Average Response Time: {request.average_response_ms} ms
Slowest Response Time: {request.slowest_response_ms} ms

EVENT HISTORY
{request.events}

Analyze the LiveWire application based only on the supplied
diagnostic information.

Look for:

1. Repeated failures
2. Recurring error types
3. Unusually slow responses
4. Patterns in response times
5. Network problems
6. HTTP failures
7. Response parsing failures
8. Evidence that the application is behaving normally

Distinguish isolated events from recurring patterns.

Do not invent problems that aren't supported by the data.

For each significant problem, identify:
- Evidence
- Likely cause
- Severity

If no significant problem is supported by the evidence,
state that the application appears healthy.

Do not modify the application or recommend changes unless
the diagnostic evidence supports the recommendation.

Do not recommend changing anything unless the
available evidence supports the recommendation.
"""

    # Send the diagnostic prompt to Ollama
    # stream=False requests a complete response instead of
    # receiving the answer incrementally
    ollama_response = requests.post(
        OLLAMA_URL,
        json={
            "model": request.model,
            "messages": [
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            "stream": False
        },
        timeout=360
    )

    # Raise an exception if Ollama returned an HTTP error
    ollama_response.raise_for_status()

    # Convert Ollama's JSON response into a Python dictionary
    data = ollama_response.json()

    # Return only the generated analysis text to the client
    return {
        "analysis": data["message"]["content"]
    }

# Defines a single chat message
# Used for general conversation with model
class ChatMessage(BaseModel):

    # Message sender type, usually "user" or "assistant"
    role: str

    # Text content of the message
    content: str

# Defines the request body for the /chat endpoint
class ChatRequest(BaseModel):

    model: str

    backend: str

    # List of previous messages forming the conversation history
    messages: list[ChatMessage]

# Defines the response format returned by /chat
class ChatResponse(BaseModel):

    # Model-generated response text
    response: str

# General chat endpoint
# Accepts a conversation history and forwards it to Ollama
@app.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest):

    print(
        "MODEL:",
        request.model,
        "BACKEND:",
        request.backend
    )

    config = get_model_config(request.model)

    if config["backend"] != request.backend:

        raise HTTPException(
            status_code=400,
            detail="Model/backend mismatch"
        )

    if request.backend == "ollama":
        return chat_with_ollama(request, config)

    elif request.backend == "transformers":
        return chat_with_llama_cpp(request, config)

    elif request.backend == "phi4mini_base":
        return chat_with_phi4mini_base(request, config)

    else:
        raise HTTPException(
            status_code=400,
            detail=(
                f"Unknown backend: "
                f"{request.backend}"
            )
        )

def chat_with_phi4mini_base(request: ChatRequest, config: dict):

    messages = [
        {"role": message.role, "content": message.content}
        for message in request.messages
    ]

    prompt = phi4mini_tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True,
    )

    inputs = phi4mini_tokenizer(
        prompt,
        return_tensors="pt"
    )

    with torch.no_grad():

        outputs = phi4mini_model.generate(
            **inputs,
            max_new_tokens=config["max_tokens"],
            do_sample=True,
            temperature=config["temperature"],
            top_p=config["top_p"],
            top_k=config["top_k"],
            pad_token_id=phi4mini_tokenizer.pad_token_id or phi4mini_tokenizer.eos_token_id,
        )

    generated_tokens = outputs[0][inputs["input_ids"].shape[1]:]

    response = phi4mini_tokenizer.decode(
        generated_tokens,
        skip_special_tokens=True
    )

    return ChatResponse(
        response=response.strip()
    )

    # Send the conversation messages to the local model
def chat_with_ollama(request: ChatRequest, config: dict):
    ollama_response = requests.post(
        OLLAMA_URL,
        json={
            "model": request.model,

            # Convert Pydantic message objects into
            # Ollama-compatible dictionaries
            "messages": [
                {
                    "role": message.role,
                    "content": message.content
                }
                for message in request.messages
            ],

            # Request a complete response
            "stream": False,

            "options": {
                "temperature": config["temperature"],
                "top_p": config["top_p"],
                "top_k": config["top_k"],
                "num_predict": config["max_tokens"]
            }
        },

        # Prevent requests from hanging indefinitely
        timeout=360
    )

    # Stop processing if Ollama returned an error
    ollama_response.raise_for_status()

    # Parse the model response JSON
    data = ollama_response.json()

    # Return the generated text using the defined response model
    return ChatResponse(
        response=data["message"]["content"]
    )

def chat_with_llama_cpp(request: ChatRequest, config: dict):
    messages = [
        {"role": message.role, "content": message.content}
        for message in request.messages
    ]

    output = student_model.create_chat_completion(
        messages=messages,
        max_tokens=config["max_tokens"],
        temperature=config["temperature"],
        top_p=config["top_p"],
        top_k=config["top_k"]
    )

    response = output["choices"][0]["message"]["content"]

    return ChatResponse(response=response.strip()
    )

def chat_with_transformers(request: ChatRequest, config: dict):

    messages = [
        {"role": message.role, "content": message.content}
        for message in request.messages
    ]

    prompt = student_tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True, # appends the assistant turn opener, ready for generation
        enable_thinking=False,
    )

    inputs = student_tokenizer(
        prompt,
        return_tensors="pt"
    )

    im_end_id = student_tokenizer.convert_tokens_to_ids("<|im_end|>")

    with torch.no_grad():

        outputs = student_model.generate(
            **inputs,
            max_new_tokens=config["max_tokens"],
            do_sample=True,
            temperature=config["temperature"],
            top_p=config["top_p"],
            top_k=config["top_k"],
            eos_token_id=[student_tokenizer.eos_token_id, im_end_id],
            pad_token_id=student_tokenizer.pad_token_id,
        )

    generated_tokens = outputs[0][inputs["input_ids"].shape[1]:]

    response = student_tokenizer.decode(
        generated_tokens,
        skip_special_tokens=True
    )
    print(f"Generated response: {repr(response)}")

    return ChatResponse(
        response=response.strip()
    )
