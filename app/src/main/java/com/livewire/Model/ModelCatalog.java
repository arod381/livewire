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
                        "qwen3:1.7b",
                        "Qwen 3 1.7B",
                        "ollama"
                )
        );

        models.add(
                new AIModel(
                        "livewire1.0:0",
                        "Livewire 1.0 1A",
                        "transformers"
                )
        );

        return models;
    }
}