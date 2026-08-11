package com.example.navermapsample;

public class MarkerInfo {
    private String title;
    private String description;
    private String category;
    private String location;

    public MarkerInfo(String title, String description, String category, String location) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }
}