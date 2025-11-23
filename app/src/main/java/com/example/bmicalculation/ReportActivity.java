package com.example.bmicalculation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bmicalculation.databinding.ActivityReportBinding;
import com.example.bmicalculation.model.BmiRecord;
import com.example.bmicalculation.model.Gender;
import com.example.bmicalculation.storage.BmiHistoryStorage;
import com.example.bmicalculation.ui.AdviceProvider;
import com.example.bmicalculation.ui.LlmClient;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;
    private LlmClient llmClient;
    private BmiHistoryStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        llmClient = new LlmClient();
        storage = new BmiHistoryStorage(this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            handleError(getString(R.string.error_number_format), getString(R.string.error_missing_fields));
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
            handleError(getString(R.string.error_missing_fields), getString(R.string.error_missing_fields));
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            Gender gender = Gender.fromLabel(genderStr);
            DecimalFormat formatter = new DecimalFormat("0.00");

            String bmiText = bmiDisplay != null ? bmiDisplay : formatter.format(bmiValue);
            binding.reportResult.setText(getString(R.string.report_bmi_value, bmiText));
            binding.reportCategory.setText(getString(R.string.report_category, bmiCategory));

            String detailsText = getString(R.string.report_details, heightStr, weightStr, ageStr, genderStr);
            binding.reportDetails.setText(detailsText);

            AdviceProvider.Advice advice = new AdviceProvider().provide(bmiCategory, gender, age < 18);
            binding.reportImage.setImageResource(advice.getImageRes());
            binding.reportAdvice.setText(advice.getMessage());

            binding.reportHistory.setText(buildHistoryText());
            binding.viewTrendButton.setOnClickListener(v -> startActivity(new Intent(this, BmiTrendActivity.class)));
            requestLlmSuggestion(bmiValue, bmiCategory, gender, age);
        } catch (NumberFormatException e) {
            handleError(getString(R.string.error_number_format), getString(R.string.error_number_format));
        }
    }

    private String buildHistoryText() {
        List<BmiRecord> records = storage.loadHistory();
        if (records.isEmpty()) {
            return getString(R.string.report_history_title) + "\n" + getString(R.string.report_no_history);
        }
        StringBuilder builder = new StringBuilder(getString(R.string.report_history_title)).append("\n");
        for (int i = records.size() - 1; i >= 0; i--) {
            BmiRecord record = records.get(i);
            builder.append(String.format(Locale.getDefault(),
                    "%s: %s kg, %s cm, age %s, %s -> %s (BMI %s)\n",
                    record.getRecordDate(),
                    record.getWeight(),
                    record.getHeight(),
                    record.getAge(),
                    record.getGender(),
                    record.getCategory(),
                    record.getBmi()));
        }
        return builder.toString();
    }

    private void requestLlmSuggestion(double bmiValue, String bmiCategory, Gender gender, int age) {
        binding.llmAdvice.setText(R.string.llm_loading);
        llmClient.requestSuggestion(
                this,
                bmiValue,
                bmiCategory,
                gender,
                age,
                getResources().getConfiguration().getLocales().get(0),
                suggestion -> binding.llmAdvice.setText(suggestion)
        );
    }

    @SuppressLint("SetTextI18n")
    private void handleError(String errorMessage, String adviceMessage) {
        binding.reportResult.setText(getString(R.string.error_label, errorMessage));
        binding.reportAdvice.setText(adviceMessage);
    }
}
