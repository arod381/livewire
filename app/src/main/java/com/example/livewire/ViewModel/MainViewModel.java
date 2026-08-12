package com.example.livewire.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.livewire.Repository.MainRepository;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> currentPrompt =
            new MutableLiveData<>();

    private final MutableLiveData<String> response =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> conversation =
            new MutableLiveData<>("");

    private final MainRepository repository;

    public MainViewModel() {
        repository = new MainRepository();
    }

    public void submitPrompt(String prompt) {
        currentPrompt.setValue(prompt);

        String existingConversation = conversation.getValue();

        if (existingConversation == null) {
            existingConversation = "";
        }

        conversation.setValue(
                existingConversation +
                "\n\nYou: \n" +
                prompt
        );

        loading.setValue(true);

        repository.submitPrompt(prompt, new MainRepository.RepositoryCallback() {
            @Override
            public void onResult(String result) {

                String existing = conversation.getValue();

                if (existing == null) {
                    existing = "";
                }

                conversation.postValue(
                        existing +
                        "\n\nLiveWire:\n" +
                        result
                );

                loading.postValue(false);
            }

            @Override
            public void onError(String error) {

                String existing = conversation.getValue();

                if (existing == null) {
                    existing = "";
                }

                conversation.postValue(
                        existing +
                        "\n\nLiveWire:\nError: " +
                        error
                );

                loading.postValue(false);
            }
        });
    }

    public LiveData<String> getCurrentPrompt() {
        return currentPrompt;
    }
    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getConversation() {
        return conversation;
    }
}
