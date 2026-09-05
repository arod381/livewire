package com.livewire.Model;

public class AIModel {

    private final String id;
    private final String displayName;
    private final String backend;

    private final int maxTokens;
    private final double temperature;
    private final double topP;
    private final int topK;

    public AIModel(
            String id,
            String displayName,
            String backend,
            int maxTokens,
            double temperature,
            double topP,
            Integer topK
            ) {

        this.id = id;
        this.displayName = displayName;
        this.backend = backend;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.topP = topP;
        this.topK = topK;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBackend() {
        return backend;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public int getMaxTokens() { return maxTokens; }

    public double getTemperature() { return temperature; }

    public double getTopP() { return topP; }

    public Integer getTopK() { return topK; }
}