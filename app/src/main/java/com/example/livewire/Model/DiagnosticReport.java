package com.example.livewire.Model;

import com.example.livewire.Model.DiagnosticEvent;

import java.util.List;

public class DiagnosticReport {

    private String serverStatus;
    private double uptimeSeconds;
    private String modelName;
    private double temperature;
    private double topP;
    private int topK;
    private int maxTokens;
    private ApplicationDiagnostics application;

    private List<DiagnosticEvent> events;

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public double getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(double uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTopP() {
        return topP;
    }

    public void setTopP(double topP) {
        this.topP = topP;
    }

    public int getTopK() {
        return topK;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public ApplicationDiagnostics getApplication() {
        return application;
    }

    public void setApplication( ApplicationDiagnostics application) {

        this.application = application;
    }

    public List<DiagnosticEvent> getEvents() {
        return events;
    }

    public void setEvents(List<DiagnosticEvent> events) {
        this.events = events;
    }
 }
