package com.example.mymovies.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class, FavoriteMovie.class}, version = 3, exportSchema = false)
public abstract class MovieDataBase extends RoomDatabase {

    private static final String DB_NAME = "movies.db";
    private static MovieDataBase movieDataBase;
    private static final Object LOCK = new Object();

    public static MovieDataBase getInstance(Context context) {
        synchronized (LOCK) {
            if (movieDataBase == null) {
                movieDataBase = Room.databaseBuilder(context, MovieDataBase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return movieDataBase;
    }
    public abstract MovieDao movieDao();
}
