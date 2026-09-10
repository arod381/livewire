package com.livewire.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.livewire.Entity.DiagnosticRequestRecord;
import com.livewire.Entity.DynamoResponse;

import java.util.List;

@Dao
public interface DiagnosticRequestRecordDao {


    // Database operation - Save a Diagnostic Request Record
    @Insert
    void insert(DiagnosticRequestRecord record);

    // Database operation - Retrieve saved Diagnostic Request Record
    @Query("SELECT * FROM diagnostic_request_records ORDER BY timestamp DESC")
    List<DiagnosticRequestRecord> getAll();

    // Database operation - Remove a Diagnostic Request Record
    @Delete
    void delete(DiagnosticRequestRecord record);

    // Database operation - Clear Diagnostic Request Records
    @Query("DELETE FROM diagnostic_request_records")
    void clearAll();
}
