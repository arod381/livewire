package com.livewire.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.livewire.Model.ChatMessage;
import com.example.livewire.R;

import java.util.List;

public class ChatAdapter
        extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder,
            int position) {

        ChatMessage message = messages.get(position);

        android.util.Log.d(
                "LiveWire",
                "BIND MESSAGE " + position + ": " + message.getMessage()
        );

        holder.messageText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder
            extends RecyclerView.ViewHolder {

        TextView messageText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(
                    R.id.chat_message_text
            );
        }
    }
}
