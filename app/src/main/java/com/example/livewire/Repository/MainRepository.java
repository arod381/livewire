package com.example.livewire.Repository;

import com.example.livewire.Model.DiagnosticEvent;
import com.example.livewire.Model.DiagnosticReport;
import com.example.livewire.Service.AIService;

import com.example.livewire.Model.ChatMessage;
import com.example.livewire.Service.DiagnosticEventLogger;

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

    public interface DiagnosticsRepositoryCallback {
        void onResult(DiagnosticReport report);
        void onError(String error);
    }

    public void getDiagnostics(DiagnosticsRepositoryCallback callback) {

        aiservice.getDiagnostics(
                new AIService.DiagnosticsCallback() {

                    @Override
                    public void onResult(DiagnosticReport report) {
                        List<DiagnosticEvent> events =
                                DiagnosticEventLogger.getEvents();

                        report.setEvents(events);

                        callback.onResult(report);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                }
        );
    }
}
