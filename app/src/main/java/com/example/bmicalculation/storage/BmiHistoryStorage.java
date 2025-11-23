package com.example.bmicalculation.storage;

import android.content.SharedPreferences;
import com.example.bmicalculation.model.BmiInput;
import com.example.bmicalculation.model.BmiRecord;
import com.example.bmicalculation.model.BmiResult;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BmiHistoryStorage {
    private static final int MAX_RECORDS = 10;
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_BMI = "bmi";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_HISTORY = "bmi_history";

    private final SharedPreferences preferences;

    public BmiHistoryStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void persistForm(String height, String weight, String age, String gender) {
        preferences.edit()
                .putString(KEY_HEIGHT, height)
                .putString(KEY_WEIGHT, weight)
                .putString(KEY_AGE, age)
                .putString(KEY_GENDER, gender)
                .apply();
    }

    public FormState loadForm() {
        String height = preferences.getString(KEY_HEIGHT, "");
        String weight = preferences.getString(KEY_WEIGHT, "");
        String age = preferences.getString(KEY_AGE, "");
        String gender = preferences.getString(KEY_GENDER, "male");
        return new FormState(height, weight, age, gender);
    }

    public void addRecord(BmiInput input, BmiResult result) {
        try {
            JSONArray history = new JSONArray(preferences.getString(KEY_HISTORY, "[]"));
            JSONObject record = new JSONObject();
            record.put(KEY_HEIGHT, String.valueOf(input.getHeightCm()));
            record.put(KEY_WEIGHT, String.valueOf(input.getWeightKg()));
            record.put(KEY_AGE, String.valueOf(input.getAge()));
            record.put(KEY_GENDER, input.getGender().getDisplayValue());
            record.put(KEY_BMI, result.getBmiDisplay());
            record.put(KEY_CATEGORY, result.getCategory());

            history.put(record);
            if (history.length() > MAX_RECORDS) {
                JSONArray trimmed = new JSONArray();
                for (int i = history.length() - MAX_RECORDS; i < history.length(); i++) {
                    trimmed.put(history.getJSONObject(i));
                }
                history = trimmed;
            }
            preferences.edit().putString(KEY_HISTORY, history.toString()).apply();
        } catch (JSONException e) {
            // Swallow to avoid crashing the flow; history is non-critical.
        }
    }

    public List<BmiRecord> loadHistory() {
        List<BmiRecord> records = new ArrayList<>();
        try {
            JSONArray history = new JSONArray(preferences.getString(KEY_HISTORY, "[]"));
            for (int i = 0; i < history.length(); i++) {
                JSONObject record = history.getJSONObject(i);
                records.add(new BmiRecord(
                        record.optString(KEY_HEIGHT),
                        record.optString(KEY_WEIGHT),
                        record.optString(KEY_AGE),
                        record.optString(KEY_GENDER),
                        record.optString(KEY_BMI),
                        record.optString(KEY_CATEGORY)));
            }
        } catch (JSONException ignored) {
        }
        return records;
    }

    public static class FormState {
        private final String height;
        private final String weight;
        private final String age;
        private final String gender;

        public FormState(String height, String weight, String age, String gender) {
            this.height = height;
            this.weight = weight;
            this.age = age;
            this.gender = gender;
        }

        public String getHeight() {
            return height;
        }

        public String getWeight() {
            return weight;
        }

        public String getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }
    }
}
