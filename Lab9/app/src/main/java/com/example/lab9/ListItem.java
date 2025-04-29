package com.example.lab9;

public class ListItem {
    private String title;
    private String description;
    private int imageResId;
    private String url;

    public ListItem(String title, String description, int imageResId, String url) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public String getUrl() { return url; }
}
