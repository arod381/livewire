from fastapi import FastAPI
from pydantic import BaseModel
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
