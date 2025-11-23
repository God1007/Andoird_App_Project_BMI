package com.example.bmicalculation.calculator;

public class AdultBmiClassifier {

    private AdultBmiClassifier() {
    }

    public static String classify(double bmi) {
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
}
