package com.example.livewire.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livewire.R;
import com.example.livewire.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionItemView;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mExcursions != null) {
                        final Excursion current = mExcursions.get(position);
                        Intent intent = new Intent(context, ExcursionDetails.class);
                        // Passing excursion details as extras
                        intent.putExtra("excursionID", current.getExcursionID());
                        intent.putExtra("name", current.getExcursionName());
                        intent.putExtra("price", current.getPrice());
                        intent.putExtra("vacationID", current.getVacationID());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            String excursionName = current.getExcursionName();
            holder.excursionItemView.setText(excursionName);
        } else {
            holder.excursionItemView.setText("No excursion name");
        }
    }

    @Override
    public int getItemCount() {
        return mExcursions != null ? mExcursions.size() : 0;
    }

    public void setExcursions(List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }
}
