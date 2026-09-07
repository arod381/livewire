package com.livewire.Service;

import com.livewire.Model.DiagnosticEvent;
import com.livewire.Model.DiagnosticStatistics;

import java.util.List;

public class DiagnosticAnalyzer {

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

}
