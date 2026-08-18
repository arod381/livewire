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
import androidx.viewpager2.widget.ViewPager2;

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

        ViewPager2 viewPager =
                findViewById(R.id.view_pager);

        viewPager.setLayoutDirection(
                View.LAYOUT_DIRECTION_LTR
        );

        MainPagerAdapter adapter =
                new MainPagerAdapter(this);

        viewPager.setAdapter(adapter);


        MainViewModel viewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

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

        chatRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        ChatAdapter chatAdapter =
                new ChatAdapter(new ArrayList<>());

        chatRecyclerView.setAdapter(chatAdapter);

        viewModel.getConversation().observe(this, messages -> {
            android.util.Log.d(
                    "LiveWire",
                    "CONVERSATION UPDATED: " + messages.size()
            );
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
    }
}