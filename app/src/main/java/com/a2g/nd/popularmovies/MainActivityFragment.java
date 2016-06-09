package com.a2g.nd.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.a2g.nd.popularmovies.models.Model;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment containing a grid view for movies
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieArrayList;


    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        if (savedInstanceState==null || !savedInstanceState.containsKey("movielist")) {
            movieArrayList = new ArrayList<Movie>();
        }
        else {
            movieArrayList = savedInstanceState.getParcelableArrayList("movielist");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the fragment menu
        inflater.inflate(R.menu.menu_mainfragment, menu);


        //Create and initialize the spinner object
        MenuItem item = menu.findItem(R.id.sort_spinner);
        Spinner sort_spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(adapter);

        //Listener for spinner object
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //clear adapter before resorting
                movieAdapter.clear();

                //Depending on which item in the spinner is selected, write the value to
                //the shared preference key
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                switch (position){
                    case 0:
                        //Log.d("SPINNER", "Sort By Popular");
                        editor.putString(getString(R.string.sort_by_key), "popular");
                        editor.commit();
                        break;
                    case 1:
                        //Log.d("SPINNER", "Sort By Top Rated");
                        editor.putString(getString(R.string.sort_by_key), "top_rated");
                        editor.commit();
                        break;
                    default:
                }

                String sortBy = sharedPref.getString(getString(R.string.sort_by_key), getString(R.string.sort_by_default));
                getMovieData(sortBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save array list
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

    public void getMovieData(String sortBy){
        //Log.d(LOG_TAG, "JSON getMovieData");

        RestInterface service = RestInterface.retrofit.create(RestInterface.class);

        Call<Model> call = service.getPopularMovies(sortBy, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                try {
                    int pageSize = response.body().getResults().size();

                    //Create movie object
                    Movie[] resultMovies = new Movie[pageSize];

                    for (int i = 0; i < pageSize ; i++) {
                        // Get the JSON movie objects
                        String  movieImage = response.body().getResults().get(i).getPoster_path();
                        String  movieTitle = response.body().getResults().get(i).getOriginal_title();
                        String  moviePlot = response.body().getResults().get(i).getOverview();
                        String  movieRating = response.body().getResults().get(i).getVote_average().toString();
                        String  movieRelDate = response.body().getResults().get(i).getRelease_date();

                        //Save the movieImage into Movie object
                        resultMovies[i] = new Movie(movieImage, movieTitle, moviePlot, movieRating, movieRelDate);
                    }

                    //add data from server
                    if (resultMovies != null) {
                        for(Movie movieObject : resultMovies){
                            movieArrayList.add(movieObject);
                        }
                        movieAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

            }
        });
    }

//    // Append more data into the adapter
//    public void customLoadMoreDataFromApi(int offset) {
//        // This method probably sends out a network request and appends new data items to your adapter.
//        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
//        // Deserialize API response and then construct new objects to append to the adapter
//
//        //Read the shared preference key for sorting
//        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//        String sort_by = sharedPref.getString(getString(R.string.sort_by_key), getString(R.string.sort_by_default));
//
//        //Log.d("SCROLL", "Sort By ---" + sort_by);
//        new FetchMoviesTask().execute(sort_by, String.valueOf(offset));
//    }

//    public class FetchMoviesTask extends AsyncTask<String,Void,Movie[]> {
//
//        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
//
//        private Movie [] getMovieData(String moviesJsonStr) throws JSONException {
//            // These are the names of the JSON objects that need to be extracted.
//            final String MOVIE_ARRAY = "results";
//            final String POSTER_PATH = "poster_path";
//            final String ORIG_TITLE = "original_title";
//            final String OVERVIEW = "overview";
//            final String VOTE_AVG = "vote_average";
//            final String REL_DATE = "release_date";
//            final String TOTAL_PAGES = "total_pages";
//
//            JSONObject moviesJson = new JSONObject(moviesJsonStr);
//            JSONArray movieArray = moviesJson.getJSONArray(MOVIE_ARRAY);
//
//            int total_pages = moviesJson.getInt(TOTAL_PAGES);
//
//            //write total pages to a shared preference key
//            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putInt(getString(R.string.total_pages_key), total_pages);
//            editor.commit();
//
//            //Create movie object
//            Movie[] resultMovies = new Movie[movieArray.length()];
//
//            for (int i = 0; i < movieArray.length() ; i++) {
//                // Get the JSON movie objects
//                JSONObject movieObject = movieArray.getJSONObject(i);
//                String  movieImage = movieObject.getString(POSTER_PATH);
//                String  movieTitle = movieObject.getString(ORIG_TITLE);
//                String  moviePlot = movieObject.getString(OVERVIEW);
//                String  movieRating = movieObject.getString(VOTE_AVG);
//                String  movieRelDate = movieObject.getString(REL_DATE);
//
//                //Save the movieImage into Movie object
//                resultMovies[i] = new Movie(movieImage, movieTitle, moviePlot, movieRating, movieRelDate);
//            }
//            return resultMovies;
//        }
//
//        @Override
//        protected Movie[] doInBackground(String... params){
//            // Verify size of params
//            if(params.length == 0){
//                return null;
//            }
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String moviesJsonStr = null;
//
//            try{
//                // Construct the URL for MovieDB query
//                // Possible parameters are available at themoviedb API page, at
//                // http://docs.themoviedb.apiary.io/
//                final String MOVIE_BASE_URL =
//                        "http://api.themoviedb.org/3/movie/" + params[0] + "?";
//
//                final String API_ID_PARAM = "api_key";
//                final String PAGE_PARAM = "page";
//
//                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
//                        .appendQueryParameter(API_ID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
//                        .appendQueryParameter(PAGE_PARAM, params[1])
//                        .build();
//
//                URL url = new URL(builtUri.toString());
//
//                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());
//
//                // Create the request to MovieDB, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//
//                moviesJsonStr = buffer.toString();
//
//                //Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the movie data, there's no point in attempting
//                // to parse it.
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try{
//                return getMovieData(moviesJsonStr);
//            } catch(JSONException e){
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            //will only return null if there was an error getting or parsing the moviedb.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Movie[] result) {
//            //Log.d(LOG_TAG, "onPost");
//
//            //add data from server
//            if (result != null) {
//                for(Movie movieObject : result){
//                    movieArrayList.add(movieObject);
//                }
//                movieAdapter.notifyDataSetChanged();
//            }
//        }
//
//    }
}
