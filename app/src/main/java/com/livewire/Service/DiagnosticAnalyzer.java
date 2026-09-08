package com.livewire.Service;

import com.livewire.Model.ContextPerformance;
import com.livewire.Model.DiagnosticEvent;
import com.livewire.Model.DiagnosticStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosticAnalyzer {

    private static int extractContext(String details) {

        if (details == null) {
            return Integer.MIN_VALUE;
        }

        String marker = "context=";

        int start = details.indexOf(marker);

        if (start == -1) {
            return Integer.MIN_VALUE;
        }

        start += marker.length();

        int end = details.indexOf(" |", start);

        if (end == -1) {
            end = details.length();
        }

        try {

            return Integer.parseInt(details.substring(start, end));
        } catch (NumberFormatException e) {

            return Integer.MIN_VALUE;
        }
    }
    
    public static DiagnosticStatistics analyze(List<DiagnosticEvent> events) {

        DiagnosticStatistics statistics =
                new DiagnosticStatistics();

        if (events == null) {
            return statistics;
        }

        int successful = 0;
        int failed = 0;
        int networkErrors = 0;
        int httpErrors = 0;
        int parseErrors = 0;

        long totalResponseTime = 0;
        long slowestResponse = 0;

        for (DiagnosticEvent event : events) {

            String type = event.getType();

            long duration = event.getDurationMs();

            if ("CHAT_SUCCESS".equals(type)) {

                successful++;

            } else if ("NETWORK_ERROR".equals(type)) {

                failed++;
                networkErrors++;

            } else if ("HTTP_ERROR".equals(type)) {

                failed++;
                httpErrors++;

            } else if ("RESPONSE_PARSE_ERROR".equals(type)) {

                failed++;
                parseErrors++;
            }

            /*
             * AI_REQUEST_COMPLETED is a performance event.
             * It does not count as success or failure.
             */
            if ("AI_REQUEST_COMPLETED".equals(type)) {

                totalResponseTime += duration;

                if (duration > slowestResponse) {
                    slowestResponse = duration;
                }
            }
        }

        statistics.setTotalRequests(
                successful + failed
        );

        statistics.setSuccessfulRequests(
                successful
        );

        statistics.setFailedRequests(
                failed
        );

        statistics.setNetworkErrors(
                networkErrors
        );

        statistics.setHttpErrors(
                httpErrors
        );

        statistics.setParseErrors(
                parseErrors
        );

        statistics.setTotalResponseTimeMs(
                totalResponseTime
        );

        statistics.setSlowestResponseMs(
                slowestResponse
        );

        return statistics;
    }

    public static List<ContextPerformance> analyzeContextPerformance(
            List<DiagnosticEvent> events) {

        List<ContextPerformance> results =
                new ArrayList<>();

        if (events == null) {
            return results;
        }

        Map<Integer, Long> totalTimes =
                new HashMap<>();

        Map<Integer, Integer> requestCounts =
                new HashMap<>();

        for (DiagnosticEvent event : events) {

            if(!"AI_REQUEST_COMPLETED".equals(
                    event.getType())) {
                continue;
            }

            String details = event.getDetails();

            int contextLimit =
                    extractContext(details);

            if (contextLimit == Integer.MIN_VALUE) {
                continue;
            }

            long duration =
                    event.getDurationMs();

            totalTimes.put(contextLimit, totalTimes.getOrDefault(contextLimit, 0L) + duration);

            requestCounts.put(contextLimit, requestCounts.getOrDefault(contextLimit, 0) + 1);

        }

        for (Integer contextLimit : requestCounts.keySet()) {

            int count = requestCounts.get(contextLimit);

            long total = totalTimes.get(contextLimit);

            long average = total / count;

            results.add(
                    new ContextPerformance(contextLimit, count, average)
            );
        }

        return results;
    }

}
