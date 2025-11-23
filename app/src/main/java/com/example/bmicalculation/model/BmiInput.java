package com.example.bmicalculation.model;

public class BmiInput {
    private final double heightCm;
    private final double weightKg;
    private final int age;
    private final Gender gender;

    public BmiInput(double heightCm, double weightKg, int age, Gender gender) {
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.age = age;
        this.gender = gender;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean isChild() {
        return age < 18;
    }
}
