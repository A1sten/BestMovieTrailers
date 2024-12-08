package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymovies.adapters.ReviewAdapter;
import com.example.mymovies.adapters.TrailerAdapter;
import com.example.mymovies.database.FavoriteMovie;
import com.example.mymovies.database.MainViewModel;
import com.example.mymovies.database.Movie;
import com.example.mymovies.database.Reviews;
import com.example.mymovies.database.Trailer;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewAddToFavorite;
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRaiting;
    private TextView textViewRealiseDate;
    private TextView textViewDescriptionOverView;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerAdapter trailersAdapter;
    private ReviewAdapter reviewAdapter;

    private int id;
    private Movie movie;
    private FavoriteMovie favoriteMovie;
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
                Intent intentFavorit = new Intent(this, FavouriteActivity.class);
                startActivity(intentFavorit);
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
        setContentView(R.layout.activity_detail);
        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavorite);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRaiting = findViewById(R.id.textViewRaiting);
        textViewRealiseDate = findViewById(R.id.textViewRealiseDate);
        textViewDescriptionOverView = findViewById(R.id.textViewDescriptionOverView);

        
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
            return;
        }
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = mainViewModel.getMovieById(id);
        if (movie != null) {

            Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
            textViewTitle.setText(movie.getTitle());
            textViewOriginalTitle.setText(movie.getOriginalTitle());
            textViewRealiseDate.setText(movie.getReleaseDate());
            textViewDescriptionOverView.setText(movie.getOverview());
            textViewRaiting.setText(Double.toString(movie.getVoteAverage()));
            setFavoriteMovie();
        } else {
            finish();
        }
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);

        trailersAdapter = new TrailerAdapter();
        reviewAdapter = new ReviewAdapter();
        trailersAdapter.setOnTrailerClickListener(new TrailerAdapter.onTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });

        recyclerViewTrailers.setAdapter(trailersAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVidios(movie.getId());
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Reviews> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);

        reviewAdapter.setReviews(reviews);
        trailersAdapter.setTrailers(trailers);

    }

    public void onClickChangeFavorite(View view) {
        favoriteMovie = mainViewModel.getFavoriteMovieById(id);
        if (favoriteMovie == null) {
            mainViewModel.InsertFavoriteMovie(new FavoriteMovie(movie));
        } else {
            mainViewModel.DeleteFavoriteMovie(favoriteMovie);
        }
        setFavoriteMovie();
    }

    private void setFavoriteMovie() {
        favoriteMovie = mainViewModel.getFavoriteMovieById(id);
        if (favoriteMovie == null) {
            imageViewAddToFavorite.setImageResource(R.drawable.like_gray);
        } else {
            imageViewAddToFavorite.setImageResource(R.drawable.like_red);

        }
    }
}