package com.livewire.UI;

// Android classes used to create and manage the Fragment's view
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Annotations that indicate whether parameters/return values can be null
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// Base Fragment class provided by the AndroidX Fragment library
import androidx.fragment.app.Fragment;

// References the application's XML layouts and other resources
import com.example.livewire.R;

/**
 * Fragment responsible for displaying the chat screen

 * The UI for this Fragment is defined in fragment_chat.xml
 */
public class ChatFragment extends Fragment {

    /**
     * Creates and returns the view hierarchy for this Fragment
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the inflated chat layout
     */

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Inflate the chat screen layout
        // Passing 'false' prevents the layout from being attached to the container
        // immediately; the FragmentManager handles the attachment
        return inflater.inflate(
                R.layout.fragment_chat,
                container,
                false
        );
    }
}