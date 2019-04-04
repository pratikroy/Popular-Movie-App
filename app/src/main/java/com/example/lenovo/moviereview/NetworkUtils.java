package com.example.lenovo.moviereview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by LENOVO on 5/20/2018.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIE_URL = "https://api.themoviedb.org/3/movie";
    private final static String api_key = "d4c5e04307784218b85a63f0a9d67a4a";
    private final static String API_KEY = "api_key";

    public static URL builtURL(String searchQuery){

        Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                .appendPath(searchQuery)
                .appendQueryParameter(API_KEY, api_key)
                .build();

        URL Url = null;
        try {
            Url = new URL(builtUri.toString());
            Log.v(TAG, "URL = " + Url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI" + Url);
        return Url;
    }

    public static URL builtUrlForVideosAndReviews(String movieId, String searchQuery){
        Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(searchQuery)
                .appendQueryParameter(API_KEY, api_key)
                .build();

        URL Url = null;
        try{
            Url = new URL(builtUri.toString());
            Log.v(TAG, "URL = " + Url);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return Url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else{
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // Check whether network connection is available or not
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
