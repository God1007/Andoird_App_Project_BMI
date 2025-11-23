package com.example.bmicalculation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText vHeight, vWeight, vAge;
    private Button submitButton;
    private Button bmiTableChoiceButton;
    private RadioGroup genderRadioGroup;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences initialization
        sharedPreferences = getSharedPreferences("BMI_Data", MODE_PRIVATE);

        // 初始化视图组件
        vHeight = findViewById(R.id.heightET);
        vWeight = findViewById(R.id.weightET);
        vAge = findViewById(R.id.yearsold);
        submitButton = findViewById(R.id.reportBtn);
        bmiTableChoiceButton = findViewById(R.id.BMI_table_choice_button);
        genderRadioGroup = findViewById(R.id.gender_radio_group);

        // 加载保存的数据
        loadSavedData();

        // 设置按钮点击监听器
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });

        // BMI表格选择按钮
        bmiTableChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BMIPictureChoiceActivity.class);
                startActivity(intent);
            }
        });
    }

    // 加载保存的数据
    private void loadSavedData() {
        String savedHeight = sharedPreferences.getString("height", "");
        String savedWeight = sharedPreferences.getString("weight", "");
        String savedAge = sharedPreferences.getString("age", "");

        vHeight.setText(savedHeight);
        vWeight.setText(savedWeight);
        vAge.setText(savedAge);

        // 可以添加性别加载逻辑
        String savedGender = sharedPreferences.getString("gender", "male");
        if (savedGender.equalsIgnoreCase("male")) {
            genderRadioGroup.check(R.id.male_radio);
        } else {
            genderRadioGroup.check(R.id.female_radio);
        }
    }

    // 更新后的BMI计算方法
    private void calculateBMI() {
        // 获取输入的身高、体重、年龄
        String heightStr = vHeight.getText().toString().trim();
        String weightStr = vWeight.getText().toString().trim();
        String ageStr = vAge.getText().toString().trim();

        // 验证输入是否为空
        if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "plz input the height, weight and years", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 获取选择的性别
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                Toast.makeText(this, "plz choose gender", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedGenderRadio = findViewById(selectedGenderId);
            String gender = selectedGenderRadio.getText().toString();

            double height = Double.parseDouble(heightStr) / 100; // 转换为米
            double weight = Double.parseDouble(weightStr);
            int age = Integer.parseInt(ageStr);

            if (height <= 0 || weight <= 0) {
                Toast.makeText(this, "height & weight must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            double bmi = weight / (height * height);

            // 保存数据到SharedPreferences
            saveData(heightStr, weightStr, ageStr, gender);

            // 根据年龄选择不同的评判标准
            String bmiCategory;
            if (age >= 18) {
                // 成人标准 (WHO亚洲标准)
                bmiCategory = getAdultBMICategory(bmi);
            } else {
                // 儿童标准 (需要根据年龄和性别查询表格)
                bmiCategory = getChildBMICategory(bmi, age, gender);
            }

            // 创建Intent跳转到ReportActivity
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            intent.putExtra("height", heightStr);
            intent.putExtra("weight", weightStr);
            intent.putExtra("age", ageStr);
            intent.putExtra("gender", gender);
            intent.putExtra("bmi", String.valueOf(bmi));
            intent.putExtra("bmi_category", bmiCategory);

            // 启动Activity
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "input valid number: ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Error occured : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 成人BMI分类 (WHO亚洲标准)
    private String getAdultBMICategory(double bmi) {
        if (bmi >= 25.0) {
            return "Obese";
        } else if (bmi >= 23.0 && bmi <= 24.9) {
            return "Overweight";
        } else if (bmi >= 18.5 && bmi <= 22.9) {
            return "Normal Range";
        } else {
            return "Underweight";
        }
    }

    // 儿童BMI分类 (根据提供的表格数据)
    private String getChildBMICategory(double bmi, int age, String gender) {
        if (age < 6 || age > 18) {
            return "out of 6-18 years old";
        }

        // 根据性别选择不同的标准
        if (gender.equals("male") || gender.equals("Male")) {
            return getBoyBMICategory(bmi, age);
        } else {
            return getGirlBMICategory(bmi, age);
        }
    }

    // 男孩BMI分类
    private String getBoyBMICategory(double bmi, int age) {
        switch (age) {
            case 6: return categorizeBoyBMI(bmi, 12.8, 13.1, 18.8, 21.4);
            case 7: return categorizeBoyBMI(bmi, 13.0, 13.3, 19.8, 23.0);
            case 8: return categorizeBoyBMI(bmi, 13.2, 13.6, 20.9, 24.6);
            case 9: return categorizeBoyBMI(bmi, 13.5, 13.8, 21.8, 26.0);
            case 10: return categorizeBoyBMI(bmi, 13.8, 14.1, 22.7, 27.3);
            case 11: return categorizeBoyBMI(bmi, 14.1, 14.5, 23.6, 28.3);
            case 12: return categorizeBoyBMI(bmi, 14.4, 14.8, 24.3, 29.2);
            case 13: return categorizeBoyBMI(bmi, 14.7, 15.1, 25.0, 30.0);
            case 14: return categorizeBoyBMI(bmi, 15.0, 15.4, 25.5, 30.6);
            case 15: return categorizeBoyBMI(bmi, 15.3, 15.8, 26.1, 31.2);
            case 16: return categorizeBoyBMI(bmi, 15.6, 16.1, 26.5, 31.7);
            case 17: return categorizeBoyBMI(bmi, 15.9, 16.3, 27.0, 32.1);
            case 18: return categorizeBoyBMI(bmi, 16.1, 16.6, 27.4, 32.4);
            default: return "ERROR!";
        }
    }

    // 女孩BMI分类
    private String getGirlBMICategory(double bmi, int age) {
        switch (age) {
            case 6: return categorizeGirlBMI(bmi, 12.6, 12.8, 18.3, 20.5);
            case 7: return categorizeGirlBMI(bmi, 12.8, 13.1, 19.1, 21.8);
            case 8: return categorizeGirlBMI(bmi, 13.1, 13.4, 20.1, 23.1);
            case 9: return categorizeGirlBMI(bmi, 13.4, 13.7, 21.0, 24.4);
            case 10: return categorizeGirlBMI(bmi, 13.7, 14.1, 21.9, 25.6);
            case 11: return categorizeGirlBMI(bmi, 14.1, 14.4, 22.7, 26.6);
            case 12: return categorizeGirlBMI(bmi, 14.4, 14.8, 23.4, 27.5);
            case 13: return categorizeGirlBMI(bmi, 14.8, 15.2, 24.0, 28.3);
            case 14: return categorizeGirlBMI(bmi, 15.1, 15.5, 24.6, 28.9);
            case 15: return categorizeGirlBMI(bmi, 15.4, 15.8, 25.0, 29.4);
            case 16: return categorizeGirlBMI(bmi, 15.7, 16.1, 25.4, 29.7);
            case 17: return categorizeGirlBMI(bmi, 15.9, 16.3, 25.7, 30.0);
            case 18: return categorizeGirlBMI(bmi, 16.1, 16.5, 25.9, 30.3);
            default: return "ERROR!";
        }
    }

    // 男孩BMI分类逻辑
    private String categorizeBoyBMI(double bmi, double severeUnder, double under, double acceptable, double over) {
        if (bmi <= severeUnder) {
            return "Severely Underweight";
        } else if (bmi <= under) {
            return "child_Underweight";
        } else if (bmi <= acceptable) {
            return "Acceptable Weight";
        } else if (bmi <= over) {
            return "child_Overweight";
        } else {
            return "Severely Overweight";
        }
    }

    // 女孩BMI分类逻辑
    private String categorizeGirlBMI(double bmi, double severeUnder, double under, double acceptable, double over) {
        if (bmi <= severeUnder) {
            return "Severely Underweight";
        } else if (bmi <= under) {
            return "child_Underweight";
        } else if (bmi <= acceptable) {
            return "Acceptable Weight";
        } else if (bmi <= over) {
            return "child_Overweight";
        } else {
            return "Severely Overweight";
        }
    }

    // 保存数据到SharedPreferences
    private void saveData(String height, String weight, String age, String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("age", age);
        editor.putString("gender", gender.toLowerCase(Locale.ROOT));
        editor.apply();
    }
}
