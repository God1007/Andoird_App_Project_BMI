package com.example.bmicalculation.model;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String displayValue;

    Gender(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static Gender fromLabel(String label) {
        if (label == null) {
            return MALE;
        }
        String normalized = label.trim().toLowerCase();
        return normalized.startsWith("f") ? FEMALE : MALE;
    }
}
