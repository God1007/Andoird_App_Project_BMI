package com.example.bmicalculation.calculator;

import com.example.bmicalculation.model.Gender;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChildBmiTable {
    private static final Map<Integer, Threshold> BOY_THRESHOLDS;
    private static final Map<Integer, Threshold> GIRL_THRESHOLDS;

    static {
        Map<Integer, Threshold> boy = new HashMap<>();
        boy.put(6, new Threshold(12.8, 13.1, 18.8, 21.4));
        boy.put(7, new Threshold(13.0, 13.3, 19.8, 23.0));
        boy.put(8, new Threshold(13.2, 13.6, 20.9, 24.6));
        boy.put(9, new Threshold(13.5, 13.8, 21.8, 26.0));
        boy.put(10, new Threshold(13.8, 14.1, 22.7, 27.3));
        boy.put(11, new Threshold(14.1, 14.5, 23.6, 28.3));
        boy.put(12, new Threshold(14.4, 14.8, 24.3, 29.2));
        boy.put(13, new Threshold(14.7, 15.1, 25.0, 30.0));
        boy.put(14, new Threshold(15.0, 15.4, 25.5, 30.6));
        boy.put(15, new Threshold(15.3, 15.8, 26.1, 31.2));
        boy.put(16, new Threshold(15.6, 16.1, 26.5, 31.7));
        boy.put(17, new Threshold(15.9, 16.3, 27.0, 32.1));
        boy.put(18, new Threshold(16.1, 16.6, 27.4, 32.4));
        BOY_THRESHOLDS = Collections.unmodifiableMap(boy);

        Map<Integer, Threshold> girl = new HashMap<>();
        girl.put(6, new Threshold(12.6, 12.8, 18.3, 20.5));
        girl.put(7, new Threshold(12.8, 13.1, 19.1, 21.8));
        girl.put(8, new Threshold(13.1, 13.4, 20.1, 23.1));
        girl.put(9, new Threshold(13.4, 13.7, 21.0, 24.4));
        girl.put(10, new Threshold(13.7, 14.1, 21.9, 25.6));
        girl.put(11, new Threshold(14.1, 14.4, 22.7, 26.6));
        girl.put(12, new Threshold(14.4, 14.8, 23.4, 27.5));
        girl.put(13, new Threshold(14.8, 15.2, 24.0, 28.3));
        girl.put(14, new Threshold(15.1, 15.5, 24.6, 28.9));
        girl.put(15, new Threshold(15.4, 15.8, 25.0, 29.4));
        girl.put(16, new Threshold(15.7, 16.1, 25.4, 29.7));
        girl.put(17, new Threshold(15.9, 16.3, 25.7, 30.0));
        girl.put(18, new Threshold(16.1, 16.5, 25.9, 30.3));
        GIRL_THRESHOLDS = Collections.unmodifiableMap(girl);
    }

    private ChildBmiTable() {
    }

    public static String classify(double bmi, int age, Gender gender) {
        if (age < 6 || age > 18) {
            return "out of 6-18 years old";
        }
        Map<Integer, Threshold> thresholds = gender == Gender.MALE ? BOY_THRESHOLDS : GIRL_THRESHOLDS;
        Threshold threshold = thresholds.get(age);
        if (threshold == null) {
            return "ERROR!";
        }
        return threshold.classify(bmi);
    }

    private static class Threshold {
        private final double severeUnder;
        private final double under;
        private final double acceptable;
        private final double over;

        Threshold(double severeUnder, double under, double acceptable, double over) {
            this.severeUnder = severeUnder;
            this.under = under;
            this.acceptable = acceptable;
            this.over = over;
        }

        String classify(double bmi) {
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
    }
}
