package com.example.androidfitness.models;

public class Workout {
    private String name;
    private int imageResId;

    public Workout(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
