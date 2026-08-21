package com.livewire.Service;

import android.util.Log;

import com.livewire.Model.ChatMessage;
import com.livewire.Model.DiagnosticReport;
import com.livewire.Model.DiagnosticStatistics;

import com.livewire.Service.DiagnosticEventLogger;

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

    public void getDiagnostics(DiagnosticsCallback callback) {

        Request request = new Request.Builder()
                .url("http://10.0.0.1:8000/diagnostics")
                .get()
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
                            "HTTP error: " + response.code()
                    );
                    return;
                }

                String responseBody =
                        response.body().string();

                try {
                    JSONObject json =
                            new JSONObject(responseBody);

                    JSONObject server =
                            json.getJSONObject("server");

                    JSONObject model =
                            json.getJSONObject("model");

                    DiagnosticReport report =
                            new DiagnosticReport();

                    report.setServerStatus(
                            server.getString("status")
                    );

                    report.setUptimeSeconds(
                            server.getDouble("uptime_seconds")
                    );

                    report.setModelName(
                            model.getString("name")
                    );

                    report.setTemperature(
                            model.getDouble("temperature")
                    );

                    report.setTopP(
                            model.getDouble("top_p")
                    );

                    report.setTopK(
                            model.getInt("top_k")
                    );

                    report.setMaxTokens(
                            model.getInt("max_tokens")
                    );

                    callback.onResult(report);

                } catch (Exception e) {

                    callback.onError(
                            "Invalid diagnostics response: "
                                    + e.getMessage()
                    );
                }
            }
        });
    }

    public interface DiagnosticsCallback {
        void onResult(DiagnosticReport report);
        void onError(String error);
    }


    public void analyzeDiagnostics(
            DiagnosticReport report,
            AnalysisCallback callback) {

        try {

            JSONObject json = new JSONObject();

            json.put(
                    "server_status",
                    report.getServerStatus()
            );

            json.put(
                    "uptime_seconds",
                    report.getUptimeSeconds()
            );

            json.put(
                    "model_name",
                    report.getModelName()
            );

            json.put(
                    "temperature",
                    report.getTemperature()
            );

            json.put(
                    "top_p",
                    report.getTopP()
            );

            json.put(
                    "top_k",
                    report.getTopK()
            );

            json.put(
                    "max_tokens",
                    report.getMaxTokens()
            );

            DiagnosticStatistics statistics =
                    report.getStatistics();

            if (statistics != null) {

                json.put(
                        "total_requests",
                        statistics.getTotalRequests()
                );

                json.put(
                        "successful_requests",
                        statistics.getSuccessfulRequests()
                );

                json.put(
                        "failed_requests",
                        statistics.getFailedRequests()
                );

                json.put(
                        "network_errors",
                        statistics.getNetworkErrors()
                );

                json.put(
                        "http_errors",
                        statistics.getHttpErrors()
                );

                json.put(
                        "parse_errors",
                        statistics.getParseErrors()
                );

                json.put(
                        "average_response_ms",
                        statistics.getAverageResponseTimeMs()
                );

                json.put(
                        "slowest_response_ms",
                        statistics.getSlowestResponseMs()
                );
            }

            RequestBody body =
                    RequestBody.create(
                            json.toString(),
                            MediaType.parse(
                                    "application/json"
                            )
                    );

            Request request =
                    new Request.Builder()
                            .url(
                                    "http://10.0.0.1:8000/analyze"
                            )
                            .post(body)
                            .build();

            client.newCall(request).enqueue(
                    new Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e) {

                            callback.onError(
                                    e.getMessage()
                            );
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response)
                                throws IOException {

                            if (!response.isSuccessful()) {

                                callback.onError(
                                        "HTTP error: " +
                                                response.code()
                                );

                                return;
                            }

                            String responseBody =
                                    response.body().string();
                            Log.d("LiveWire", "ANALYZE RESPONSE: " + responseBody);

                            try {

                                JSONObject result =
                                        new JSONObject(responseBody);

                                String analysis =
                                        result.getString("analysis");

                                Log.d("LiveWire", "ANALYSIS PARSED: " + analysis);
                                callback.onResult(analysis);

                            } catch (Exception e) {

                                callback.onError(
                                        "Invalid analysis response: " +
                                                e.getMessage()
                                );
                            }
                        }
                    }
            );

        } catch (Exception e) {

            callback.onError(
                    e.getMessage()
            );
        }
    }

    public interface AnalysisCallback {
        void onResult(String analysis);
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

                jsonMessage.put("content", message.getMessage());
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

            long startTime = System.currentTimeMillis();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    long duration = System.currentTimeMillis() - startTime;

                    DiagnosticEventLogger.log(
                            "NETWORK_ERROR",
                            e.getMessage(),
                            duration
                    );

                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    long duration =
                            System.currentTimeMillis() - startTime;

                    if (!response.isSuccessful()) {
                        DiagnosticEventLogger.log(
                                "HTTP_ERROR",
                                "HTTP " + response.code(),
                                duration
                        );

                        callback.onError("HTTP error: " + response.code());

                        return;
                    }

                    String responseBody =
                            response.body().string();

                    try {

                        JSONObject jsonResponse = new JSONObject(responseBody);

                        String result =
                                jsonResponse.getString("response");

                        DiagnosticEventLogger.log(
                                "CHAT_SUCCESS",
                                "HTTP " + response.code(),
                                duration
                        );

                        callback.onResult(result);

                    } catch (Exception e) {

                        DiagnosticEventLogger.log(
                                "RESPONSE_PARSE_ERROR",
                                e.getMessage(),
                                duration
                        );

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