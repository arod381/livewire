package com.example.livewire.UI;

import com.example.livewire.ViewModel.MainViewModel;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.livewire.R;

public class MainActivity extends AppCompatActivity {

    private String prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MainViewModel viewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        EditText editText = findViewById(R.id.edit_text_id);

        TextView responseText = findViewById(R.id.response_text_id);

        viewModel.getResponse().observe(this, response -> {
            responseText.setText(response);
        });

        // Setup button to navigate to VacationList
        ImageButton button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String prompt = editText.getText().toString();
            viewModel.submitPrompt(prompt);

            // Tell ViewModel about the prompt
            viewModel.getLoading().observe(this, isLoading -> {
                button.setEnabled(!isLoading);
            });
        });

        // Adjust window insets for Edge-to-Edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Report button to launch ReportActivity
        Button reportButton = findViewById(R.id.reportButton);
        reportButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        });
    }
}