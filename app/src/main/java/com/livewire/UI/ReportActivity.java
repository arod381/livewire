package com.livewire.UI;

import android.os.Build;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.livewire.Model.ApplicationDiagnostics;
import com.livewire.Model.DiagnosticEvent;
import com.livewire.Model.DiagnosticStatistics;
import com.livewire.R;
import com.livewire.ViewModel.MainViewModel;

public class ReportActivity extends AppCompatActivity {

    private ApplicationDiagnostics getApplicationDiagnostics() {

        ApplicationDiagnostics diagnostics =
                new ApplicationDiagnostics();

        try {

            PackageInfo packageInfo =
                    getPackageManager().getPackageInfo(
                            getPackageName(),
                            0
                    );

            diagnostics.setAppVersion(
                    packageInfo.versionName
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                diagnostics.setVersionCode(
                        (int) packageInfo.getLongVersionCode()
                );

            } else {

                diagnostics.setVersionCode(
                        packageInfo.versionCode
                );
            }

        } catch (PackageManager.NameNotFoundException e) {

            diagnostics.setAppVersion("Unknown");
            diagnostics.setVersionCode(-1);
        }

        diagnostics.setAndroidVersion(
                Build.VERSION.RELEASE
        );

        diagnostics.setDeviceModel(
                Build.MANUFACTURER +
                        " " +
                        Build.MODEL
        );

        return diagnostics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ApplicationDiagnostics app =
                getApplicationDiagnostics();

        MainViewModel viewModel =
                new ViewModelProvider(this)
                        .get(MainViewModel.class);

        TextView diagnosticsText =
                findViewById(R.id.diagnostics_text);

        viewModel.getDiagnostics().observe(this, diagnostics -> {
            if (diagnostics == null) {
                diagnosticsText.setText(
                        "Unable to retrieve diagnostics"
                );
                return;
            }

            String text =

                    "APPLICATION\n" +
                    "Version: " +
                    app.getAppVersion() +
                    "\n" +
                    "Version Code: " +
                    app.getVersionCode() +
                    "\n" +
                    "Android: " +
                    app.getAndroidVersion() +
                    "\n" +
                    "Device: " +
                    app.getDeviceModel() +
                    "\n\n" +

                    "Server\n" +
                    "Status: " +
                    diagnostics.getServerStatus() +
                    "\n" +
                    "Uptime: " +
                    diagnostics.getUptimeSeconds() +
                    " seconds\n\n" +

                    "MODEL \n" +
                            "Name: " +
                            diagnostics.getModelName() +
                            "\n" +
                            "Temperature: " +
                            diagnostics.getTemperature() +
                            "\n" +
                            "Top P: " +
                            diagnostics.getTopP() +
                            "\n" +
                            "Top K: " +
                            diagnostics.getTopK() +
                            "\n" +
                            "Max Tokens: " +
                            diagnostics.getMaxTokens();

            text += "\n\nRECENT EVENTS\n";

            if (diagnostics.getEvents() == null ||
                    diagnostics.getEvents().isEmpty()) {

                text += "No diagnostic events recorded.";

            } else {

                for (DiagnosticEvent event :
                        diagnostics.getEvents()) {

                    text += "\n" +
                            event.getType() +
                            "\n" +
                            event.getDetails() +
                            "\n" +
                            event.getDurationMs() +
                            " ms\n";
                }

                DiagnosticStatistics statistics =
                        diagnostics.getStatistics();

                text += "\n\nPERFORMANCE\n";

                if (statistics != null) {

                    text +=
                            "Total Requests: " +
                                    statistics.getTotalRequests() +
                                    "\n" +

                                    "Successful: " +
                                    statistics.getSuccessfulRequests() +
                                    "\n" +

                                    "Failed: " +
                                    statistics.getFailedRequests() +
                                    "\n" +

                                    "Network Errors: " +
                                    statistics.getNetworkErrors() +
                                    "\n" +

                                    "HTTP Errors: " +
                                    statistics.getHttpErrors() +
                                    "\n" +

                                    "Parse Errors: " +
                                    statistics.getParseErrors() +
                                    "\n" +

                                    "Average Response: " +
                                    statistics.getAverageResponseTimeMs() +
                                    " ms\n" +

                                    "Slowest Response: " +
                                    statistics.getSlowestResponseMs() +
                                    " ms";
                }
            }

            diagnosticsText.setText(text);
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