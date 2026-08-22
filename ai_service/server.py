# Import FASTAPI framework components used to create HTTP API endpoints
from fastapi import FastAPI

# Import Pydantic's BaseModel for defining and validating request/response schemas
from pydantic import BaseModel

# Optional is imported for fields that may accept None Values
from typing import Optional

# Requests Library is used to communicate with the local Ollama LLM server
import requests

# Time module is used to track server uptime
import time

# Create the FastAPI application instance
app = FastAPI()

# Store the timestamp when the application starts
SERVER_START_TIME = time.time()

# URL for the local Ollama chat API endpoint
# Ollama must be running locally for the AI requests to succeed
OLLAMA_URL = "http://127.0.0.1:11434/api/chat"

# The LLM model that will handle chat and diagnostic analysis requests
MODEL = "qwen3:1.7b"

# Configuration information describing the active model settings
MODEL_CONFIG = {
    "name": "Qwen",
    "temperature": 0.7, # Controls randomness of generated responses
    "top_p": 0.9,       # Controls nucleus sampling probability
    "top_k": 10,        # Limits token selection to the top K choices
    "max_tokens": 100   # Max number of tokens generated per response
}

# Defines the expected structure of diagnostic data sent to /analyze endpoint
# FastAPI automatically validates incoming JSON against this model
class DiagnosticAnalysisRequest(BaseModel):

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
def diagnostics():

    return {
        "server": {
            "status": "healthy",
            "uptime_seconds": round(
                time.time() - SERVER_START_TIME, 2
            )
        },

        "model": MODEL_CONFIG
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
            "model": MODEL,
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

    # Send the conversation messages to the local model
    ollama_response = requests.post(
        OLLAMA_URL,
        json={
            "model": MODEL,

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
            "stream": False
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
