package com.livewire.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.livewire.Entity.DynamoResponse;
import com.livewire.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DynamoAdapter
        extends RecyclerView.Adapter<DynamoAdapter.ViewHolder> {

    private List<DynamoResponse> responses =
            new ArrayList<>();

    private final DeleteDynamoListener deleteListener;

    public interface DeleteDynamoListener {
        void onDelete(DynamoResponse response);
    }

    public DynamoAdapter(DeleteDynamoListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setResponses(
            List<DynamoResponse> responses) {

        this.responses = responses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.dynamo_response,
                parent,
                false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        DynamoResponse response =
                responses.get(position);

        holder.responseText.setText(
                response.getResponse()
        );

        String date =
                DateFormat.getDateTimeInstance()
                        .format(
                                new Date(
                                        response.getTimestamp()
                                )
                        );

        holder.timestamp.setText(date);

        holder.deleteButton.setOnClickListener(v ->
                deleteListener.onDelete(response)
        );
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView responseText;
        TextView timestamp;
        Button deleteButton;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            responseText =
                    itemView.findViewById(
                            R.id.dynamo_response_text
                    );

            timestamp =
                    itemView.findViewById(
                            R.id.dynamo_timestamp
                    );

            deleteButton =
                    itemView.findViewById(
                            R.id.delete_dynamo_button
                    );
        }
    }
}