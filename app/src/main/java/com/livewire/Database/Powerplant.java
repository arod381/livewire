package com.livewire.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.livewire.DAO.DiagnosticRequestRecordDao;
import com.livewire.DAO.DynamoResponseDao;
import com.livewire.Entity.DiagnosticRequestRecord;
import com.livewire.Entity.DynamoResponse;

@Database(
        entities = {
                DynamoResponse.class,
                DiagnosticRequestRecord.class
        },
        version = 2,
        exportSchema = false
)

public abstract class Powerplant extends RoomDatabase {

    // Makes DAO accessible through the database
    public abstract DynamoResponseDao dynamoResponseDao();

    public abstract DiagnosticRequestRecordDao diagnosticRequestRecordDao();
}
