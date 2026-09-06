package com.livewire.Model;

import java.util.ArrayList;
import java.util.List;

public class ConversationSnapshot {

    private final String modelId;
    private final int contextLimit;
    private final List<ChatMessage> messages;

    public ConversationSnapshot(
            String modelId,
            int contextLimit,
            List<ChatMessage> messages) {

        this.modelId = modelId;
        this.contextLimit = contextLimit;

        // Create a copy so the snapshot does not change
        this.messages = new ArrayList<>(messages);
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
