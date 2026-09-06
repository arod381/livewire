package com.livewire.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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

        TextView maxTokensLabel =
                view.findViewById(R.id.max_tokens_label);

        TextView temperatureLabel =
                view.findViewById(R.id.temperature_label);

        TextView topPLabel =
                view.findViewById(R.id.top_p_label);

        TextView topKLabel =
                view.findViewById(R.id.top_k_label);

        SeekBar maxSeekBar =
                view.findViewById(R.id.max_seekbar);

        SeekBar temperatureSeekBar =
                view.findViewById(R.id.temp_seekbar);

        SeekBar topPSeekBar =
                view.findViewById(R.id.top_p_seekbar);

        SeekBar topKSeekBar =
                view.findViewById(R.id.top_k_seekbar);

        /*
         * Updates the SeekBars and labels from the selected model.
         */
        AdapterView.OnItemSelectedListener modelListener =
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View itemView,
                            int position,
                            long id) {

                        AIModel selectedModel =
                                models.get(position);

                        viewModel.setSelectedModel(
                                selectedModel
                        );

                        /*
                         * Convert the model's floating-point
                         * values into integer SeekBar positions.
                         */

                        maxSeekBar.setProgress(
                                selectedModel.getMaxTokens()
                        );

                        temperatureSeekBar.setProgress(
                                (int) (selectedModel.getTemperature() * 100)
                        );

                        topPSeekBar.setProgress(
                                (int) (selectedModel.getTopP() * 100)
                        );

                        topKSeekBar.setProgress(
                                selectedModel.getTopK()
                        );

                        /*
                         * Update the visible labels.
                         */

                        maxTokensLabel.setText("Max Tokens: " + selectedModel.getMaxTokens());

                        temperatureLabel.setText(
                                String.format("Temperature: %.2f", selectedModel.getTemperature()));

                        topPLabel.setText(
                                String.format("Top P: %.2f", selectedModel.getTopP()));

                        topKLabel.setText("Top K: " + selectedModel.getTopK());
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                        viewModel.setSelectedModel(null);
                    }
                };

        modelSpinner.setOnItemSelectedListener(
                modelListener
        );

        temperatureSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        double temperature =
                                progress / 100.0;

                        temperatureLabel.setText(
                                String.format("Temperature: %.2f", temperature));

                        if (fromUser) {
                            viewModel.setTemperature(temperature);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        topPSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        double topP =
                                progress / 100.0;

                        topPLabel.setText(
                                String.format("Top P: %.2f", topP)
                        );

                        if (fromUser) {
                            viewModel.setTopP(progress / 100.0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                }
        );

        topKSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        topKLabel.setText("Top K: " + progress);

                        if (fromUser) {
                            viewModel.setTopK(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        maxSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        maxTokensLabel.setText("Max Tokens: " + progress);

                        if (fromUser) {
                            viewModel.setMaxTokens(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        Button applyConfigButton =
                view.findViewById(R.id.apply_config_button);

        applyConfigButton.setOnClickListener(v -> {

            applyConfigButton.setEnabled(false);

            viewModel.applyModelConfiguration(new MainViewModel.ConfigurationCallback() {

                        @Override
                        public void onResult(String message) {

                            requireActivity().runOnUiThread(() -> {

                                applyConfigButton.setEnabled(true);

                                android.widget.Toast.makeText(
                                        requireContext(),
                                        "Configuration applied",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();
                            });
                        }

                        @Override
                        public void onError(String error) {

                            requireActivity().runOnUiThread(() -> {

                                applyConfigButton.setEnabled(true);

                                android.widget.Toast.makeText(
                                        requireContext(),
                                        "Configuration error: " + error,
                                        android.widget.Toast.LENGTH_LONG
                                ).show();
                            });
                        }
                    }
            );
        });

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

            AIModel selectedModel = viewModel.getSelectedModel().getValue();

            Intent intent =
                    new Intent(requireContext(), ReportActivity.class);

            if (selectedModel != null) {

                intent.putExtra("model_id", selectedModel.getId());
            }

            startActivity(intent);
        });

        Spinner contextSpinner =
                view.findViewById(R.id.context_spinner);

        contextSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        int selectedLimit;

                        switch (position) {

                            case 0:
                                // Context OFF
                                selectedLimit = 0;
                                break;

                            case 1:
                                // Recent 4 messages
                                selectedLimit = 4;
                                break;

                            case 2:
                                // Recent 10 messages
                                selectedLimit = 10;
                                break;

                            case 3:
                                // Full conversation
                                selectedLimit = -1;
                                break;

                            default:
                                selectedLimit = 10;
                                break;
                        }

                        viewModel.setContextLimit(selectedLimit);

                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                        viewModel.setContextLimit(10);
                    }
                }
        );

    }
}