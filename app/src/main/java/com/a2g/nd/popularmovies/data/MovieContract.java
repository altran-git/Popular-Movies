package com.a2g.nd.popularmovies.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {

    // Suppress default constructor for noninstantiability
    private MovieContract(){
        throw new AssertionError();
    }

    // The "Content authority" is a name for the entire content provider, it is a unique name
    public static final String CONTENT_AUTHORITY = "com.a2g.nd.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";

    /* Inner class that defines the contents of the weather table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_REL_DATE = "relDate";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
