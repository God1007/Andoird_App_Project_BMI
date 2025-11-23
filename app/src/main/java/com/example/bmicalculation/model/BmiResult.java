package com.example.bmicalculation.model;

public class BmiResult {
    private final double bmiValue;
    private final String bmiDisplay;
    private final String category;

    public BmiResult(double bmiValue, String bmiDisplay, String category) {
        this.bmiValue = bmiValue;
        this.bmiDisplay = bmiDisplay;
        this.category = category;
    }

    public double getBmiValue() {
        return bmiValue;
    }

    public String getBmiDisplay() {
        return bmiDisplay;
    }

    public String getCategory() {
        return category;
    }
}
