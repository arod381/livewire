package com.livewire.Model;

import java.util.ArrayList;
import java.util.List;

public class ConversationSnapshot {

    private final long timestamp;
    private final String modelId;
    private final int contextLimit;
    private final List<ChatMessage> messages;


    public ConversationSnapshot(
            long timestamp,
            String modelId,
            int contextLimit,
            List<ChatMessage> messages) {

        this.timestamp = timestamp;
        this.modelId = modelId;
        this.contextLimit = contextLimit;

        // Create a copy so the snapshot does not change
        this.messages = new ArrayList<>(messages);
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

    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public int getMessageCount() {
        return messages.size();
    }

}
