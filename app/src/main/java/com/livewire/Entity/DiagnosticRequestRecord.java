package com.livewire.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diagnostic_request_records")
public class DiagnosticRequestRecord {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long timestamp;

    private String modelId;

    private int contextLimit;
    private int messageCount;

    private double temperature;
    private double topP;
    private int topK;
    private int maxTokens;

    private long responseTimeMs;

    private boolean successful;

    private String errorType;

    public DiagnosticRequestRecord(
            long timestamp,
            String modelId,
            int contextLimit,
            int messageCount,
            double temperature,
            double topP,
            int topK,
            int maxTokens,
            long responseTimeMs,
            boolean successful,
            String errorType) {

        this.timestamp = timestamp;
        this.modelId = modelId;
        this.contextLimit = contextLimit;
        this.messageCount = messageCount;
        this.temperature = temperature;
        this.topP = topP;
        this.topK = topK;
        this.maxTokens = maxTokens;
        this.responseTimeMs = responseTimeMs;
        this.successful = successful;
        this.errorType = errorType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getModelId() {
        return modelId;
    }

    public int getContextLimit() {
        return contextLimit;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTopP() {
        return topP;
    }

    public int getTopK() {
        return topK;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorType() {
        return errorType;
    }
}