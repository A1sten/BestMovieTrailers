package com.example.mymovies.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static MovieDataBase movieDataBase;
    private LiveData <List<Movie>> allMovies;
    private LiveData <List<FavoriteMovie>> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        movieDataBase = MovieDataBase.getInstance(application);
        allMovies = movieDataBase.movieDao().getAllMovie();
        favoriteMovies = movieDataBase.movieDao().getAllFavoriteMovie();
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public Movie getMovieById(int movieId){
        try {
            return new GetMovieTask().execute(movieId).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public FavoriteMovie getFavoriteMovieById(int movieId){
        try {
            return new GetFavoriteMovieTask().execute(movieId).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAllMovie(){
        new DeleteMovieTask().execute();
    }
public void InsertMovie(Movie  movie){
    new InsertMovieTask().execute(movie);
}
    public void DeleteMovie(Movie  movie){
        new DeleteTask().execute(movie);
    }

    public void InsertFavoriteMovie(FavoriteMovie  movie){
        new InsertFavoriteMovieTask().execute(movie);
    }
    public void DeleteFavoriteMovie(FavoriteMovie  movie){
        new DeleteFavoriteMovieTask().execute(movie);
    }

    public LiveData<List<Movie>> getAllMovies() {
        return allMovies;
    }

    private static class DeleteTask extends AsyncTask<Movie, Void, Void>{
        @Override
        protected Void doInBackground(Movie... movies) {
            if(movies != null && movies.length > 0){
                movieDataBase.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                movieDataBase.movieDao().insertFavoriteMovie(movies[0]);
            }
            return null;
        }
    }
    private static class DeleteFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                movieDataBase.movieDao().deleteFavoriteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertMovieTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                movieDataBase.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }
    private static class DeleteMovieTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
                movieDataBase.movieDao().deleteAllMovies();
            return null;
        }
    }
    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... ids) {
            if (ids != null && ids.length > 0) {
                return movieDataBase.movieDao().getMovieByIdSync(ids[0]);
            }
            return null;
        }
    }

    private static class GetFavoriteMovieTask extends AsyncTask<Integer, Void, FavoriteMovie> {
        @Override
        protected FavoriteMovie doInBackground(Integer... ids) {
            if (ids != null && ids.length > 0) {
                return movieDataBase.movieDao().getFavoriteMovieByIdSync(ids[0]);
            }
            return null;
        }
    }

}
