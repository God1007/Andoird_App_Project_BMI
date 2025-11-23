package com.example.bmicalculation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.bmicalculation.databinding.ActivityBmiTrendBinding;
import com.example.bmicalculation.model.BmiRecord;
import com.example.bmicalculation.storage.BmiHistoryStorage;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BmiTrendActivity extends AppCompatActivity {

    private ActivityBmiTrendBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBmiTrendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BmiHistoryStorage storage = new BmiHistoryStorage(this);
        List<BmiRecord> records = storage.loadHistory();
        renderChart(records);

        binding.trendBack.setOnClickListener(v -> finish());
    }

    private void renderChart(List<BmiRecord> records) {
        if (records.isEmpty()) {
            binding.trendHint.setText(R.string.trend_empty);
            binding.bmiChart.setNoDataText(getString(R.string.trend_empty));
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            BmiRecord record = records.get(i);
            entries.add(new Entry(i, Float.parseFloat(record.getBmi())));
            labels.add(record.getRecordDate().toString());
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.trend_title));
        dataSet.setValueTextSize(12f);
        dataSet.setColor(ContextCompat.getColor(this, R.color.purple_500));
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleRadius(2f);

        LineData lineData = new LineData(dataSet);
        binding.bmiChart.setData(lineData);

        Description description = new Description();
        description.setText("");
        binding.bmiChart.setDescription(description);

        Legend legend = binding.bmiChart.getLegend();
        legend.setTextSize(12f);

        binding.bmiChart.getXAxis().setGranularity(1f);
        binding.bmiChart.getXAxis().setValueFormatter((value, axis) -> {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.get(index);
            }
            return String.format(Locale.getDefault(), "%d", index);
        });

        binding.bmiChart.invalidate();
    }
}
