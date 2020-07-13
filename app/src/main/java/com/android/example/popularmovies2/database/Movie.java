package com.android.example.popularmovies2.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Movie {
    @PrimaryKey
    @NonNull
    private String mMovieId;
    private String mTitle;
    private String mImageUrl;
    private String mOverview;
    private String mPopularity;
    private String mViewCount;
    private String mReleaseDate;

    public Movie(String movieId, String imageUrl, String title, String overview, String popularity, String viewCount, String releaseDate) {
        mMovieId = movieId;
        mTitle = title;
        mImageUrl = imageUrl;
        mOverview = overview;
        mPopularity = popularity;
        mViewCount = viewCount;
        mReleaseDate = releaseDate;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPopularity() {
        return mPopularity;
    }

    public String getViewCount() {
        return mViewCount;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
}
