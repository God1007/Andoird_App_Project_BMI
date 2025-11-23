package com.example.bmicalculation.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bmicalculation.model.BmiInput;
import com.example.bmicalculation.model.BmiRecord;
import com.example.bmicalculation.model.BmiResult;
import java.time.LocalDate;
import java.util.List;

public class BmiHistoryStorage {
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";

    private final SharedPreferences preferences;
    private final BmiDatabaseHelper databaseHelper;

    public BmiHistoryStorage(Context context) {
        this.preferences = context.getSharedPreferences("BMI_Data", Context.MODE_PRIVATE);
        this.databaseHelper = new BmiDatabaseHelper(context);
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
        BmiRecord record = new BmiRecord(
                String.valueOf(input.getHeightCm()),
                String.valueOf(input.getWeightKg()),
                String.valueOf(input.getAge()),
                input.getGender().getDisplayValue(),
                String.valueOf(result.getBmiValue()),
                result.getCategory(),
                LocalDate.now());
        databaseHelper.saveRecord(record);
    }

    public List<BmiRecord> loadHistory() {
        return databaseHelper.loadRecords();
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
