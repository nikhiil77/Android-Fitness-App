package com.example.androidfitness.models;

public class Exercise {
    private final int id;
    private final String name;
    private final String image;
    private final String category;
    private final String description;  // Add this
    private final String videoUrl;     // Add this

    // Updated constructor
    public Exercise(int id, String name, String image, String category,
                    String description, String videoUrl) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.category = category;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }  // Add this
    public String getVideoUrl() { return videoUrl; }        // Add this
}