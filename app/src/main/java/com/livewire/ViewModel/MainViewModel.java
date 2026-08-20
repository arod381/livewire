package com.livewire.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.livewire.Model.ChatMessage;
import com.livewire.Model.DiagnosticEvent;
import com.livewire.Model.DiagnosticReport;
import com.livewire.Model.DiagnosticStatistics;

import com.livewire.Service.DiagnosticAnalyzer;
import com.livewire.Service.DiagnosticEventLogger;

import java.util.List;
import com.livewire.Repository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Integer> contextLimit =
            new MutableLiveData<>(10);

    private final MutableLiveData<String> currentPrompt =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<List<ChatMessage>> conversation =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<DiagnosticReport> diagnostics =
            new MutableLiveData<>();

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

        int limit = contextLimit.getValue() != null
                ? contextLimit.getValue()
                : 10;

        List<ChatMessage> aiContext;

        if (limit == 0) {

            // Context OFF:
            // Send only the current user prompt
            aiContext = new ArrayList<>();

            aiContext.add(
                    new ChatMessage(
                            prompt,
                            ChatMessage.Sender.USER
                    )
            );

        } else if (limit == -1 || messages.size() <= limit) {

            // All context
            aiContext = new ArrayList<>(messages);

        } else {

            // Limited recent context
            aiContext = new ArrayList<>(
                    messages.subList(
                            messages.size() - limit,
                            messages.size()
                    )
            );
        }

        List<ChatMessage> conversationSnapshot =
                new ArrayList<>(messages);

        repository.submitPrompt(aiContext, new MainRepository.RepositoryCallback() {
            @Override
            public void onResult(String result) {

                android.util.Log.d(
                        "LiveWire",
                        "AI RESPONSE: " + result
                );

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

    public void loadDiagnostics() {

        repository.getDiagnostics(
                new MainRepository.DiagnosticsRepositoryCallback() {

                    @Override
                    public void onResult(DiagnosticReport report) {

                        List<DiagnosticEvent> events =
                                DiagnosticEventLogger.getEvents();

                        report.setEvents(events);

                        DiagnosticStatistics statistics =
                                DiagnosticAnalyzer.analyze(events);

                        report.setStatistics(statistics);

                        diagnostics.postValue(report);
                    }

                    @Override
                    public void onError(String error) {

                        diagnostics.postValue(null);
                    }
                }
        );
    }

    public LiveData<DiagnosticReport> getDiagnostics() {
        return diagnostics;
    }

    public LiveData<Integer> getContextLimit() {
        return contextLimit;
    }

    public void setContextLimit(int limit) {
        contextLimit.setValue(limit);
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
