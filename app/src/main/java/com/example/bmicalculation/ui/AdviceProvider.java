package com.example.bmicalculation.ui;

import com.example.bmicalculation.R;
import com.example.bmicalculation.model.Gender;

public class AdviceProvider {

    public Advice provide(String category, Gender gender, boolean isChild) {
        int imageRes = pickImage(category);
        String adviceText = pickAdvice(category, gender, isChild);
        return new Advice(imageRes, adviceText);
    }

    private int pickImage(String category) {
        if (category == null) {
            return R.drawable.bot_fit;
        }
        switch (category) {
            case "Underweight":
            case "Severely Underweight":
            case "child_Underweight":
                return R.drawable.bot_thin;
            case "Normal Range":
            case "Acceptable Weight":
                return R.drawable.bot_fit;
            case "Overweight":
            case "Obese":
            case "Severely Overweight":
            case "child_Overweight":
                return R.drawable.bot_fat;
            default:
                return R.drawable.bot_fit;
        }
    }

    private String pickAdvice(String category, Gender gender, boolean isChild) {
        if (category == null || category.trim().isEmpty()) {
            return "Please consult a healthcare professional for advice.";
        }
        switch (category) {
            case "Underweight":
                return getUnderweightAdvice(gender, isChild);
            case "Normal Range":
                return getNormalWeightAdvice(gender, isChild);
            case "Overweight":
                return getOverweightAdvice(gender, isChild);
            case "Obese":
                return getObeseAdvice(gender, isChild);
            case "Severely Underweight":
            case "child_Underweight":
                return getSeverelyUnderweightAdvice(gender, isChild);
            case "Acceptable Weight":
                return getAcceptableWeightAdvice(gender);
            case "Severely Overweight":
            case "child_Overweight":
                return getSeverelyOverweightAdvice(gender, isChild);
            default:
                return "Please consult a healthcare professional for advice.";
        }
    }

    private String getUnderweightAdvice(Gender gender, boolean isChild) {
        if (isChild) {
            return "Child is underweight. " +
                    (gender == Gender.MALE ?
                            "Increase nutrient-rich foods and consult a pediatrician." :
                            "Focus on balanced nutrition and regular growth monitoring.");
        }
        return "Underweight. " +
                (gender == Gender.MALE ?
                        "Increase calorie intake with protein-rich foods and consider strength training." :
                        "Focus on nutrient-dense foods and moderate exercise.");
    }

    private String getNormalWeightAdvice(Gender gender, boolean isChild) {
        if (isChild) {
            return "Child has a healthy weight! " +
                    (gender == Gender.MALE ?
                            "Maintain balanced nutrition and regular physical activity." :
                            "Keep up the good eating habits and active lifestyle.");
        }
        return "Healthy weight! " +
                (gender == Gender.MALE ?
                        "Maintain your current lifestyle with regular exercise." :
                        "Keep up the good work! Continue with balanced diet.");
    }

    private String getOverweightAdvice(Gender gender, boolean isChild) {
        if (isChild) {
            return "Child is overweight. " +
                    (gender == Gender.MALE ?
                            "Encourage more physical activity and healthy eating habits." :
                            "Focus on portion control and increase active play time.");
        }
        return "Overweight. " +
                (gender == Gender.MALE ?
                        "Reduce calorie intake and increase cardiovascular exercise." :
                        "Focus on portion control and regular physical activity.");
    }

    private String getObeseAdvice(Gender gender, boolean isChild) {
        if (isChild) {
            return "Child is obese. " +
                    (gender == Gender.MALE ?
                            "Please consult a pediatrician for a weight management plan." :
                            "Seek medical advice for a child-friendly weight management program.");
        }
        return "Obese. " +
                (gender == Gender.MALE ?
                        "Consult a healthcare professional for a comprehensive plan." :
                        "Seek medical advice for a sustainable weight loss program.");
    }

    private String getSeverelyUnderweightAdvice(Gender gender, boolean isChild) {
        return "Severely underweight. " +
                (isChild ?
                        "Please consult a pediatrician immediately for urgent nutritional guidance." :
                        "Consult a doctor immediately and focus on balanced nutrition.");
    }

    private String getAcceptableWeightAdvice(Gender gender) {
        return "Healthy weight range. " +
                (gender == Gender.MALE ?
                        "Maintain current nutrition and activity levels." :
                        "Continue with good eating habits and physical activity.");
    }

    private String getSeverelyOverweightAdvice(Gender gender, boolean isChild) {
        return "Severely overweight. " +
                (isChild ?
                        "Urgent consultation with pediatrician required for weight management." :
                        "Immediate medical advice needed for weight control.");
    }

    public static class Advice {
        private final int imageRes;
        private final String message;

        public Advice(int imageRes, String message) {
            this.imageRes = imageRes;
            this.message = message;
        }

        public int getImageRes() {
            return imageRes;
        }

        public String getMessage() {
            return message;
        }
    }
}
