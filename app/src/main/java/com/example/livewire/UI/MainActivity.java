package com.example.livewire.UI;

// ViewModel used to manage and preserve UI-related data
import com.example.livewire.ViewModel.MainViewModel;
import androidx.lifecycle.ViewModelProvider;

// Used to navigate between Android activities
import android.content.Intent;

// Android lifecycle and UI classes
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

// Classes used for handling system window insets (Edge-to-Edge UI support)
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Application resources
import com.example.livewire.R;

// RecyclerView components for displaying lists of messages
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// ViewPager2 allows horizontal paging between different screens/fragments
import androidx.viewpager2.widget.ViewPager2;

// Adapter and model classes used by the chat UI
import com.example.livewire.Adapter.ChatAdapter;
import com.example.livewire.Model.ChatMessage;

import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity for the LiveWire application

 * This activity:
 * - Hosts the main ViewPager navigation
 * - Displays the chat conversation
 * - Handles user prompt submission
 * - Observes ViewModel data changes and updates the UI
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is created

     * Initializes:
     * - The main layout
     * - ViewPager navigation
     * - ViewModel
     * - Chat RecyclerView
     * - Loading state observers
     * - User input handling

     * @param savedInstanceState Previously saved activity state
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the activity layout XML file
        setContentView(R.layout.activity_main);

        // Initialize ViewPager used for navigating between app pages
        ViewPager2 viewPager =
                findViewById(R.id.view_pager);

        // Ensure pages are displayed left-to-right
        viewPager.setLayoutDirection(
                View.LAYOUT_DIRECTION_LTR
        );

        // Create and assign the adapter responsible for managing pages
        MainPagerAdapter adapter =
                new MainPagerAdapter(this);

        viewPager.setAdapter(adapter);

        // Obtain the ViewModel instance
        // ViewModel survives configuration changes such as screen rotation
        MainViewModel viewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        // Input field where users type their prompts/messages
        EditText editText = findViewById(R.id.edit_text_id);

        // Button used to submit a prompt
        ImageButton button = findViewById(R.id.button);

        // Displays loading progress while processing a request
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // Observe loading state
        // Updates the button and progress bar depending on whether
        // the application is processing a request
        viewModel.getLoading().observe(this, isLoading -> {

            // Prevent multiple submissions while loading
            button.setEnabled(!isLoading);

            if (isLoading) {
                // Show progress indicator
                progressBar.setVisibility(View.VISIBLE);
            } else {
                // Hide progress indicator when finished
                progressBar.setVisibility(View.GONE);
            }
        });

        // RecyclerView that displays chat messages
        RecyclerView chatRecyclerView =
                findViewById(R.id.chat_recycler_view);

        // Use a vertical list layout for chat messages
        chatRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Create adapter with an empty message list initially
        ChatAdapter chatAdapter =
                new ChatAdapter(new ArrayList<>());

        // Attach adapter to RecyclerView
        chatRecyclerView.setAdapter(chatAdapter);

        // Observe conversation updates from the ViewModel
        // Whenever new messages arrive, update the RecyclerView
        viewModel.getConversation().observe(this, messages -> {

            // Debug log showing when conversation data changes
            android.util.Log.d(
                    "LiveWire",
                    "CONVERSATION UPDATED: " + messages.size()
            );

            // Refresh displayed messages
            chatAdapter.setMessages(messages);
        });

        // Submit prompt
        button.setOnClickListener(v -> {

            // Retrieve text entered by the user
            String prompt = editText.getText().toString();

            // Only submit if the input is not empty
            if(!prompt.isEmpty()) {

                // Send the prompt to the ViewModel for processing
                viewModel.submitPrompt(prompt);

                // Clear the input field after submission
                editText.setText("");
            }
        });

        // Adjust window insets for Edge-to-Edge support
        // Apply system windows insets so the UI does not overlap
        // with status bars, navigation bars, or device cutouts
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            // Retrieve system bar dimensions
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Add padding around the main view
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // Return the consumed insets
            return insets;
        });
    }
}