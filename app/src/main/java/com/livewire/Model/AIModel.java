package com.livewire.Model;

public class AIModel {

    private final String id;
    private final String displayName;
    private final String backend;

    public AIModel(
            String id,
            String displayName,
            String backend) {

        this.id = id;
        this.displayName = displayName;
        this.backend = backend;
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
}