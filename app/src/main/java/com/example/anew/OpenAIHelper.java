package com.example.anew;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Helper class that communicates with the OpenAI Chat Completions API.
 * Sends the full conversation history (up to 20 messages) along with a
 * location-aware system prompt so the model can give relevant travel advice.
 */
public class OpenAIHelper {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_HISTORY_MESSAGES = 20;

    private final String apiKey;
    private final OkHttpClient client;
    private final Handler mainHandler;

    /**
     * Callback interface for asynchronous API responses.
     */
    public interface ChatCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public OpenAIHelper(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Sends the conversation history to OpenAI and returns the assistant's reply
     * via the callback on the main thread.
     *
     * @param conversationHistory All messages exchanged so far (user + bot).
     * @param latitude            User's current latitude (0 if unknown).
     * @param longitude           User's current longitude (0 if unknown).
     * @param preferences         Comma-separated list of user's preferred place types.
     * @param callback            Callback to receive the response or error.
     */
    public void sendMessage(List<ChatMessage> conversationHistory,
                            double latitude, double longitude,
                            String preferences, ChatCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            JSONArray messages = new JSONArray();

            // System message with travel context
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", buildSystemPrompt(latitude, longitude, preferences));
            messages.put(systemMsg);

            // Conversation history (capped to prevent token overflow)
            int startIndex = Math.max(0, conversationHistory.size() - MAX_HISTORY_MESSAGES);
            for (int i = startIndex; i < conversationHistory.size(); i++) {
                ChatMessage msg = conversationHistory.get(i);
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.getType() == ChatMessage.TYPE_USER ? "user" : "assistant");
                msgObj.put("content", msg.getMessage());
                messages.put(msgObj);
            }

            requestBody.put("messages", messages);

            RequestBody body = RequestBody.create(requestBody.toString(), JSON_TYPE);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() ->
                            callback.onError("Network error. Please check your connection."));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(responseBody);
                            String content = json.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content")
                                    .trim();
                            mainHandler.post(() -> callback.onSuccess(content));
                        } catch (Exception e) {
                            mainHandler.post(() ->
                                    callback.onError("Failed to parse AI response."));
                        }
                    } else {
                        String errorMsg;
                        if (response.code() == 401) {
                            errorMsg = "Invalid API key. Please update it in settings (gear icon).";
                        } else if (response.code() == 429) {
                            errorMsg = "Rate limit reached. Please try again in a moment.";
                        } else if (response.code() == 500) {
                            errorMsg = "OpenAI server error. Please try again later.";
                        } else {
                            errorMsg = "API error (code " + response.code() + "). Please try again.";
                        }
                        mainHandler.post(() -> callback.onError(errorMsg));
                    }
                }
            });
        } catch (Exception e) {
            mainHandler.post(() ->
                    callback.onError("Failed to send message: " + e.getMessage()));
        }
    }

    /**
     * Builds a system prompt that gives the LLM travel-assistant context,
     * including the user's GPS coordinates and place preferences.
     */
    private String buildSystemPrompt(double latitude, double longitude, String preferences) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are Wanderly AI, a friendly and knowledgeable travel assistant ");
        prompt.append("embedded in a mobile travel app called Wanderly. ");
        prompt.append("Help users discover interesting places, plan trips, and provide ");
        prompt.append("travel recommendations. ");
        prompt.append("Keep responses concise, practical, and under 200 words. ");
        prompt.append("Use a warm, enthusiastic tone. ");

        if (latitude != 0.0 || longitude != 0.0) {
            prompt.append(String.format(
                    "The user's current GPS coordinates are (%.4f, %.4f). ", latitude, longitude));
            prompt.append("Use this to give location-relevant suggestions when appropriate. ");
        }

        if (preferences != null && !preferences.isEmpty()
                && !preferences.equalsIgnoreCase("skip")) {
            prompt.append("The user's preferred place categories are: ")
                    .append(preferences).append(". ");
            prompt.append("Prioritize these interests when making recommendations. ");
        }

        prompt.append("When listing places or tips, use bullet points or numbered lists. ");
        prompt.append("If the user asks for an itinerary, include estimated times and distances. ");
        prompt.append("Always be helpful and suggest follow-up questions the user might ask.");

        return prompt.toString();
    }
}
