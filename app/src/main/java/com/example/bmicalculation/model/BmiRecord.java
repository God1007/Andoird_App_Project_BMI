package com.example.bmicalculation.model;

import java.time.LocalDate;

public class BmiRecord {
    private final String height;
    private final String weight;
    private final String age;
    private final String gender;
    private final String bmi;
    private final String category;
    private final LocalDate recordDate;

    public BmiRecord(String height, String weight, String age, String gender, String bmi, String category, LocalDate recordDate) {
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.bmi = bmi;
        this.category = category;
        this.recordDate = recordDate;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }
}
