package com.livewire.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.livewire.Entity.DynamoResponse;

import java.util.List;

@Dao
public interface DynamoResponseDao {

    // Database operation - Save a favorite
    @Insert
    void insert(DynamoResponse response);

    // Database operation - Remove a favorite
    @Delete
    void delete(DynamoResponse response);

    // Database operation - Retrieve saved favorites
    @Query("SELECT * FROM dynamo_responses ORDER BY timestamp DESC")
    List<DynamoResponse> getAll();

    // Database operation - Clear favorites
    @Query("DELETE FROM dynamo_responses")
    void deleteAll();
}
