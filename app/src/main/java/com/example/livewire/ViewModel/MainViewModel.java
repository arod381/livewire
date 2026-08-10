package com.example.livewire.ViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModel extends ViewModel {

    private String prompt;

    public void submitPrompt(String prompt) {
        this.prompt = prompt;
    }
}
