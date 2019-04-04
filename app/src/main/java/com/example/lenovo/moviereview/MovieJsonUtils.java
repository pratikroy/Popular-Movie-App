package com.example.lenovo.moviereview;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by LENOVO on 5/20/2018.
 */

public final class MovieJsonUtils {

    public static ArrayList<MovieDetails> getSimpleMovieStringsFromJson
            (Context context, String movieJsonStr) throws JSONException{

        ArrayList<MovieDetails> movieList = new ArrayList<MovieDetails>();

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieJsonArray = movieJson.getJSONArray("results");

        for(int i = 0; i < movieJsonArray.length(); i++){
            JSONObject movieObj = movieJsonArray.getJSONObject(i);
            String movieId = movieObj.getString("id");
            String title = movieObj.getString("original_title");
            String postarPath = movieObj.getString("poster_path");
            String movieOverview = movieObj.getString("overview");
            String voteAvg = movieObj.getString("vote_average");
            String releasedate = movieObj.getString("release_date");
            movieList.add(new MovieDetails(movieId, title, postarPath, movieOverview, voteAvg, releasedate));
        }

        return movieList;
    }

    public static ArrayList<String> getSimpleTrailerFromJson(Context context, String trailerJsonStr)
        throws JSONException {

        ArrayList<String> temp = new ArrayList<>();
        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerKeyJsonArray = trailerJson.getJSONArray("results");
        for(int i = 0; i < trailerKeyJsonArray.length(); i++){
            JSONObject trailerObject = trailerKeyJsonArray.getJSONObject(i);
            String trailerType = trailerObject.getString("type");
            if(trailerType.toLowerCase().equals("trailer")){
                String trailerID = trailerObject.getString("key");
                temp.add(trailerID);
            }
        }

        return temp;
    }

    public static ReviewDetails getSimpleReviewsFromJson(Context context, String reviewJsonStr)
        throws JSONException {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewJsonArray = reviewJson.getJSONArray("results");
        for(int i = 0; i < reviewJsonArray.length(); i++){
            JSONObject reviewObject = reviewJsonArray.getJSONObject(i);
            names.add(reviewObject.getString("author"));
            comments.add(reviewObject.getString("content"));
        }
        return new ReviewDetails(names, comments);
    }
}
