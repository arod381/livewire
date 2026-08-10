package com.example.livewire.Service;

import androidx.annotation.NonNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class AIService {

    private final OkHttpClient client = new OkHttpClient();

    private static final String URL =
            "http://10.0.2.2:8000/chat";

    public interface ServiceCallback {
        void onResult(String response);
        void onError(String error);
    }

    public void sendPrompt(String prompt, ServiceCallback callback) {

        try {
            JSONObject json = new JSONObject();
            json.put("prompt", prompt);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    if (!response.isSuccessful()) {
                        callback.onError(
                                "HTTP error: " + response.code()
                        );
                        return;
                    }

                    String responseBody = response.body().string();

                    try {
                        JSONObject jsonResponse =
                                new JSONObject(responseBody);

                        String result =
                                jsonResponse.getString("response");

                        callback.onResult(result);

                    } catch (Exception e) {
                        callback.onError(
                                "Invalid response: " + e.getMessage()
                        );
                    }
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}
