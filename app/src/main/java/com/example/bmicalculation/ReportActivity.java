package com.example.bmicalculation;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // 初始化视图
        TextView result = findViewById(R.id.report_result);
        TextView details = findViewById(R.id.report_details);
        TextView category = findViewById(R.id.report_category);
        ImageView image = findViewById(R.id.report_image);
        TextView advice = findViewById(R.id.report_advice);

        // 获取传递过来的数据
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            handleError("No data received", "Please go back and calculate your BMI first", result, advice);
            return;
        }

        String heightStr = bundle.getString("height");
        String weightStr = bundle.getString("weight");
        String ageStr = bundle.getString("age");
        String gender = bundle.getString("gender");
        String bmiValueStr = bundle.getString("bmi");
        String bmiCategory = bundle.getString("bmi_category");

        if (heightStr == null || weightStr == null || ageStr == null || gender == null || bmiValueStr == null || bmiCategory == null) {
            handleError("Missing data", "Please provide all required information", result, advice);
            return;
        }

        try {
            double bmiValue = Double.parseDouble(bmiValueStr);
            int age = Integer.parseInt(ageStr);

            DecimalFormat nf = new DecimalFormat("0.00");

            // 显示BMI结果
            result.setText("Your BMI: " + nf.format(bmiValue));

            // 显示BMI分类
            category.setText("Category: " + bmiCategory);

            // 显示详细信息
            String detailsText = String.format(Locale.getDefault(), "Height: %s cm\nWeight: %s kg\nAge: %s years\nGender: %s",
                    heightStr, weightStr, ageStr, gender);
            details.setText(detailsText);

            // 根据BMI分类设置图片和建议
            setBmiImageAndAdvice(bmiCategory, gender, age, image, advice);

            // 展示历史记录
            TextView historyView = findViewById(R.id.report_history);
            historyView.setText(loadHistory());

        } catch (NumberFormatException e) {
            handleError("Invalid data format", "Please check your input values", result, advice);
        } catch (Exception e) {
            handleError("Unexpected error", "Please try again", result, advice);
        }
    }

    /**
     * 根据BMI分类设置图片和建议
     */
    @SuppressLint("SetTextI18n")
    private void setBmiImageAndAdvice(String bmiCategory, String gender, int age, ImageView image, TextView advice) {
        boolean isChild = age < 18;

        if (bmiCategory == null || bmiCategory.trim().isEmpty()) {
            advice.setText("Please consult a healthcare professional for advice.");
            return;
        }

        switch (bmiCategory) {
            // 成人分类
            case "Underweight":
                image.setImageResource(R.drawable.bot_thin);
                advice.setText(getUnderweightAdvice(gender, isChild));
                break;
            case "Normal Range":
                image.setImageResource(R.drawable.bot_fit);
                advice.setText(getNormalWeightAdvice(gender, isChild));
                break;
            case "Overweight":
                image.setImageResource(R.drawable.bot_fat);
                advice.setText(getOverweightAdvice(gender, isChild));
                break;
            case "Obese":
                image.setImageResource(R.drawable.bot_fat);
                advice.setText(getObeseAdvice(gender, isChild));
                break;

            // 儿童分类 - 注意：标签必须与MainActivity中返回的字符串完全一致
            case "Severely Underweight":
            case "child_Underweight":
                image.setImageResource(R.drawable.bot_thin);
                advice.setText(getSeverelyUnderweightAdvice(gender, isChild));
                break;
            case "Acceptable Weight":
                image.setImageResource(R.drawable.bot_fit);
                advice.setText(getAcceptableWeightAdvice(gender));
                break;
            case "Severely Overweight":
            case "child_Overweight":
                image.setImageResource(R.drawable.bot_fat);
                advice.setText(getSeverelyOverweightAdvice(gender, isChild));
                break;

            default:
                image.setImageResource(R.drawable.bot_fit);
                advice.setText("Please consult a healthcare professional for advice.");
        }
    }

    /**
     * 读取 BMI 历史记录并转换为可读文本
     */
    private String loadHistory() {
        SharedPreferences preferences = getSharedPreferences("BMI_Data", MODE_PRIVATE);
        String historyJson = preferences.getString("bmi_history", "[]");
        StringBuilder builder = new StringBuilder("Recent Records:\n");

        try {
            JSONArray history = new JSONArray(historyJson);
            if (history.length() == 0) {
                builder.append("No records yet.");
            } else {
                for (int i = history.length() - 1; i >= 0; i--) {
                    JSONObject record = history.getJSONObject(i);
                    builder.append(String.format(Locale.getDefault(),
                            "%s kg, %s cm, age %s, %s -> %s (BMI %s)\n",
                            record.optString("weight"),
                            record.optString("height"),
                            record.optString("age"),
                            record.optString("gender"),
                            record.optString("category"),
                            record.optString("bmi")));
                }
            }
        } catch (JSONException e) {
            builder.append("Unable to load history.");
        }

        return builder.toString();
    }

    /**
     * 根据不同性别和年龄提供个性化的建议
     */
    private String getUnderweightAdvice(String gender, boolean isChild) {
        if (isChild) {
            return "Child is underweight. " +
                    ("male".equalsIgnoreCase(gender) ?
                            "Increase nutrient-rich foods and consult a pediatrician." :
                            "Focus on balanced nutrition and regular growth monitoring.");
        }
        return "Underweight. " +
                ("male".equalsIgnoreCase(gender) ?
                        "Increase calorie intake with protein-rich foods and consider strength training." :
                        "Focus on nutrient-dense foods and moderate exercise.");
    }

    private String getNormalWeightAdvice(String gender, boolean isChild) {
        if (isChild) {
            return "Child has a healthy weight! " +
                    ("male".equalsIgnoreCase(gender) ?
                            "Maintain balanced nutrition and regular physical activity." :
                            "Keep up the good eating habits and active lifestyle.");
        }
        return "Healthy weight! " +
                ("male".equalsIgnoreCase(gender) ?
                        "Maintain your current lifestyle with regular exercise." :
                        "Keep up the good work! Continue with balanced diet.");
    }

    private String getOverweightAdvice(String gender, boolean isChild) {
        if (isChild) {
            return "Child is overweight. " +
                    ("male".equalsIgnoreCase(gender) ?
                            "Encourage more physical activity and healthy eating habits." :
                            "Focus on portion control and increase active play time.");
        }
        return "Overweight. " +
                ("male".equalsIgnoreCase(gender) ?
                        "Reduce calorie intake and increase cardiovascular exercise." :
                        "Focus on portion control and regular physical activity.");
    }

    private String getObeseAdvice(String gender, boolean isChild) {
        if (isChild) {
            return "Child is obese. " +
                    ("male".equalsIgnoreCase(gender) ?
                            "Please consult a pediatrician for a weight management plan." :
                            "Seek medical advice for a child-friendly weight management program.");
        }
        return "Obese. " +
                ("male".equalsIgnoreCase(gender) ?
                        "Consult a healthcare professional for a comprehensive plan." :
                        "Seek medical advice for a sustainable weight loss program.");
    }

    // 儿童专用建议方法
    private String getSeverelyUnderweightAdvice(String gender, boolean isChild) {
        return "Severely underweight! " +
                (isChild ?
                        "Please consult a pediatrician immediately for urgent nutritional guidance." :
                        "Consult a doctor immediately and focus on balanced nutrition");
    }

    private String getChildUnderweightAdvice(String gender) {
        return "Underweight. " +
                ("male".equalsIgnoreCase(gender) ?
                        "Child needs more nutrient-rich foods and regular growth checks." :
                        "Focus on balanced meals and consult pediatrician for guidance.");
    }

    private String getAcceptableWeightAdvice(String gender) {
        return "Healthy weight range. " +
                ("male".equalsIgnoreCase(gender) ?
                        "Maintain current nutrition and activity levels." :
                        "Continue with good eating habits and physical activity.");
    }

    private String getChildOverweightAdvice(String gender) {
        return "Overweight. " +
                ("male".equalsIgnoreCase(gender) ?
                        "Encourage more outdoor activities and limit sugary foods." :
                        "Focus on healthy snacks and increase active play time.");
    }

    private String getSeverelyOverweightAdvice(String gender, boolean isChild) {
        return "Severely overweight. " +
                (isChild ?
                        "Urgent consultation with pediatrician required for weight management." :
                        "Immediate medical advice needed for weight control.");
    }

    /**
     * 错误处理
     */
    private void handleError(String errorMessage, String adviceMessage, TextView result, TextView advice) {
        result.setText("Error: " + errorMessage);
        advice.setText(adviceMessage);
    }


}