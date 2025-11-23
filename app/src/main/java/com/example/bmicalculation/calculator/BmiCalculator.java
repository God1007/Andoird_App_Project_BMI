package com.example.bmicalculation.calculator;

import com.example.bmicalculation.model.BmiInput;
import com.example.bmicalculation.model.BmiResult;
import com.example.bmicalculation.model.Gender;
import java.util.Locale;

public class BmiCalculator {

    public BmiResult calculate(BmiInput input) {
        double heightMeters = input.getHeightCm() / 100.0;
        double bmi = input.getWeightKg() / (heightMeters * heightMeters);
        String display = String.format(Locale.getDefault(), "%.2f", bmi);
        String category = input.isChild()
                ? ChildBmiTable.classify(bmi, input.getAge(), input.getGender())
                : AdultBmiClassifier.classify(bmi);
        return new BmiResult(bmi, display, category);
    }

    public static BmiInput createInput(double heightCm, double weightKg, int age, String genderLabel) {
        Gender gender = Gender.fromLabel(genderLabel);
        return new BmiInput(heightCm, weightKg, age, gender);
    }
}
