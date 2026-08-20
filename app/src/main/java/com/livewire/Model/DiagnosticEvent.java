package com.livewire.Model;

public class DiagnosticEvent {

    private final long timestamp;
    private final String type;
    private final String details;
    private final long durationMs;

    public DiagnosticEvent(
            long timestamp,
            String type,
            String details,
            long durationMs) {

        this.timestamp = timestamp;
        this.type = type;
        this.details = details;
        this.durationMs = durationMs;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    public long getDurationMs() {
        return durationMs;
    }
}