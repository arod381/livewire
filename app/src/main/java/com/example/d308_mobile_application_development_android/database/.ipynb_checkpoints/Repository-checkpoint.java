package com.example.d308_mobile_application_development_android.database;

import android.app.Application;

import androidx.room.Query;

import com.example.d308_mobile_application_development_android.dao.ExcursionDAO;
import com.example.d308_mobile_application_development_android.dao.VacationDAO;
import com.example.d308_mobile_application_development_android.entities.Excursion;
import com.example.d308_mobile_application_development_android.entities.Vacation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private final VacationDAO vacationDAO;
    private final ExcursionDAO excursionDAO;
    private final ExecutorService executorService;

    public Repository(Application application) {
        // Initialize the DAOs and ExecutorService
        cDatabaseBuilder db = cDatabaseBuilder.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
        executorService = Executors.newFixedThreadPool(2); // Simple thread pool for async operations
    }

    // Vacation Methods
    public List<Vacation> getAllVacations() {
        try {
            return executorService.submit(() -> vacationDAO.getAllVacations()).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error or return empty list
        }
    }

    public Vacation getVacationById(int vacationID) {
        try {
            return executorService.submit(() -> vacationDAO.getVacationById(vacationID)).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error
        }
    }

    public Integer getMaxVacationId() {
        try {
            return executorService.submit(() -> vacationDAO.getMaxVacationId()).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error
        }
    }

    public void insert(Vacation vacation) {
        executorService.execute(() -> vacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        executorService.execute(() -> vacationDAO.update(vacation));
    }

    public void save(Vacation vacation) {
        executorService.execute(() -> vacationDAO.upsert(vacation));
    }

    public void delete(Vacation vacation) {
        executorService.execute(() -> vacationDAO.delete(vacation));
    }

    public void deleteVacationById(int vacationID) {
        executorService.execute(() -> vacationDAO.deleteVacationById(vacationID));
    }

    public List<Vacation> getVacationsWithinDates(String startDate, String endDate) {
        try {
            return executorService.submit(() -> vacationDAO.getVacationsWithinDates(startDate, endDate)).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error
        }
    }

    public List<Vacation> getVacationsBetweenDates(String startDate, String endDate) {
        try {
            return executorService.submit(() -> vacationDAO.getVacationsBetweenDates(startDate, endDate)).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error
        }
    }

    // Excursion Methods
    public void insert(Excursion excursion) {
        executorService.execute(() -> excursionDAO.insert(excursion));
    }

    public void update(Excursion excursion) {
        executorService.execute(() -> excursionDAO.update(excursion));
    }

    public void save(Excursion excursion) {
        executorService.execute(() -> excursionDAO.upsert(excursion));
    }

    public List<Excursion> getExcursionsByVacationId(int vacationID) {
        // Synchronously fetch the data in the background thread
        try {
            return executorService.submit(() -> excursionDAO.getExcursionsByVacationId(vacationID)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on failure
        }
    }

    public Integer getMaxExcursionId() {
        try {
            return executorService.submit(() -> excursionDAO.getMaxExcursionId()).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error
        }
    }

    // Method to get excursions associated with a vacation ID
    public List<Excursion> getAssociatedExcursions(int vacationID) {
        return excursionDAO.getExcursionsByVacationId(vacationID);
    }

    public Excursion getExcursionById(int excursionID) {
        return excursionDAO.getExcursionById(excursionID);
    }

    public void deleteExcursionById(int excursionID) {
        executorService.execute(() -> excursionDAO.deleteExcursionById(excursionID));
    }

    public void deleteExcursion(Excursion excursion) {
        executorService.execute(() -> excursionDAO.deleteExcursion(excursion));
    }

    public void deleteVacation(int vacationID) {
        executorService.execute(() -> vacationDAO.deleteVacationById(vacationID));
    }

    public List<Excursion> getAllExcursions() {
        try {
            return executorService.submit(() -> excursionDAO.getAllExcursions()).get();  // Blocking until result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle error or return empty list
        }
    }
}
