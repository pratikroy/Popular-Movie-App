package com.example.lenovo.moviereview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.example.lenovo.moviereview.data.MovieContract;

/**
 * Created by LENOVO on 6/25/2018.
 */

public final class DecisionMaker {

    public static boolean searchDataBase(Context context, String movieID){

        Cursor cursor;

        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] selectionArgs = {movieID};

        cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        if(cursor.getCount() != 0)
            return true;
        else
            return false;
    }

    public static Uri insertIntoDB(Context context, String movieId, String movieName, String userRating,
                                   String releaseYear, String plotSynopsis, byte[] movieImage){

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, movieName);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, userRating);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_YEAR, releaseYear);
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, plotSynopsis);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movieImage);

        return context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }

    public static int deleteFromDB(Context context, String movieId){

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId).build();
        return context.getContentResolver().delete(uri, null, null);
    }
}
