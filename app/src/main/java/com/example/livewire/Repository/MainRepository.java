package com.example.livewire.Repository;

import com.example.livewire.Service.AIService;

public class MainRepository {

    private final AIService aiservice = new AIService();
    public interface RepositoryCallback {
        void onResult(String result);
        void onError(String error);
    }

    public void submitPrompt(String prompt, RepositoryCallback callback) {

        aiservice.sendPrompt(prompt, new AIService.ServiceCallback() {
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
}
