from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional
import requests
import time

app = FastAPI()

SERVER_START_TIME = time.time()

OLLAMA_URL = "http://127.0.0.1:11434/api/chat"
MODEL = "qwen3:1.7b"

MODEL_CONFIG = {
    "name": "Qwen",
    "temperature": 0.7,
    "top_p": 0.9,
    "top_k": 10,
    "max_tokens": 100
}

class DiagnosticAnalysisRequest(BaseModel):
    server_status: str
    uptime_seconds: float

    model_name: str
    temperature: float
    top_p: float
    top_k: int
    max_tokens: int

    total_requests: int
    successful_requests: int
    failed_requests: int
    network_errors: int
    http_errors: int
    parse_errors: int

    average_response_ms: float
    slowest_response_ms: float

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

Provide a concise diagnostic assessment.

If the application appears healthy, say so.

If there are problems, identify:
1. The evidence
2. The likely cause
3. The severity

Do not recommend changing anything unless the
available evidence supports the recommendation.
"""


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

    ollama_response.raise_for_status()

    data = ollama_response.json()

    return {
        "analysis": data["message"]["content"]
    }

class ChatMessage(BaseModel):
    role: str
    content: str

class ChatRequest(BaseModel):
    messages: list[ChatMessage]

class ChatResponse(BaseModel):
    response: str

@app.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest):

    ollama_response = requests.post(
        OLLAMA_URL,
        json={
            "model": MODEL,
            "messages": [
                {
                    "role": message.role,
                    "content": message.content
                }
                for message in request.messages
            ],
            "stream": False
        },
        timeout=360
    )

    ollama_response.raise_for_status()

    data = ollama_response.json()

    return ChatResponse(
        response=data["message"]["content"]
    )
