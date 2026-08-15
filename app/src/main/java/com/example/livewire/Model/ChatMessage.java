package com.example.livewire.Model;

public class ChatMessage {

    public enum Sender {
        USER,
        AI
    }

    private final String message;
    private final Sender sender;

    public ChatMessage(String message, Sender sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public Sender getSender() {
        return sender;
    }

}
