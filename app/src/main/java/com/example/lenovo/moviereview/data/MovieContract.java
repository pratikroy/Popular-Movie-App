package com.example.lenovo.moviereview.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by LENOVO on 6/24/2018.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.lenovo.moviereview";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIE = "favourite";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "favourite";

        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_MOVIE_NAME = "moviename";
        public static final String COLUMN_RATING = "userrating";
        public static final String COLUMN_RELEASE_YEAR = "releaseyear";
        public static final String COLUMN_SYNOPSIS = "plotsynopsis";
        public static final String COLUMN_POSTER = "movieposter";
    }
}
