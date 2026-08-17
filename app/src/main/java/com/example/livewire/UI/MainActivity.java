package com.example.livewire.UI;

import com.example.livewire.ViewModel.MainViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.livewire.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livewire.Adapter.ChatAdapter;
import com.example.livewire.Model.ChatMessage;

import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MainViewModel viewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        Spinner contextSpinner =
                findViewById(R.id.context_spinner);

        contextSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        switch (position) {

                            case 0:
                                viewModel.setContextLimit(0);
                                break;

                            case 1:
                                viewModel.setContextLimit(4);
                                break;

                            case 2:
                                viewModel.setContextLimit(10);
                                break;

                            case 3:
                                viewModel.setContextLimit(20);
                                break;

                            case 4:
                                viewModel.setContextLimit(50);
                                break;

                            case 5:
                                viewModel.setContextLimit(-1);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        EditText editText = findViewById(R.id.edit_text_id);

        ImageButton button = findViewById(R.id.button);

        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // Observe loading state
        viewModel.getLoading().observe(this, isLoading -> {
            button.setEnabled(!isLoading);

            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        RecyclerView chatRecyclerView =
                findViewById(R.id.chat_recycler_view);

        ChatAdapter chatAdapter =
                new ChatAdapter(new ArrayList<>());

        chatRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        chatRecyclerView.setAdapter(chatAdapter);

        viewModel.getConversation().observe(this, messages -> {
            chatAdapter.setMessages(messages);
        });

        // Submit prompt
        button.setOnClickListener(v -> {
            String prompt = editText.getText().toString();

            if(!prompt.isEmpty()) {
                viewModel.submitPrompt(prompt);
                editText.setText("");
            }
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