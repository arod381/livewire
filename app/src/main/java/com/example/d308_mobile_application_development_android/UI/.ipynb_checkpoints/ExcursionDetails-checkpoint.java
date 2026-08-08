package com.example.d308_mobile_application_development_android.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308_mobile_application_development_android.R;
import com.example.d308_mobile_application_development_android.database.Repository;
import com.example.d308_mobile_application_development_android.entities.Excursion;
import com.example.d308_mobile_application_development_android.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcursionDetails extends AppCompatActivity {

    private EditText editName, editPrice, editStartDate;
    private Repository repository;
    private Calendar myCalendarStart = Calendar.getInstance();
    private int excursionID, vacationID;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        repository = new Repository(getApplication());
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        editName = findViewById(R.id.excursionName);
        editPrice = findViewById(R.id.excursionPrice);
        editStartDate = findViewById(R.id.startDate);

        // Retrieve IDs from Intent
        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);

        // Get excursion name and price from Intent and populate fields
        Intent intent = getIntent();
        String excursionName = intent.getStringExtra("name");
        double price = intent.getDoubleExtra("price", 0.0);

        // Set the name and price in the corresponding EditText fields
        editName.setText(excursionName);
        editPrice.setText(String.valueOf(price));

        // Fetch and populate excursion details if editing an existing excursion
        if (excursionID != -1) {
            fetchAndPopulateExcursionDetails(excursionID);
        }

        // Date Picker listener
        editStartDate.setOnClickListener(v -> showDatePickerDialog(editStartDate, myCalendarStart));
    }

    private void fetchAndPopulateExcursionDetails(int excursionID) {
        executorService.execute(() -> {
            Excursion excursion = repository.getExcursionById(excursionID);
            runOnUiThread(() -> {
                if (excursion != null) {
                    populateFields(excursion);
                }
            });
        });
    }

    private void populateFields(Excursion excursion) {
        editName.setText(excursion.getExcursionName());
        editPrice.setText(String.valueOf(excursion.getPrice()));
        editStartDate.setText(excursion.getStartDate());
    }

    private void showDatePickerDialog(EditText dateField, Calendar calendar) {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            updateLabel(dateField, calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(EditText dateField, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateField.setText(sdf.format(calendar.getTime()));
    }

    private boolean isDateWithinVacationRange(Vacation vacation, String startDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date vacationStart = sdf.parse(vacation.getStartDate());
            Date vacationEnd = sdf.parse(vacation.getEndDate());
            Date excursionStart = sdf.parse(startDate);

            return !excursionStart.before(vacationStart) && !excursionStart.after(vacationEnd);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {  // Back button in the toolbar
            finish();  // Close ExcursionDetails and go back
            return true;
        }
        if (id == R.id.excursiondelete) {
            deleteExcursion();
            return true;
        }
        if (id == R.id.excursionsave) {
            saveExcursion();
            return true;
        }
        if (id == R.id.excursionshare) {
            shareExcursionDetails();
            return true;
        }
        if (item.getItemId() == R.id.excursionnotify) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            try {
                String dateFromScreen = editStartDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date myDate = sdf.parse(dateFromScreen);
                Long trigger = myDate.getTime();

                Intent intent = new Intent(ExcursionDetails.this, CReceiver.class);
                intent.putExtra("key", editName.getText().toString() + " is starting");
                intent.putExtra("excursion_channel", true);
                PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, excursionID * 10 + 1, intent, PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to set start date notification.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveExcursion() {
        executorService.execute(() -> {
            Vacation vacation = repository.getVacationById(vacationID);
            if (vacation != null) {
                String startDate = editStartDate.getText().toString();
                String name = editName.getText().toString();
                double price;

                // Validate that the excursion name is not empty
                if (name.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Excursion name cannot be empty.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Validate price input
                try {
                    price = Double.parseDouble(editPrice.getText().toString());
                } catch (NumberFormatException e) {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Check if date is within vacation range
                if (isDateWithinVacationRange(vacation, startDate)) {
                    Excursion excursion = new Excursion(
                            excursionID == -1 ? generateNewExcursionID() : excursionID,
                            name,
                            startDate,
                            price,
                            vacationID
                    );

                    if (excursionID == -1) {
                        // Insert new excursion
                        repository.insert(excursion);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Excursion added successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        // Update existing excursion
                        repository.update(excursion);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Excursion updated successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                } else {
                    // Notify user if the date is outside vacation range
                    runOnUiThread(() -> Toast.makeText(this, "Excursion date must be within vacation dates.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Helper method to generate a unique Excursion ID
    private int generateNewExcursionID() {
        Integer nextId = repository.getMaxExcursionId();
        return (nextId == null ? 1 : nextId + 1); // Generate new ID
    }


    private void deleteExcursion() {
        executorService.execute(() -> {
            // Fetch the Excursion object by its ID
            Excursion excursion = repository.getExcursionById(excursionID);

            if (excursion != null) {
                // Call the repository method to delete the excursion
                repository.deleteExcursion(excursion);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Excursion not found", Toast.LENGTH_SHORT).show());
            }

        });
    }


    private void shareExcursionDetails() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Excursion Details:\n" +
                "Name: " + editName.getText().toString() + "\n" +
                "Price: " + editPrice.getText().toString() + "\n" +
                "Start Date: " + editStartDate.getText().toString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}
