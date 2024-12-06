package com.example.mymovies.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.database.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private onPosterClickListener onPosterClickListener;
    public MovieAdapter.onReachEndListener onReachEndListener;

    public MovieAdapter(){
        movies = new ArrayList<>();
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(view);
    }

    public interface onPosterClickListener {
        void onPosterClick(int position);
    }
    public interface onReachEndListener {
        void onReachEnd();
    }

    public void setOnReachEndListener(MovieAdapter.onReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    public void setOnPosterClickListener(MovieAdapter.onPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if(movies.size() >= 20 && position > movies.size() - 4 && onReachEndListener !=  null) {
            onReachEndListener.onReachEnd();
        }
    Movie movie = movies.get(position);
        Picasso.get().load(movie.getPosterPath()).into(holder.imageViewSmallPoster);
    }
    @Override
    public int getItemCount() {
        return movies.size();
    }
    class MovieViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageViewSmallPoster;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null){
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void clear(){
        this.movies.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void  addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
      }

    public List<Movie> getMovies() {
        return movies;
    }
}
