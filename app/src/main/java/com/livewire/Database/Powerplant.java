package com.livewire.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.livewire.DAO.DynamoResponseDao;
import com.livewire.Entity.DynamoResponse;

@Database(
        entities = {
                DynamoResponse.class
        },
        version = 1,
        exportSchema = false
)

public abstract class Powerplant extends RoomDatabase {

    // Makes DAO accessible through the database
    public abstract DynamoResponseDao dynamoResponseDao();
}
