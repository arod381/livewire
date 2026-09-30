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
                        "microsoft_Phi-4-mini-instruct-Q4_K_M.gguf",
                        "Phi 4 Mini",
                        "gguf_ondevice",
                        1000,
                        1.5,
                        0.9,
                        10
                )
        );

        models.add(
                new AIModel(
                        "gemma-2-2b-it-Q4_K_M.gguf",
                        "Gemma 2B",
                        "gguf_ondevice",
                        500,
                        0.8,
                        0.9,
                        10
                )
        );

        return models;
    }
}