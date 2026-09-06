// Application Logic - state and orchestration layer

package com.livewire.ViewModel;

import android.util.Log;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.livewire.Model.AIModel;
import com.livewire.Model.ChatMessage;
import com.livewire.Model.ConversationSnapshot;
import com.livewire.Model.DiagnosticEvent;
import com.livewire.Model.DiagnosticReport;
import com.livewire.Model.DiagnosticStatistics;
import com.livewire.Entity.DynamoResponse;

import com.livewire.Service.DiagnosticAnalyzer;
import com.livewire.Service.DiagnosticEventLogger;

import java.util.List;
import com.livewire.Repository.MainRepository;

import java.util.ArrayList;

/**
 * MainViewModel is responsible for managing the application's UI state

 * It sits between the UI (Activity/Fragment) and MainRepository

 * The ViewModel:
 * - Maintains the current conversation
 * - Sends prompts to the AI
 * - Controls how much conversation context is sent to the AI
 * - Tracks loading state
 * - Loads diagnostic information
 * - Calculates local diagnostic statistics
 * - Requests AI analysis of diagnostic data

 * LiveData is used so the UI can observe changes without directly
 * managing the underlying data or network operations
 */

public class MainViewModel extends AndroidViewModel {

    /*
     * Controls how much conversation history is sent to the AI
     *
     * Values:
     *
     * 0 = Context disabled; send only the current prompt
     * -1 = Send the entire conversation
     * >0 = Send only the most recent N messages
     *
     * Default is 0 messages
     */
    private final MutableLiveData<Integer> contextLimit =
            new MutableLiveData<>(10);

    private final MutableLiveData<AIModel> selectedModel =
            new MutableLiveData<>();

    /*
     * Stores the most recently submitted user prompt
     */
    private final MutableLiveData<String> currentPrompt =
            new MutableLiveData<>();

    /*
     * Indicates whether an AI request is currently being processed
     *
     * true = request in progress
     * false = request completed or failed
     */
    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    /*
     * Stores the complete conversation displayed by the UI
     *
     * This contains both USER and AI messages
     */
    private final MutableLiveData<List<ChatMessage>> conversation =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<ConversationSnapshot> conversationSnapshot =
            new MutableLiveData<>();
    /*
     * Stores the most recently retrieved diagnostic report
     */
    private final MutableLiveData<DiagnosticReport> diagnostics =
            new MutableLiveData<>();

    /*
     * Stores the AI-generated analysis of the diagnostic report
     */
    private final MutableLiveData<String> diagnosticAnalysis =
            new MutableLiveData<>();

    private final MutableLiveData<List<DynamoResponse>> dynamos =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Integer> maxTokens =
            new MutableLiveData<>(100);

    private final MutableLiveData<Double> temperature =
            new MutableLiveData<>(0.7);

    private final MutableLiveData<Double> topP =
            new MutableLiveData<>(0.9);

    private final MutableLiveData<Integer> topK =
            new MutableLiveData<>(10);

    /*
     * Repository responsible for communicating with the AI/backend layer
     *
     * The ViewModel does not communicate directly with AIService
     */
    private final MainRepository repository;

    /**
     * Creates the ViewModel and initializes the repository
     */
    public MainViewModel(Application application) {

        super(application);

        repository = new MainRepository(application);

        loadDynamos();
    }

    public LiveData<Integer> getMaxTokens() {
        return maxTokens;
    }

    public LiveData<Double> getTemperature() {
        return temperature;
    }

    public LiveData<Double> getTopP() {
        return topP;
    }

    public LiveData<Integer> getTopK() {
        return topK;
    }

    public void setMaxTokens(int value) {
        maxTokens.setValue(value);
    }

    public void setTemperature(double value) {
        temperature.setValue(value);
    }

    public void setTopP(double value) {
        topP.setValue(value);
    }

    public void setTopK(int value) {
        topK.setValue(value);
    }


    public void loadModelConfiguration(AIModel model) {

        if (model == null) {
            return;
        }

        maxTokens.setValue(model.getMaxTokens());
        temperature.setValue(model.getTemperature());
        topP.setValue(model.getTopP());
        topK.setValue(model.getTopK());
    }


    public LiveData<AIModel> getSelectedModel() {
        return selectedModel;
    }
    public LiveData<List<DynamoResponse>> getDynamos() {
        return dynamos;
    }

    public void setSelectedModel(AIModel model) {
        selectedModel.setValue(model);

        if (model != null) {

            Log.d("LiveWire", "SELECTED MODEL SET: " + model.getId());

            loadModelConfiguration(model);
        }
    }

