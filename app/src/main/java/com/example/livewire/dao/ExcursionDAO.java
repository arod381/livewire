package com.example.livewire.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.livewire.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Excursion excursion);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void deleteExcursion(Excursion excursion);

    @Query("SELECT MAX(excursionID) FROM excursions")
    Integer getMaxExcursionId();

    @Query("SELECT * FROM excursions WHERE excursionID = :excursionID")
    Excursion getExcursionById(int excursionID);

    @Query("DELETE FROM excursions WHERE excursionID = :excursionID")
    void deleteExcursionById(int excursionID); // Deletes by excursionID

    @Query("SELECT * FROM excursions WHERE vacationID = :vacationID")
    List<Excursion> getExcursionsByVacationId(int vacationID);  // Return List directly, not LiveData

    @Query("SELECT * FROM excursions ORDER BY excursionID ASC")
        List<Excursion> getAllExcursions();
}
