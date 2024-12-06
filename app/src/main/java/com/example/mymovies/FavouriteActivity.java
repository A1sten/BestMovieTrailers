package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.database.FavoriteMovie;
import com.example.mymovies.database.MainViewModel;
import com.example.mymovies.database.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavoriteList;
    private MovieAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.itemMain:
                Intent intentMain = new Intent (this, MainActivity.class);
                startActivity(intentMain);
                break;
            case R.id.itemFavourite:
                Intent itemFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(itemFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        recyclerViewFavoriteList = findViewById(R.id.recyclerViewFavoriteList);
        adapter = new MovieAdapter();
        recyclerViewFavoriteList.setAdapter(adapter);
        recyclerViewFavoriteList.setLayoutManager(new GridLayoutManager(this, 2));
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        LiveData<List<FavoriteMovie>> favoriteMovie = mainViewModel.getFavoriteMovies();
        favoriteMovie.observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(List<FavoriteMovie> favoriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favoriteMovie != null) {
                    movies.addAll(favoriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });
        adapter.setOnPosterClickListener(new MovieAdapter.onPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie  = adapter.getMovies().get(position);
                Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
    }
}