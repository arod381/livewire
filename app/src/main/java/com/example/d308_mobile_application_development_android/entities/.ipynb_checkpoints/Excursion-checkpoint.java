package com.example.d308_mobile_application_development_android.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "excursions")
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    private int excursionID;
    private String excursionName;
    private double price;
    private int vacationID;
    private String vacationName; // Represents vacation name
    private String startDate;

    // Constructor for all necessary fields
    public Excursion(int excursionID, String excursionName, String startDate, double price, int vacationID) {
        this.excursionID = excursionID;
        this.excursionName = excursionName;
        this.startDate = startDate;
        this.price = price;
        this.vacationID = vacationID;
    }

    // Optional constructor for vacationName (if needed)
    @Ignore
    public Excursion(String vacationName) {
        this.vacationName = vacationName;
    }

    // Getters and Setters
    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getExcursionName() {
        return excursionName;
    }

    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getVacationName() {
        return vacationName;
    }

    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }

}
