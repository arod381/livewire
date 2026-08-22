// Package location for adapter classes
// Adapters connect data sources (models) to UI components like RecyclerViews
package com.livewire.Adapter;

// Android UI classes used for creating and managing list item views
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// RecyclerView framework classes
// RecyclerView efficiently displays large lists by recycling item views
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Application-specific imports
import com.livewire.Model.ChatMessage;
import com.livewire.R;

// Java List collection used to store chat messages
import java.util.List;

/**
 * Adapter responsible for displaying chat messages inside a RecyclerView

 * Responsibilities:
 * - Receives a list of ChatMessage objects
 * - Creates chat message item views
 * - Places messages text into each RecyclerView row
 * - Updates the UI when new messages are provided
 */
public class ChatAdapter
        extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // Stores the current collection of messages displayed by the RecyclerView
    private List<ChatMessage> messages;

    /**
     * Constructor

     * @param messages Initial list of chat messages to display
     */
    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    /**
     * Replaces the current message list with a new list

     * notifyDataSetChanged() tells RecyclerView that the data changed
     * and it should redraw visible items

     * @param messages Updated chat message list
     */
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;

        // Refresh the RecyclerView display
        notifyDataSetChanged();
    }

    /**
     * Creates a new ViewHolder when RecyclerView needs a new row

     * RecyclerView does not create a view for every message
     * It creates only enough views to fill the screen and reuses them.

     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     */
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        // Convert the XML Layout file into an actual view object
        // chat_message.xml defines the appearance of each chat message row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message, parent, false);

        // Wrap the view inside a chatViewHolder
        return new ChatViewHolder(view);
    }

    /**
     * Connects the data from the message list to the UI

     * This method is called whenever a RecyclerView row needs data
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set. ViewHolder containing row views
     * @param position The position of the item within the adapter's data set. Position of the
     *                 message in the list
     */

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder,
            int position) {

        // Retrieve the ChatMessage object for this row
        ChatMessage message = messages.get(position);

        // Debug logging
        // Useful for confirming that the messages are reaching the adapter
        // and being displayed at the expected positions
        android.util.Log.d(
                "LiveWire",
                "BIND MESSAGE " + position + ": " + message.getMessage()
        );

        // Put the message text into the TextView displayed on the screen
        holder.messageText.setText(message.getMessage());
    }

    /**
     * Returns the number of items RecyclerView should display

     * RecyclerView uses this value to determine how many rows exists
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * ViewHolder class
     * Holds references to the views inside a single RecyclerView row
     * Instead of repeatedly searching for views with findViewById()
     * RecyclerView stores these references for faster scrolling
     */
    public static class ChatViewHolder
            extends RecyclerView.ViewHolder {

        // TextView where the chat message content is displayed
        TextView messageText;

        // Constructor
        // Finds UI elements inside the chat\_message.xml layout
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            // Connect the Java variable to the TextView defined in XML
            messageText = itemView.findViewById(
                    R.id.chat_message_text
            );
        }
    }
}
