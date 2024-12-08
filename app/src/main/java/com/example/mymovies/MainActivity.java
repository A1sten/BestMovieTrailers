package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;


import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.database.MainViewModel;
import com.example.mymovies.database.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {
    private Switch switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;

    private MainViewModel mainViewModel;

    private static final int LOADER_ID = 10;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
                break;
            case R.id.itemFavourite:
                Intent intentFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentFavourite);
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

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 160 > 2 ? width / 160 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderManager = LoaderManager.getInstance(this);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        movieAdapter = new MovieAdapter();
        switchSort = findViewById(R.id.switchSort);

        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        recyclerViewPosters.setAdapter(movieAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener((buttonView, isChecked) -> {
            page = 1;
            setMethodOfSort(isChecked);
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.onPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.onReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    downloadData(methodOfSort, page);
                }

            }
        });
        LiveData<List<Movie>> movieFromLiveData = mainViewModel.getAllMovies();
        movieFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            textViewTopRated.setTextColor(getResources().getColor(R.color.purple_200));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
            textViewPopularity.setTextColor(getResources().getColor(R.color.purple_200));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        downloadData(methodOfSort, page);
    }

    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.onStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                mainViewModel.deleteAllMovie();
                movieAdapter.clear();
            }
            for (Movie movie : movies) {
                mainViewModel.InsertMovie(movie);
            }
            movieAdapter.addMovies(movies);
            page++;
        }
        isLoading = false;
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}