package com.livewire.Service;

// Used for writing diagnostic and debugging messages to Android Logcat
import android.util.Log;

// Model class representing individual chat messages
import com.livewire.Model.AIModel;
import com.livewire.Model.ChatMessage;

// Model class representing individual diagnostic events
import com.livewire.Model.DiagnosticEvent;

// Model class containing complete diagnostic information about the AI service
import com.livewire.Model.DiagnosticReport;

// Model class containing aggregated diagnostic statistics
import com.livewire.Model.DiagnosticStatistics;

// Logger utility used to record service events such as errors and successful requests
import com.livewire.Service.DiagnosticEventLogger;

// OkHttp classes used for creating and executing HTTP network requests
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// JSON classes used to create and parse API request and response data
import org.json.JSONArray;
import org.json.JSONObject;

// Java utility classes
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AIService provides communication between the Android application and the
 * LiveWire AI backend server

 * Responsibilities:
 * - Sending chat conversations to the AI server
 * - Retrieving server and model diagnostics
 * - Sending diagnostic information for AI analysis
 * - Handling asynchronous network responses

 * All network operations are preformed asynchronously using OkHttp so that
 * the Android UI thread is not blocked
 */
public class AIService {

    /**
     *  OkHttp client used for all communication with the AI backend
     *  The timeout values are extended because AI model responses may take
     *  longer than normal API requests due to processing time.
     */
    private final OkHttpClient client = new OkHttpClient.Builder()

            // Maximum time allowed to establish a connection with the server
            .connectTimeout(360, TimeUnit.SECONDS)

            // Maximum time allowed for sending request data
            .writeTimeout(360, TimeUnit.SECONDS)

            // Maximum time allowed waiting for the server response
            .readTimeout(360, TimeUnit.SECONDS)

            // Creates the configured HTTP client instance
            .build();

    /**
     * Base URL used for sending chat requests
     * The backend server is expected to expose the /chat endpoint
     */
    private static final String URL =
            "http://10.0.0.1:8000/chat";

    /**
     * Callback interface used by chat requests

     * Network operations run asynchronously so results are returned through
     * these callback methods instead of directly returning a value
     */
    public interface ServiceCallback {

        /**
         * Called when the AI server successfully returns a response
         * @param response AI-generated response text
         */
        void onResult(String response);

        /**
         * Called when the request fails
         
         * Possible causes:
         * - Network connection failure
         * - Server unavailable
         * - Invalid response
         * @param error Description of failure
         */
        void onError(String error);
    }

    public void getDiagnostics(AIModel model, DiagnosticsCallback callback) {

        String url = "http://10.0.0.1:8000/diagnostics" + "?model=" + model.getId();

        Request request = new Request.Builder()
                .url(url)
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
            String modelId,
            AnalysisCallback callback) {

        try {

            JSONObject json = new JSONObject();

            json.put(
                    "model",
                    modelId
            );

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

            JSONArray eventsJson = new JSONArray();

            List<DiagnosticEvent> events =
                    report.getEvents();

            if (events != null) {

                int start =
                        Math.max(0, events.size() - 50);

                for (int i = start; i < events.size(); i++) {

                    DiagnosticEvent event =
                            events.get(i);

                    JSONObject eventJson =
                            new JSONObject();

                    eventJson.put(
                            "type",
                            event.getType()
                    );

                    eventJson.put(
                            "details",
                            event.getDetails()
                    );

                    eventJson.put(
                            "timestamp",
                            event.getTimestamp()
                    );

                    eventJson.put(
                            "duration_ms",
                            event.getDurationMs()
                    );

                    eventsJson.put(eventJson);
                }
            }

            json.put("events", eventsJson);

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
            AIModel model,
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

            json.put("model", model.getId());

            json.put("backend", model.getBackend());

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