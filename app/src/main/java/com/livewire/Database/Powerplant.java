package com.livewire.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.livewire.DAO.DynamoResponseDao;
import com.livewire.Entity.DynamoResponse;

@Database(
        entities = {
                DynamoResponse.class
        },
        version = 2,
        exportSchema = false
)

public abstract class Powerplant extends RoomDatabase {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    // Makes DAO accessible through the database
    public abstract DynamoResponseDao dynamoResponseDao();
}
