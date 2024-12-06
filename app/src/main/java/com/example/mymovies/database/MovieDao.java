package com.example.mymovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovie();

    @Query("SELECT * FROM favoriteMovies")
    LiveData<List<FavoriteMovie>> getAllFavoriteMovie();

    @Query("SELECT * FROM movies WHERE id = :movieId")
    Movie getMovieByIdSync(int movieId);

    @Query("SELECT * FROM favoriteMovies WHERE id = :movieId")
    FavoriteMovie getFavoriteMovieByIdSync(int movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

    @Insert
    void insertMovie(Movie... movies);

    @Delete
    void deleteMovie(Movie movie);

    @Insert
    void insertFavoriteMovie(FavoriteMovie movies);

    @Delete
    void deleteFavoriteMovie(FavoriteMovie movie);
}
