package com.example.livewire.Service;

import androidx.annotation.NonNull;

import com.example.livewire.Model.ChatMessage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AIService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(360, TimeUnit.SECONDS)
            .writeTimeout(360, TimeUnit.SECONDS)
            .readTimeout(360, TimeUnit.SECONDS)
            .build();

    private static final String URL =
            "http://10.0.0.1:8000/chat";

    public interface ServiceCallback {
        void onResult(String response);
        void onError(String error);
    }

    public void sendPrompt(
            List<ChatMessage> messages,
            ServiceCallback callback) {

        try {
            JSONObject json = new JSONObject();
            JSONArray jsonMessages = new JSONArray();

            for (ChatMessage message : messages) {

                JSONObject jsonMessage = new JSONObject();

                if (message.getSender() ==
                        ChatMessage.Sender.USER) {

                    jsonMessage.put("role", "user");

                } else {

                    jsonMessage.put("role", "assistant");
                }

                jsonMessage.put(
                        "content",
                        message.getMessage()
                );

                jsonMessages.put(jsonMessage);
            }

            json.put("messages", jsonMessages);

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
                public void onFailure(
                        Call call,
                        IOException e) {

                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(
                        Call call,
                        Response response)
                        throws IOException {

                    if (!response.isSuccessful()) {

                        callback.onError(
                                "HTTP error: "
                                        + response.code()
                        );

                        return;
                    }

                    String responseBody =
                            response.body().string();

                    try {

                        JSONObject jsonResponse =
                                new JSONObject(responseBody);

                        String result =
                                jsonResponse.getString(
                                        "response"
                                );

                        callback.onResult(result);

                    } catch (Exception e) {

                        callback.onError(
                                "Invalid response: "
                                        + e.getMessage()
                        );
                    }
                }
            });

        } catch (Exception e) {

            callback.onError(e.getMessage());
        }
    }
}