    public interface ConfigurationCallback {

        void onResult(String message);

        void onError(String error);
    }

    public void applyModelConfiguration(
            ConfigurationCallback callback) {

        AIModel model =
                selectedModel.getValue();

        if (model == null) {

            callback.onError(
                    "No model selected."
            );

            return;
        }

        Integer max =
                maxTokens.getValue();

        Double temp =
                temperature.getValue();

        Double p =
                topP.getValue();

        Integer k =
                topK.getValue();

        if (max == null ||
                temp == null ||
                p == null ||
                k == null) {

            callback.onError(
                    "Model configuration is incomplete."
            );

            return;
        }

        repository.updateModelConfiguration(
                model.getId(),
                max,
                temp,
                p,
                k,
                new MainRepository.ConfigurationRepositoryCallback() {

                    @Override
                    public void onResult(String message) {

                        callback.onResult(message);
                    }

                    @Override
                    public void onError(String error) {

                        callback.onError(error);
                    }
                }
        );
    }

    /*
     * Create a snapshot of the current conversation
     *
     * This creates an independent copy rather than keeping a
     * reference to the mutable list
     *
     * This snapshot is not used later in the method
     * so it could potentially be removed unless it is intended
     * for future asynchronous/state handling
     */

    public LiveData<ConversationSnapshot> getConversationSnapshot() {
        return conversationSnapshot;
    }

    /**
     * Sends a user prompt to the AI

     * This method:
     * 1. Saves the current prompt
     * 2. Adds the user's message to the conversation
     * 3. Sets loading to true
     * 4. Builds the context that will be sent to the AI
     * 5. Sends that context through MainRepository
     * 6. Adds the AI response to the conversation

     * @param prompt Text entered by the user
     */
    public void submitPrompt(String prompt) {

        AIModel model = selectedModel.getValue();

        if (model == null) {
            // No model has been selected yet.
            Log.d(
                    "LiveWire",
                    "No AI model selected"
            );

            loading.setValue(false);
            return;
        }
        // Store the prompt as the current prompt
        currentPrompt.setValue(prompt);

        /*
         * Get the current conversation
         *
         * getValue() may return null, so create an empty list
         * if no conversation exists yet
         */
        List<ChatMessage> existing = conversation.getValue();

        List<ChatMessage> messages =
                existing == null
                        ? new ArrayList<>()
                        : new ArrayList<>(existing);

        // Add the user's new message to the conversation
        messages.add(new ChatMessage(prompt, ChatMessage.Sender.USER));

        // Publish the updated conversation to observers
        conversation.setValue(messages);

        // Tell the UI that an AI request is now running
        loading.setValue(true);

        /*
         * Retrieve the configured context limit
         * If it has somehow become null, use the default value of 10
         */
        int limit = contextLimit.getValue() != null
                ? contextLimit.getValue()
                : 10;

        // This list will contain only the messages sent to the AI
        List<ChatMessage> aiContext;

        /*
         * CONTEXT MODE 1: Context disabled
         * Only the current user prompt is sent
         * The conversation is still retained locally and displayed
         * by the application
         */
        if (limit == 0) {

            // Context OFF:
            // Send only the current user prompt
            aiContext = new ArrayList<>();

            aiContext.add(
                    new ChatMessage(
                            prompt,
                            ChatMessage.Sender.USER
                    )
            );

            /*
             * CONTEXT MODE 2: Full conversation
             *
             * A limit of -1 means the entire conversation should be
             * included in the request
             *
             * This also happens when the conversation contains fewer
             * messages than the configured limit
             */
        } else if (limit == -1 || messages.size() <= limit) {

            // All context
            aiContext = new ArrayList<>(messages);

            /*
             * CONTEXT MODE 3: Limited recent context
             * Only the most recent N messages are sent to the AI
             *
             * Example:
             *
             * Conversation has 20 messages
             * Limit = 10
             *
             * Messages 11-20 are sent to the model
             */
        } else {

            // Limited recent context
            aiContext = new ArrayList<>(
                    messages.subList(
                            messages.size() - limit,
                            messages.size()
                    )
            );
        }

        Log.d(
                "LiveWire",
                "CONTEXT LIMIT: " + limit +
                        " | MESSAGES SENT: " + aiContext.size()
        );

        for (ChatMessage message : aiContext) {

            Log.d(
                    "LiveWire",
                    "CONTEXT MESSAGE: " +
                            message.getSender() +
                            " -> " +
                            message.getMessage()
            );
        }

        /*
         * Create a snapshot of the current conversation
         *
         * This creates an independent copy rather than keeping a
         * reference to the mutable list
         *
         * This snapshot is not used later in the method
         * so it could potentially be removed unless it is intended
         * for future asynchronous/state handling
         */
        ConversationSnapshot snapshot =
                new ConversationSnapshot(
                        model.getId(),
                        limit,
                        aiContext
                );

        conversationSnapshot.setValue(snapshot);

        Log.d(
                "LiveWire",
                "SNAPSHOT CREATED: model=" +
                        snapshot.getModelId() +
                        " | context=" +
                        snapshot.getContextLimit() +
                        " | messages=" +
                        snapshot.getMessageCount()
        );
        
        /*
         * Send the selected AI context to the repository
         *
         * The repository handles communication with AIService
         */
        repository.submitPrompt(
                aiContext,
                model,
                new MainRepository.RepositoryCallback() {

            /**
             * Called when the AI successfully responds
             */
            @Override
            public void onResult(String result) {

                // Log the response for debugging
                android.util.Log.d(
                        "LiveWire",
                        "AI RESPONSE: " + result
                );

                /*
                 * Retrieve the latest conversation
                 *
                 * We retrieve it again here because the request
                 * is asynchronous and the state may have changed
                 * while the AI was processing the request
                 */
                List<ChatMessage> existing = conversation.getValue();

                List<ChatMessage> messages =
                        existing == null
                                ? new ArrayList<>()
                                : new ArrayList<>(existing);

                // Add the AI response to the conversation
                messages.add(
                        new ChatMessage(result, ChatMessage.Sender.AI)
                );

                // postValue() is safe when called from a
                // background/network thread
                conversation.postValue(messages);

                // Tell the UI that the request has finished
                loading.postValue(false);
            }

            /**
             * Called when the AI request fails
             */
            @Override
            public void onError(String error) {

                // Retrieve the current conversation
                List<ChatMessage> messages = conversation.getValue();

                // Make sure a list exists before adding the error
                if (messages == null) {
                    messages = new ArrayList<>();
                }

                /*
                 * Display the error as an AI message
                 *
                 * This means the chat UI can show the failure
                 * without requiring a separate error-message UI
                 */
                messages.add(
                        new ChatMessage(
                                "Error: " + error,
                                ChatMessage.Sender.AI
                        )
                );

                // Update the conversation from the callback thread
                conversation.postValue(messages);

                // Request is no longer loading
                loading.postValue(false);
            }
        });
    }

