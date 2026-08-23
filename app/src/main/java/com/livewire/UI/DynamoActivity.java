package com.livewire.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.livewire.Adapter.DynamoAdapter;
import com.livewire.R;
import com.livewire.ViewModel.MainViewModel;

public class DynamoActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_dynamo
        );

        MainViewModel viewModel =
                new ViewModelProvider(this)
                        .get(MainViewModel.class);

        RecyclerView recyclerView =
                findViewById(
                        R.id.dynamo_recycler_view
                );

        DynamoAdapter adapter =
                new DynamoAdapter();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        viewModel.getDynamos()
                .observe(
                        this,
                        responses ->
                                adapter.setResponses(
                                        responses
                                )
                );

        viewModel.loadDynamos();
    }
}