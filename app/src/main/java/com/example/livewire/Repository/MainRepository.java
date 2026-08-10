package com.example.livewire.Repository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainRepository {

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public interface RepositoryCallback {
        void onResult(String result);
    }

    public void submitPrompt(String prompt, RepositoryCallback callback) {

        executor.execute(() -> {

            String result = "Repository received: " + prompt;

            callback.onResult(result);
        });
    }
}
