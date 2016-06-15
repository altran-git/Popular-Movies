package com.a2g.nd.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.a2g.nd.popularmovies.data.MovieContract;

import java.util.Map;
import java.util.Set;


public class TestProvider extends AndroidTestCase {
    private static final int TEST_MOVIE_ID = 123;
    private static final String TEST_TITLE = "The Sandlot";
    private static final String TEST_UPDATE_TITLE = "The Sandlot 2";
    private static final String TEST_POSTER = "posterpath";
    private static final String TEST_SYNOPSIS = "Summary of movie";
    private static final String TEST_USER_RATING = "10/10";
    private static final String TEST_REL_DATE = "1993";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testDeleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDeleteAllRecords();
    }

    public void testDeleteAllRecords(){
        // Delete movies
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        // Ensure movies were deleted
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testGetType(){
        // content_authority = "content://com.androidessence.moviedatabase/:

        //-- MOVIE --//
        // content_authority + movie
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals(MovieContract.MovieEntry.CONTENT_TYPE, type);

        //-- MOVIE_ID --//
        // content_authority + movie/id
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(0));
        assertEquals(MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertReadMovie(){
        ContentValues movieContentValuesContentValues = getMovieContentValues();
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValuesContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Verify we inserted a row
        assertTrue(movieRowId > 0);

        // Query for all rows and validate cursor
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValuesContentValues);
        movieCursor.close();
        // Query for specific row and validate cursor
        movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValuesContentValues);
        movieCursor.close();
    }

    public void testUpdateMovie(){
        ContentValues movieContentValues = getMovieContentValues();
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Update
        ContentValues updatedMovieContentValues = new ContentValues(movieContentValues);
        updatedMovieContentValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedMovieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, TEST_UPDATE_TITLE);
        mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedMovieContentValues,
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(movieRowId)}
        );

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, updatedMovieContentValues);
        movieCursor.close();
    }

    private ContentValues getMovieContentValues(){
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, TEST_POSTER);
        values.put(MovieContract.MovieEntry.COLUMN_REL_DATE, TEST_REL_DATE);
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, TEST_SYNOPSIS);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, TEST_TITLE);
        values.put(MovieContract.MovieEntry.COLUMN_USER_RATING, TEST_USER_RATING);
        return values;
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues){
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            switch(valueCursor.getType(idx)){
                case Cursor.FIELD_TYPE_FLOAT:
                    assertEquals(entry.getValue(), valueCursor.getDouble(idx));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    assertEquals(Integer.parseInt(entry.getValue().toString()), valueCursor.getInt(idx));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
                default:
                    assertEquals(entry.getValue().toString(), valueCursor.getString(idx));
                    break;
            }
        }
        valueCursor.close();
    }
}
