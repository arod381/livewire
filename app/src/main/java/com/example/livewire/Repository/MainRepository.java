package com.example.livewire.Repository;

import com.example.livewire.Service.AIService;

import com.example.livewire.Model.ChatMessage;

import java.util.List;

public class MainRepository {

    private final AIService aiservice = new AIService();
    public interface RepositoryCallback {
        void onResult(String result);
        void onError(String error);
    }

    public void submitPrompt(List<ChatMessage> messages, RepositoryCallback callback) {

        aiservice.sendPrompt(messages, new AIService.ServiceCallback() {
            @Override
            public void onResult(String response) {
                callback.onResult(response);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void getDiagnostics(RepositoryCallback callback) {

        aiservice.getDiagnostics(
                new AIService.ServiceCallback() {

                    @Override
                    public void onResult(String response) {
                        callback.onResult(response);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                }
        );
    }
}
