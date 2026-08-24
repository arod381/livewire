package com.livewire.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.livewire.Model.AIModel;
import com.livewire.Model.ModelCatalog;
import com.livewire.R;
import com.livewire.ViewModel.MainViewModel;

import java.util.List;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_settings,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        MainViewModel viewModel =
                new ViewModelProvider(requireActivity())
                        .get(MainViewModel.class);

        Spinner modelSpinner =
                view.findViewById(
                        R.id.model_spinner
                );

        List<AIModel> models =
                ModelCatalog.getModels();

        ArrayAdapter<AIModel> adapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        models
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        modelSpinner.setAdapter(adapter);

        // MODEL SELECTION
        modelSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        AIModel selectedModel =
                                models.get(position);

                        viewModel.setSelectedModel(
                                selectedModel
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                        viewModel.setSelectedModel(null);
                    }
                }
        );

        Button dynamoButton =
                view.findViewById(R.id.dynamo_button);

        dynamoButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(requireContext(),
                            DynamoActivity.class
                    );

            startActivity(intent);
        });

        Button reportButton =
                view.findViewById(R.id.reportButton);

        reportButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(requireContext(),
                            ReportActivity.class);

            startActivity(intent);
        });
    }
}