    /**
     * Retrieves diagnostic information

     * The repository obtains the server-side DiagnosticReport and
     * combines it with the locally recorded DiagnosticEvents

     * This ViewModel then calculates additional statistics locally
     */
    public void loadDiagnostics() {

        AIModel model = selectedModel.getValue();

        if (model == null) {

            Log.e("LiveWire", "DIAGNOSTICS: No model selected");
            diagnostics.postValue(null);
            return;
        }

        Log.d("LiveWire", "LOADING DIAGNOSTICS FOR MODEL: " + model.getId());

        repository.getDiagnostics(
                model,
                new MainRepository.DiagnosticsRepositoryCallback() {

                    /**
                     * Called when diagnostics are successfully retrieved
                     */
                    @Override
                    public void onResult(DiagnosticReport report) {

                        Log.d("LiveWire", "DIAGNOSTICS REPORT RECEIVED");

                        /*
                         * Get a snapshot of locally recorded diagnostic
                         * events
                         */
                        List<DiagnosticEvent> events =
                                new ArrayList<>(
                                        DiagnosticEventLogger.getEvents()
                                );

                        /*
                         * Attach local event history to the diagnostic
                         * report
                         */
                        report.setEvents(events);

                        /*
                         * Analyze the events locally
                         *
                         * DiagnosticAnalyzer calculates statistics such
                         * as request counts, failures, and response times
                         */
                        DiagnosticStatistics statistics =
                                DiagnosticAnalyzer.analyze(events);

                        // Attach calculated statistics to the report
                        report.setStatistics(statistics);

                        /*
                         * Publish the completed report
                         *
                         * Any Activity/Fragment observing getDiagnostics()
                         * will receive the updated report
                         */
                        diagnostics.postValue(report);
                    }

                    /**
                     * Called when diagnostics could not be retrieved
                     */
                    @Override
                    public void onError(String error) {

                        Log.e("LiveWire", "DIAGNOSTICS ERROR: " + error);

                        // Clear the current diagnostic report
                        diagnostics.postValue(null);

                        // Expose the error through the analysis state
                        diagnosticAnalysis.postValue("Diagnostics error: " + error);
                    }
                }
        );
    }

