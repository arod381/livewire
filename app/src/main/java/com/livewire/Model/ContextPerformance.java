package com.livewire.Model;

public class ContextPerformance {

    private final int contextLimit;
    private final int requestCount;
    private final long averageResponseMs;

    public ContextPerformance(
            int contextLimit,
            int requestCount,
            long averageResponseMs) {

        this.contextLimit = contextLimit;
        this.requestCount = requestCount;
        this.averageResponseMs = averageResponseMs;
    }

    public int getContextLimit() {
        return contextLimit;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public long getAverageResponseMs() {
        return averageResponseMs;
    }

}
