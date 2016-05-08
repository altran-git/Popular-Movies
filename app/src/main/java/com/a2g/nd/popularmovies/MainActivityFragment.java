package com.a2g.nd.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A fragment containing a grid view for movies
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieArrayList;


    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState==null || !savedInstanceState.containsKey("movielist")) {
            movieArrayList = new ArrayList<Movie>();
            new FetchMoviesTask().execute("popular","1");
        }
        else {
            movieArrayList = savedInstanceState.getParcelableArrayList("movielist");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movielist", movieArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(movieAdapter == null) {
            movieAdapter = new MovieAdapter(getActivity(), movieArrayList);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Get reference to Gridview and attach adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieObject = movieAdapter.getItem(position);
                Intent detailActivityIntent = new Intent(getContext(), DetailActivity.class)
                        .putExtra("movie_object", movieObject);
                startActivity(detailActivityIntent);
            }
        });

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie [] getMovieData(String moviesJsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_ARRAY = "results";
            final String POSTER_PATH = "poster_path";
            final String ORIG_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String VOTE_AVG = "vote_average";
            final String REL_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(MOVIE_ARRAY);

            //Create movie object
            Movie[] resultMovies = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length() ; i++) {
                // Get the JSON movie object
                JSONObject movieObject = movieArray.getJSONObject(i);
                String  movieImage = movieObject.getString(POSTER_PATH);
                String  movieTitle = movieObject.getString(ORIG_TITLE);
                String  moviePlot = movieObject.getString(OVERVIEW);
                String  movieRating = movieObject.getString(VOTE_AVG);
                String  movieRelDate = movieObject.getString(REL_DATE);

                //Save the movieImage into Movie object
                resultMovies[i] = new Movie(movieImage, movieTitle, moviePlot, movieRating, movieRelDate);
            }
            return resultMovies;
        }

        @Override
        protected Movie[] doInBackground(String... params){
            // Verify size of params
            if(params.length == 0){
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try{
                // Construct the URL for MovieDB query
                // Possible parameters are available at themoviedb API page, at
                // http://docs.themoviedb.apiary.io/
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "?";

                final String API_ID_PARAM = "api_key";
                final String PAGE_PARAM = "page";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_ID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .appendQueryParameter(PAGE_PARAM, params[1])
                        .build();

                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to MovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                moviesJsonStr = buffer.toString();

                //Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                return getMovieData(moviesJsonStr);
            } catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            //will only return null if there was an error getting or parsing the moviedb.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            //add data from server
            if (result != null) {
                movieAdapter.clear();

                for(Movie movieObject : result){
                    movieArrayList.add(movieObject);
                }
            }
        }

    }
}
