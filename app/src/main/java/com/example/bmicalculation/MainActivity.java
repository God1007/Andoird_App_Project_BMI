package com.example.bmicalculation;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bmicalculation.calculator.BmiCalculator;
import com.example.bmicalculation.databinding.ActivityMainBinding;
import com.example.bmicalculation.model.BmiInput;
import com.example.bmicalculation.model.BmiResult;
import com.example.bmicalculation.model.Gender;
import com.example.bmicalculation.storage.BmiHistoryStorage;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_HEIGHT = "height";
    public static final String EXTRA_WEIGHT = "weight";
    public static final String EXTRA_AGE = "age";
    public static final String EXTRA_GENDER = "gender";
    public static final String EXTRA_BMI_VALUE = "bmi_value";
    public static final String EXTRA_BMI_DISPLAY = "bmi_display";
    public static final String EXTRA_BMI_CATEGORY = "bmi_category";

    private ActivityMainBinding binding;
    private BmiCalculator calculator;
    private BmiHistoryStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calculator = new BmiCalculator();
        storage = new BmiHistoryStorage(this);

        restoreForm();
        setListeners();
        registerForContextMenu(binding.reportBtn);
    }

    private void setListeners() {
        binding.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalculateClicked();
            }
        });

        binding.bmiTableChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BMIPictureChoiceActivity.class));
            }
        });

        binding.trendButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BmiTrendActivity.class)));
    }

    private void onCalculateClicked() {
        String heightText = binding.heightET.getText().toString().trim();
        String weightText = binding.weightET.getText().toString().trim();
        String ageText = binding.yearsold.getText().toString().trim();

        if (heightText.isEmpty() || weightText.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this, R.string.error_missing_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        Gender gender = getSelectedGender();
        if (gender == null) {
            Toast.makeText(this, R.string.error_choose_gender, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double heightCm = Double.parseDouble(heightText);
            double weightKg = Double.parseDouble(weightText);
            int age = Integer.parseInt(ageText);

            if (heightCm <= 0 || weightKg <= 0) {
                Toast.makeText(this, R.string.error_positive, Toast.LENGTH_SHORT).show();
                return;
            }

            BmiInput input = new BmiInput(heightCm, weightKg, age, gender);
            BmiResult result = calculator.calculate(input);

            storage.persistForm(heightText, weightText, ageText, gender.getDisplayValue().toLowerCase());
            storage.addRecord(input, result);

            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            intent.putExtra(EXTRA_HEIGHT, heightText);
            intent.putExtra(EXTRA_WEIGHT, weightText);
            intent.putExtra(EXTRA_AGE, ageText);
            intent.putExtra(EXTRA_GENDER, gender.getDisplayValue());
            intent.putExtra(EXTRA_BMI_VALUE, result.getBmiValue());
            intent.putExtra(EXTRA_BMI_DISPLAY, result.getBmiDisplay());
            intent.putExtra(EXTRA_BMI_CATEGORY, result.getCategory());
            startActivity(intent);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_number_format, Toast.LENGTH_SHORT).show();
        }
    }

    private Gender getSelectedGender() {
        int selectedId = binding.genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return null;
        }
        RadioButton button = findViewById(selectedId);
        return Gender.fromLabel(button.getText().toString());
    }

    private void restoreForm() {
        BmiHistoryStorage.FormState formState = storage.loadForm();
        binding.heightET.setText(formState.getHeight());
        binding.weightET.setText(formState.getWeight());
        binding.yearsold.setText(formState.getAge());

        String savedGender = formState.getGender();
        if (savedGender.toLowerCase().startsWith("f")) {
            binding.femaleRadio.setChecked(true);
        } else {
            binding.maleRadio.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_wiki) {
            openWikipedia();
            return true;
        } else if (id == R.id.menu_exit) {
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.menu_header);
        getMenuInflater().inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    private void openWikipedia() {
        String language = getResources().getConfiguration().getLocales().get(0).getLanguage();
        String localePrefix = language.startsWith("zh") ? "zh" : "en";
        Uri wikiUri = Uri.parse("https://" + localePrefix + ".wikipedia.org/wiki/Body_mass_index");
        startActivity(new Intent(Intent.ACTION_VIEW, wikiUri));
    }
}
