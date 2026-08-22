package com.livewire.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Database entity user chose to persist
@Entity(tableName = "dynamo_responses")
public class DynamoResponse {

    @PrimaryKey(autoGenerate = true)

    // Room's unique identifier for the saved response
    private int id;

    // Actual AI-generated text user wants to keep
    private String response;

    // When response was saved
    private long timestamp;

    public DynamoResponse(String response, long timestamp) {
        this.response = response;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
