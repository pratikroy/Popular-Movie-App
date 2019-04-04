package com.example.lenovo.moviereview.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import static com.example.lenovo.moviereview.data.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by LENOVO on 6/24/2018.
 */

public class MovieContentProvider extends ContentProvider {

    //Define integer constants for unique identification of Uris
    public static final int QUERY_AND_INSERT = 100;
    public static final int DELETE = 101;

    private static final UriMatcher sUriMatcher = builtUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    public static UriMatcher builtUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE, QUERY_AND_INSERT);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE + "/*", DELETE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case QUERY_AND_INSERT: {
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri = " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)){

            case QUERY_AND_INSERT: {
                long id = db.insert(TABLE_NAME, null, values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else{
                    throw new android.database.SQLException("Failed to insert new row" + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int movieDeleted;

        switch (sUriMatcher.match(uri)){
            case DELETE: {
                String id = uri.getPathSegments().get(1);
                String selectionParameter = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
                movieDeleted = db.delete(TABLE_NAME, selectionParameter, new String[]{id});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);
        }

        if(movieDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
