package com.livewire.Model;

import java.util.ArrayList;
import java.util.List;

public final class ModelCatalog {

    private ModelCatalog() {
    }

    public static List<AIModel> getModels() {

        List<AIModel> models = new ArrayList<>();

        models.add(
                new AIModel(
                        "phi4-mini",
                        "Phi 4 3.8B",
                        "ollama",
                        350,
                        0.8,
                        0.9,
                        10

                )
        );

        models.add(
                new AIModel(
                        "livewire1.0:0",
                        "Livewire 1.0 1A",
                        "transformers",
                        350,
                        0.8,
                        0.9,
                        10
                )
        );

        return models;
    }
}