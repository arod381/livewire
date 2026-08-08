package com.example.d308_mobile_application_development_android.UI;

import android.app.DatePickerDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308_mobile_application_development_android.R;
import com.example.d308_mobile_application_development_android.UI.ExcursionAdapter;
import com.example.d308_mobile_application_development_android.UI.ExcursionDetails;
import com.example.d308_mobile_application_development_android.database.Repository;
import com.example.d308_mobile_application_development_android.entities.Excursion;
import com.example.d308_mobile_application_development_android.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationDetails extends AppCompatActivity {

    private int vacationID;
    private String vacationName;
    private EditText editName;
    private EditText editHotel;
    private EditText editStartDate;
    private EditText editEndDate;
    private EditText editPrice;
    private ExecutorService executorService;
    private Repository repository;

    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();
    private ExcursionAdapter excursionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        vacationName = getIntent().getStringExtra("vacationName");
        editName = findViewById(R.id.titletext);
        editHotel = findViewById(R.id.hoteltext);
        editStartDate = findViewById(R.id.startDate);
        editEndDate = findViewById(R.id.endDate);
        editPrice = findViewById(R.id.pricetext);
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        executorService = Executors.newSingleThreadExecutor();
        repository = new Repository(getApplication());

        excursionAdapter = new ExcursionAdapter(VacationDetails.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(VacationDetails.this));
        recyclerView.setAdapter(excursionAdapter);
        initializeDatePickers();

        vacationID = getIntent().getIntExtra("vacationID", -1);
        if (vacationID != -1) {
            fetchAndPopulateVacationDetails(vacationID);
        }

        // Fetch excursions associated with the vacation without LiveData
        fetchExcursions(vacationID);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
            intent.putExtra("vacationID", vacationID);
            startActivity(intent);
        });
    }

    private void initializeDatePickers() {
        DatePickerDialog.OnDateSetListener startDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, monthOfYear);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(editStartDate, myCalendarStart);
        };

        DatePickerDialog.OnDateSetListener endDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(editEndDate, myCalendarEnd);
        };

        editStartDate.setOnClickListener(v -> showDatePickerDialog(editStartDate, myCalendarStart, startDate));
        editEndDate.setOnClickListener(v -> showDatePickerDialog(editEndDate, myCalendarEnd, endDate));
    }

    private void fetchAndPopulateVacationDetails(int vacationID) {
        // Fetch vacation details in a background thread using ExecutorService
        new Thread(() -> {
            Vacation vacation = repository.getVacationById(vacationID);
            runOnUiThread(() -> {
                if (vacation != null) {
                    populateFields(vacation);
                } else {
                    Toast.makeText(VacationDetails.this, "Could not find vacation details. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void fetchExcursions(int vacationID) {
        new Thread(() -> {
            List<Excursion> excursions = repository.getAssociatedExcursions(vacationID);
            runOnUiThread(() -> {
                List<Excursion> filteredExcursions = new ArrayList<>();
                for (Excursion e : excursions) {
                    if (e.getVacationID() == vacationID) {
                        filteredExcursions.add(e);
                    }
                }
                excursionAdapter.setExcursions(filteredExcursions);
            });
        }).start();
    }

    private void showDatePickerDialog(TextView dateField, Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        String info = dateField.getText().toString();
        if (info.isEmpty()) info = "2024-12-12";
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(info));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(TextView textView, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        textView.setText(sdf.format(calendar.getTime()));
    }

    private void populateFields(Vacation vacation) {
        editName.setText(vacation.getVacationName());
        editHotel.setText(vacation.getHotel());
        editStartDate.setText(vacation.getStartDate());
        editEndDate.setText(vacation.getEndDate());
        editPrice.setText(String.valueOf(vacation.getPrice()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            deleteVacation();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            saveVacation();
            return true;
        }
        if (item.getItemId() == R.id.share) {
            shareVacationDetails();
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

            if (editStartDate.getText().toString().equals(today)) {
                try {
                    String dateFromScreen = editStartDate.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date myDate = sdf.parse(dateFromScreen);
                    Long trigger = myDate.getTime();

                    Intent intent = new Intent(VacationDetails.this, CReceiver.class);
                    intent.putExtra("key", editName.getText().toString() + " is starting");
                    PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, vacationID * 10 + 1, intent, PendingIntent.FLAG_IMMUTABLE);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to set start date notification.", Toast.LENGTH_SHORT).show();
                }
            }

            if (editEndDate.getText().toString().equals(today)) {
                try {
                    String dateFromScreen = editEndDate.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date myDate = sdf.parse(dateFromScreen);
                    Long trigger = myDate.getTime();

                    Intent intent = new Intent(VacationDetails.this, CReceiver.class);
                    intent.putExtra("key", editName.getText().toString() + " is ending");
                    PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, vacationID * 10 + 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to set end date notification.", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveVacation() {
        // Collect vacation details from UI
        String name = editName.getText().toString();
        String hotel = editHotel.getText().toString();
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();
        double price;

        // Validate that the vacation name is not empty
        if (name.isEmpty()) {
            Toast.makeText(this, "Vacation name cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            price = Double.parseDouble(editPrice.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that end date is not before start date
        if (isEndDateBeforeStartDate(startDate, endDate)) {
            Toast.makeText(this, "End date cannot be before start date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (vacationID == -1) {
            // Generate a new vacation ID
            new Thread(() -> {
                Integer nextId = repository.getMaxVacationId();
                if (nextId == null) {
                    nextId = 0; // Default to 0 if no vacations exist
                }
                vacationID = nextId + 1;

                Vacation newVacation = new Vacation(vacationID, name, hotel, startDate, endDate, price);

                // Insert the new vacation into the database
                repository.insert(newVacation);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Vacation added successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        } else {
            // Update the existing vacation
            Vacation existingVacation = new Vacation(vacationID, name, hotel, startDate, endDate, price);
            new Thread(() -> {
                repository.update(existingVacation);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Vacation updated successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        }
    }

    // Helper method to check if end date is before start date
    private boolean isEndDateBeforeStartDate(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return end != null && start != null && end.before(start);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // In case of error parsing dates, do not block save
        }
    }


    private void deleteVacation() {
        executorService.execute(() -> {
            List<Excursion> excursions = repository.getAssociatedExcursions(vacationID);
            runOnUiThread(() -> {
                if (excursions != null && !excursions.isEmpty()) {
                    Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
                } else {
                    executorService.execute(() -> repository.deleteVacation(vacationID));
                    fetchAndPopulateVacationDetails(vacationID);
                    finish();
                }
            });
        });
    }

    private void shareVacationDetails() {
        // Collect vacation details
        Vacation vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString(), Double.parseDouble(editPrice.getText().toString()));

        // Check if vacation details are complete
        if (vacation.getVacationName().isEmpty() || vacation.getHotel().isEmpty() || vacation.getStartDate().isEmpty() || vacation.getEndDate().isEmpty()) {
            Toast.makeText(this, "Some vacation details are missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch excursions for this vacation
        new Thread(() -> {
            List<Excursion> excursions = repository.getAssociatedExcursions(vacationID);
            runOnUiThread(() -> {
                StringBuilder vacationDetails = new StringBuilder();
                vacationDetails.append("Vacation: ").append(vacation.getVacationName()).append("\n")
                        .append("Hotel: ").append(vacation.getHotel()).append("\n")
                        .append("Start Date: ").append(vacation.getStartDate()).append("\n")
                        .append("End Date: ").append(vacation.getEndDate()).append("\n")
                        .append("Price: $").append(vacation.getPrice()).append("\n\n");

                if (excursions != null && !excursions.isEmpty()) {
                    vacationDetails.append("Excursion: ");
                    for (Excursion excursion : excursions) {
                        vacationDetails.append(excursion.getExcursionName())
                                .append("\nDate: ").append(excursion.getStartDate()).append("\n")
                                .append("Price: $").append(excursion.getPrice()).append("\n");
                    }
                } else {
                    vacationDetails.append("No excursions associated with this vacation.\n");
                }

                try {
                    // Prepare to share vacation and excursion details
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation and Excursion Details");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails.toString());
                    startActivity(Intent.createChooser(sendIntent, "Share vacation and excursion details"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to share vacation and excursion details.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
