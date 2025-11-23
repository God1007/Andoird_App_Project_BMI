package com.example.bmicalculation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bmicalculation.databinding.ActivityReportBinding;
import com.example.bmicalculation.model.BmiRecord;
import com.example.bmicalculation.model.Gender;
import com.example.bmicalculation.storage.BmiHistoryStorage;
import com.example.bmicalculation.ui.AdviceProvider;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            handleError("No data received", "Please go back and calculate your BMI first");
            return;
        }

        String heightStr = extras.getString(MainActivity.EXTRA_HEIGHT);
        String weightStr = extras.getString(MainActivity.EXTRA_WEIGHT);
        String ageStr = extras.getString(MainActivity.EXTRA_AGE);
        String genderStr = extras.getString(MainActivity.EXTRA_GENDER);
        double bmiValue = extras.getDouble(MainActivity.EXTRA_BMI_VALUE, Double.NaN);
        String bmiDisplay = extras.getString(MainActivity.EXTRA_BMI_DISPLAY);
        String bmiCategory = extras.getString(MainActivity.EXTRA_BMI_CATEGORY);

        if (heightStr == null || weightStr == null || ageStr == null || genderStr == null || bmiCategory == null || Double.isNaN(bmiValue)) {
            handleError("Missing data", "Please provide all required information");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            Gender gender = Gender.fromLabel(genderStr);
            DecimalFormat formatter = new DecimalFormat("0.00");

            String bmiText = bmiDisplay != null ? bmiDisplay : formatter.format(bmiValue);
            binding.reportResult.setText("Your BMI: " + bmiText);
            binding.reportCategory.setText("Category: " + bmiCategory);

            String detailsText = String.format(Locale.getDefault(),
                    "Height: %s cm\nWeight: %s kg\nAge: %s years\nGender: %s",
                    heightStr, weightStr, ageStr, genderStr);
            binding.reportDetails.setText(detailsText);

            AdviceProvider.Advice advice = new AdviceProvider().provide(bmiCategory, gender, age < 18);
            binding.reportImage.setImageResource(advice.getImageRes());
            binding.reportAdvice.setText(advice.getMessage());

            binding.reportHistory.setText(buildHistoryText());
        } catch (NumberFormatException e) {
            handleError("Invalid data format", "Please check your input values");
        }
    }

    private String buildHistoryText() {
        BmiHistoryStorage storage = new BmiHistoryStorage(getSharedPreferences("BMI_Data", MODE_PRIVATE));
        List<BmiRecord> records = storage.loadHistory();
        if (records.isEmpty()) {
            return "Recent Records:\nNo records yet.";
        }
        StringBuilder builder = new StringBuilder("Recent Records:\n");
        for (int i = records.size() - 1; i >= 0; i--) {
            BmiRecord record = records.get(i);
            builder.append(String.format(Locale.getDefault(),
                    "%s kg, %s cm, age %s, %s -> %s (BMI %s)\n",
                    record.getWeight(),
                    record.getHeight(),
                    record.getAge(),
                    record.getGender(),
                    record.getCategory(),
                    record.getBmi()));
        }
        return builder.toString();
    }

    @SuppressLint("SetTextI18n")
    private void handleError(String errorMessage, String adviceMessage) {
        binding.reportResult.setText("Error: " + errorMessage);
        binding.reportAdvice.setText(adviceMessage);
    }
}
