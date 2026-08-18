package com.example.livewire.UI;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.livewire.R;
import com.example.livewire.ViewModel.MainViewModel;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        MainViewModel viewModel =
                new ViewModelProvider(this)
                        .get(MainViewModel.class);

        TextView diagnosticsText =
                findViewById(R.id.diagnostics_text);

        viewModel.getDiagnostics().observe(this, diagnostics -> {
            diagnosticsText.setText(diagnostics);
        });

        viewModel.loadDiagnostics();

        // Generate the report text
        StringBuilder report = new StringBuilder("Notification Report\n\n");
        if (CReceiver.notificationList.isEmpty()) {
            report.append("No notifications recorded yet.");
        } else {
            for (CReceiver.NotificationData data : CReceiver.notificationList) {
                report.append("ID: ").append(data.id)
                        .append("\nText: ").append(data.text)
                        .append("\nChannel: ").append(data.channel)
                        .append("\nTime: ").append(data.timestamp)
                        .append("\n\n");
            }
        }
    }
}