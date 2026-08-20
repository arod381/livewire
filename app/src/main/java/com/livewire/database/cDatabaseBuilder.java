package com.livewire.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.livewire.dao.ExcursionDAO;
import com.livewire.dao.VacationDAO;
import com.livewire.entities.Excursion;
import com.livewire.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 4, exportSchema = false)
public abstract class cDatabaseBuilder extends RoomDatabase {

    // Abstract methods to access DAOs
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();

    // Singleton instance
    private static volatile cDatabaseBuilder INSTANCE;

    /**
     * Returns the singleton instance of the database, creating it if necessary.
     * @param context Application context
     * @return Singleton instance of cDatabaseBuilder
     */
    public static cDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (cDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    cDatabaseBuilder.class, "CtheWorldDatabase.db")
                            .fallbackToDestructiveMigration() // Ensures the database can migrate safely
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
