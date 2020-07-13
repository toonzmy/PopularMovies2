package com.android.example.popularmovies2.database;

public class Trailer {

    private int id;
    private String key;
    private String site;
    private String title;

    public Trailer(String key, String site, String title) {
        this.key = key;
        this.site = site;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public String getTitle() {
        return title;
    }
}
