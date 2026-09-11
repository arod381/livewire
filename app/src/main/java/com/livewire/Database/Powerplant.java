package com.livewire.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `diagnostic_request_records` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`timestamp` INTEGER NOT NULL, " +
                            "`modelId` TEXT, " +
                            "`contextLimit` INTEGER NOT NULL, " +
                            "`messageCount` INTEGER NOT NULL, " +
                            "`temperature` REAL NOT NULL, " +
                            "`topP` REAL NOT NULL, " +
                            "`topK` INTEGER NOT NULL, " +
                            "`maxTokens` INTEGER NOT NULL, " +
                            "`responseTimeMs` INTEGER NOT NULL, " +
                            "`successful` INTEGER NOT NULL, " +
                            "`errorType` TEXT" +
                            ")"
            );
        }
    };

    // Makes DAO accessible through the database
    public abstract DynamoResponseDao dynamoResponseDao();

    // Makes DAO accessible through the database
    public abstract DiagnosticRequestRecordDao diagnosticRequestRecordDao();
}
