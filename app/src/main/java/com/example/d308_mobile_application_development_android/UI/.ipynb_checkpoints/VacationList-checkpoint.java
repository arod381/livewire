package com.example.d308_mobile_application_development_android.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308_mobile_application_development_android.R;
import com.example.d308_mobile_application_development_android.database.Repository;
import com.example.d308_mobile_application_development_android.entities.Excursion;
import com.example.d308_mobile_application_development_android.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private VacationAdapter vacationAdapter;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Set up Floating Action Button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(intent);
        });

        // Set up RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all vacations in the background using ExecutorService
        repository = new Repository(getApplication());
        executorService.execute(() -> {
            List<Vacation> vacations = repository.getAllVacations();  // Retrieve data synchronously
            runOnUiThread(() -> vacationAdapter.setVacations(vacations));  // Update UI on main thread
        });

        // Handle window insets for a clean edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mysample) {
            // Add sample data to the database
            addSampleData();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    private void addSampleData() {
        // Inserting sample vacation and excursion data in the background
        executorService.execute(() -> {
            Vacation vacation = new Vacation(-4, "Bermuda Trip", "Sandy Beach", "2024-12-16", "2025-01-31", 100.0);
            repository.insert(vacation);
            vacation = new Vacation(-3, "Spring Break", "Hilton", "2024-12-16", "2025-01-31", 100.0);
            repository.insert(vacation);
            vacation = new Vacation(-2, "London Trip", "Tower", "2024-12-16", "2025-01-31", 100.0);
            repository.insert(vacation);

            Excursion excursion = new Excursion(0, "Snorkeling", "2024-12-16", 10.0, -4);
            repository.insert(excursion);
            excursion = new Excursion(0, "Hiking", "2024-12-16", 10.0, -4);
            repository.insert(excursion);
            excursion = new Excursion(0, "Bus Tour", "2024-12-16", 10.0, -3);
            repository.insert(excursion);
            excursion = new Excursion(0, "Cooking Lesson", "2024-12-16", 10.0, -2);
            repository.insert(excursion);

            runOnUiThread(() -> Toast.makeText(this, "Sample data added!", Toast.LENGTH_SHORT).show());
            onResume();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch the updated data after adding the sample data
        executorService.execute(() -> {
            List<Vacation> vacations = repository.getAllVacations();  // Retrieve data synchronously
            runOnUiThread(() -> vacationAdapter.setVacations(vacations));  // Update UI on main thread
        });
    }
}
