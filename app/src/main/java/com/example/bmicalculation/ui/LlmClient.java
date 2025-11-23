package com.example.bmicalculation.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.bmicalculation.BuildConfig;
import com.example.bmicalculation.R;
import com.example.bmicalculation.model.Gender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class LlmClient {

    private static final String ENDPOINT = "https://api.example.com/llm";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onResult(String suggestion);
    }

    public void requestSuggestion(Context context, double bmiValue, String bmiCategory, Gender gender, int age, Locale locale, Callback callback) {
        executor.execute(() -> {
            String suggestion = context.getString(R.string.llm_fallback);
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                if (!BuildConfig.LLM_API_KEY.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + BuildConfig.LLM_API_KEY);
                }
                connection.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("bmi", bmiValue);
                payload.put("category", bmiCategory);
                payload.put("gender", gender.getDisplayValue());
                payload.put("age", age);
                payload.put("language", locale.getLanguage());
                payload.put("prompt", context.getString(R.string.llm_prompt_template, bmiValue, age, gender.getDisplayValue()));

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    suggestion = readResponse(connection);
                }
            } catch (Exception ignored) {
                // fall back to default suggestion
            }

            final String finalSuggestion = suggestion;
            mainHandler.post(() -> callback.onResult(finalSuggestion));
        });
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String response = builder.toString();
            try {
                JSONObject json = new JSONObject(response);
                if (json.has("suggestion")) {
                    return json.getString("suggestion");
                }
            } catch (Exception ignored) {
                // ignore malformed json
            }
            return response.isEmpty() ? "" : response;
        }
    }
}
