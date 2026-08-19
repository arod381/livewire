package com.example.livewire.Service;

import com.example.livewire.Model.DiagnosticEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagnosticEventLogger {

    private static final List<DiagnosticEvent> events =
            Collections.synchronizedList(
                    new ArrayList<>()
            );

    public static void log(
            String type,
            String details,
            long durationMs) {

        events.add(
                new DiagnosticEvent(
                        System.currentTimeMillis(),
                        type,
                        details,
                        durationMs
                )
        );
    }

    public static List<DiagnosticEvent> getEvents() {

        synchronized (events) {
            return new ArrayList<>(events);
        }
    }

    public static void clear() {
        events.clear();
    }
}