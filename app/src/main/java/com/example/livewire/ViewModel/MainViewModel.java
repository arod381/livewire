package com.example.livewire.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.livewire.Model.ChatMessage;
import com.example.livewire.Repository.MainRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> currentPrompt =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<List<ChatMessage>> conversation =
            new MutableLiveData<>(new ArrayList<>());

    private final MainRepository repository;

    public MainViewModel() {
        repository = new MainRepository();
    }

    public void submitPrompt(String prompt) {

        currentPrompt.setValue(prompt);

        List<ChatMessage> existing = conversation.getValue();

        List<ChatMessage> messages =
                existing == null
                        ? new ArrayList<>()
                        : new ArrayList<>(existing);

        messages.add(
                new ChatMessage(prompt, ChatMessage.Sender.USER)
        );

        conversation.setValue(messages);

        loading.setValue(true);

        List<ChatMessage> conversationSnapshot =
                new ArrayList<>(messages);

        repository.submitPrompt(conversationSnapshot, new MainRepository.RepositoryCallback() {
            @Override
            public void onResult(String result) {

                List<ChatMessage> existing = conversation.getValue();

                List<ChatMessage> messages =
                        existing == null
                                ? new ArrayList<>()
                                : new ArrayList<>(existing);

                messages.add(
                        new ChatMessage(result, ChatMessage.Sender.AI)
                );

                conversation.postValue(messages);

                loading.postValue(false);
            }

            @Override
            public void onError(String error) {

                List<ChatMessage> messages = conversation.getValue();

                if (messages == null) {
                    messages = new ArrayList<>();
                }

                messages.add(
                        new ChatMessage(
                                "Error: " + error,
                                ChatMessage.Sender.AI
                        )
                );

                conversation.postValue(messages);

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

    public LiveData<List<ChatMessage>> getConversation() {
        return conversation;
    }
}
