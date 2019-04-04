package com.example.lenovo.moviereview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LENOVO on 5/19/2018.
 */

public class MovieDetails implements Parcelable{

    final static private String baseImageUrl = "http://image.tmdb.org/t/p/w185/";

    private String movieId;
    private String originalTitle;
    private String imageThumbnail;
    private String plotSynopsis;
    private String userRating;
    private String releaseDate;

    public MovieDetails(String id, String movieTitle, String imageURL, String movieSynopsis,
                        String movieRating, String date){
        movieId = id;
        originalTitle = movieTitle;
        imageThumbnail = baseImageUrl + imageURL;
        plotSynopsis = movieSynopsis;
        userRating = movieRating;
        releaseDate = date;
    }

    public String getMovieId() { return movieId; }

    public String getOriginalTitle(){
        return originalTitle;
    }

    public String getImageThumbnail(){ return imageThumbnail; }

    public String getPlotSynopsis(){
        return plotSynopsis;
    }

    public String getUserRating(){
        return userRating;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    private MovieDetails(Parcel in){
        movieId = in.readString();
        originalTitle = in.readString();
        imageThumbnail = in.readString();
        plotSynopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(movieId);
        dest.writeString(originalTitle);
        dest.writeString(imageThumbnail);
        dest.writeString(plotSynopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR =
            new Parcelable.Creator<MovieDetails>() {
                public MovieDetails createFromParcel(Parcel in){
                    return new MovieDetails(in);
                }

                public MovieDetails[] newArray(int size){
                    return new MovieDetails[size];
                }
            };
}