    /**
     * Sends the current diagnostic report to the AI for analysis

     * The diagnostic report must already have been loaded using
     * loadDiagnostics()
     */
    public void analyzeDiagnostics() {

        // Retrieve the most recently loaded diagnostic report
        DiagnosticReport report =
                diagnostics.getValue();

        /*
         * There is nothing to analyze if diagnostics have not
         * been loaded yet
         */
        if (report == null) {
            diagnosticAnalysis.postValue(
                    "No diagnostic report available."
            );
            return;
        }

        AIModel model =
                selectedModel.getValue();

        if (model == null) {
            diagnosticAnalysis.postValue("No model selected");

            return;
        }

        String modelId = model.getId();

        Log.d("LiveWire", "ANALYZING DIAGNOSTICS WITH MODEL: " + modelId);

        /*
         * Send the report to MainRepository
         *
         * MainRepository delegates the actual network request
         * to AIService
         */
        repository.analyzeDiagnostics(
                report,
                modelId,
                new MainRepository.AnalysisRepositoryCallback() {

                    /**
                     * Called when AI diagnostic analysis succeeds
                     */
                    @Override
                    public void onResult(
                            String analysis) {

                        // Log the received analysis for debugging
                        Log.d(
                                "LiveWire",
                                "VIEWMODEL ANALYSIS RECEIVED: " + analysis
                        );

                        // Publish the AI analysis to the UI
                        diagnosticAnalysis.postValue(
                                analysis
                        );
                    }

                    /**
                     * Called when AI diagnostic analysis fails
                     */
                    @Override
                    public void onError(
                            String error) {

                        // Make the error available to the UI
                        diagnosticAnalysis.postValue(
                                "Analysis error: " + error);
                    }
                }
        );
    }

    public void saveDynamo(String response) {

        DynamoResponse favorite =
                new DynamoResponse(
                        response,
                        System.currentTimeMillis()
                );

        repository.saveDynamo(
                favorite,
                new MainRepository.DynamoRepositoryCallback() {

                    @Override
                    public void onComplete() {

                        loadDynamos();
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(
                                "LiveWire",
                                "SAVE DYNAMO ERROR: " + error
                        );
                    }
                }
        );
    }

    public void loadDynamos() {

        repository.getDynamos(
                new MainRepository.DynamosRepositoryCallback() {

                    @Override
                    public void onResult(
                            List<DynamoResponse> responses) {

                        dynamos.postValue(responses);
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(
                                "LiveWire",
                                "LOAD DYNAMOS ERROR: " + error
                        );
                    }
                }
        );
    }

    public void deleteDynamo(
            DynamoResponse response) {

        repository.deleteDynamo(
                response,
                new MainRepository.DynamoRepositoryCallback() {

                    @Override
                    public void onComplete() {

                        loadDynamos();
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(
                                "LiveWire",
                                "DELETE DYNAMO ERROR: " + error
                        );
                    }
                }
        );
    }

    public void deleteAllDynamos() {

        repository.deleteAllDynamos(
                new MainRepository.DynamoRepositoryCallback() {

                    @Override
                    public void onComplete() {

                        loadDynamos();
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(
                                "LiveWire",
                                "DELETE ALL DYNAMOS ERROR: " + error
                        );
                    }
                }
        );
    }

    /**
     * Provides read-only access to the diagnostic report

     * The UI observes this LiveData rather than modifying the
     * MutableLiveData directly
     */
    public LiveData<DiagnosticReport> getDiagnostics() {
        return diagnostics;
    }

    /**
     * Provides read-only access to the AI diagnostic analysis
     */
    public LiveData<String> getDiagnosticAnalysis() {
        return diagnosticAnalysis;
    }

    /**
     * Provides read-only access to the context limit
     */
    public LiveData<Integer> getContextLimit() {
        return contextLimit;
    }

    /**
     * Changes the amount of conversation context sent to the AI
     *
     * @param limit 0 for no context, -1 for all context
     *              or a positive number for recent-message context
     */
    public void setContextLimit(int limit) {
        contextLimit.setValue(limit);
    }

    /**
     * Provides the most recently submitted prompt
     */
    public LiveData<String> getCurrentPrompt() {
        return currentPrompt;
    }

    /**
     * Provides the current loading state
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }

    /**
     * Provides the current conversation
     */
    public LiveData<List<ChatMessage>> getConversation() {
        return conversation;
    }

}
