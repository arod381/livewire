package com.livewire.Model;

public class DiagnosticStatistics {

    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private int networkErrors;
    private int httpErrors;
    private int parseErrors;

    private long totalResponseTimeMs;
    private long slowestResponseMs;

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public void setSuccessfulRequests(int successfulRequests) {
        this.successfulRequests = successfulRequests;
    }

    public int getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(int failedRequests) {
        this.failedRequests = failedRequests;
    }

    public int getNetworkErrors() {
        return networkErrors;
    }

    public void setNetworkErrors(int networkErrors) {
        this.networkErrors = networkErrors;
    }

    public int getHttpErrors() {
        return httpErrors;
    }

    public void setHttpErrors(int httpErrors) {
        this.httpErrors = httpErrors;
    }

    public int getParseErrors() {
        return parseErrors;
    }

    public void setParseErrors(int parseErrors) {
        this.parseErrors = parseErrors;
    }

    public long getTotalResponseTimeMs() {
        return totalResponseTimeMs;
    }

    public void setTotalResponseTimeMs(
            long totalResponseTimeMs) {

        this.totalResponseTimeMs =
                totalResponseTimeMs;
    }

    public long getSlowestResponseMs() {
        return slowestResponseMs;
    }

    public void setSlowestResponseMs(
            long slowestResponseMs) {

        this.slowestResponseMs =
                slowestResponseMs;
    }

    public long getAverageResponseTimeMs() {

        if (successfulRequests == 0) {
            return 0;
        }

        return totalResponseTimeMs /
                successfulRequests;
    }
}