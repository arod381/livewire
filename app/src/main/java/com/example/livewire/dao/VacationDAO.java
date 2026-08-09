package com.example.livewire.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.livewire.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Vacation vacation);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY vacationID ASC")
    List<Vacation> getAllVacations();

    @Query("SELECT MAX(vacationID) FROM vacations")
    Integer getMaxVacationId();

    @Query("DELETE FROM vacations WHERE vacationID = :id")
    void deleteVacationById(int id);

    @Query("SELECT * FROM vacations WHERE startDate BETWEEN :startDate AND :endDate")
    List<Vacation> getVacationsWithinDates(String startDate, String endDate);

    @Query("SELECT * FROM vacations WHERE startDate >= :startDate AND endDate <= :endDate")
    List<Vacation> getVacationsBetweenDates(String startDate, String endDate);

    @Query("SELECT * FROM vacations WHERE vacationID = :id")
    Vacation getVacationById(int id);
}
