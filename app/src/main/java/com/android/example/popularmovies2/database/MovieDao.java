package com.android.example.popularmovies2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<Movie[]> loadAllFabMovies();

    @Insert
    void insertFabMovie(Movie fabMovie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavMovie(Movie favMovie);

    @Delete
    void deleteFabMovie(Movie favMovie);

    @Query("SELECT * FROM Movie WHERE mMovieId = :mMovieId")
    LiveData<Movie> loadFabMovieById(int mMovieId);
}
