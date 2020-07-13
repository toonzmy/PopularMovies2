package com.android.example.popularmovies2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.example.popularmovies2.database.AppDatabase;
import com.android.example.popularmovies2.database.Movie;

public class MovieViewModel extends AndroidViewModel {

    private LiveData<Movie[]> movies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movies = database.favMovieDao().loadAllFabMovies();
    }

    public LiveData<Movie[]> getMovies(){
        return movies;
    }
}
