package com.example.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";


    private static final String API_KEY = "ac6f0b3e89da4f505ff6e4286728507d";
    private static final String PARAMS_API_KEY = "api_key";

    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    private static final String PARAMS_LANGUAGE = "language";
    private static final String LANGUAGE_VALUE = "ru-RU";

    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    //кол-во голосов фильма
    private static final String PARAMS_VOTE_COUNT = "vote_count.gte";
    private static final String MIN_VOTE_COUNT_VALUE = "800";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    public static URL buildURLtoReviews(int id){
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL buildURLtoVideos(int id){
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL buildURL(int sortBy, int page) {
        URL result = null;
        String methodOfSort;
        if(sortBy == POPULARITY){
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForReviews(int id) {
        JSONObject result = null;
        URL url = buildURLtoReviews(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static JSONObject getJSONForVidios(int id) {
        JSONObject result = null;
        URL url = buildURLtoVideos(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        JSONObject result = null;
        URL url = buildURL(sortBy, page);
        try {
           result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {
        private Bundle bundle;
        private onStartLoadingListener onStartLoadingListener;

        public interface onStartLoadingListener {
            void onStartLoading();
        }

        public void setOnStartLoadingListener(JSONLoader.onStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if(onStartLoadingListener != null){
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if(bundle == null){
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
                JSONObject result = null;
                if (url == null) {
                    return null;
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while(line != null){
                        builder.append(line);
                        line = bufferedReader.readLine();
                    }
                    result = new JSONObject(builder.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return result;
        }
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if (urls == null || urls.length == 0) {
                return null;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while(line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }
}
