package com.a2g.nd.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.a2g.nd.popularmovies.data.MovieContract;
import com.a2g.nd.popularmovies.models.MovieModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment containing a grid view for movies
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static MovieAdapter movieAdapter;
    private static ArrayList<Movie> movieArrayList;
    private static GridView gridView;
    Spinner sort_spinner;
    ArrayAdapter<CharSequence> spinnerAdapter;
    public static int mSpinnerPosition ;
    Bundle myBundle;

    boolean userSelect = false;


    private static final String[] MOVIE_PROJECTION = new String[] {
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_USER_RATING,
            MovieContract.MovieEntry.COLUMN_REL_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    // these indices must match the projection
    private static final int INDEX_POSTER = 0;
    private static final int INDEX_TITLE = 1;
    private static final int INDEX_SYNOPSIS = 2;
    private static final int INDEX_USER_RATING = 3;
    private static final int INDEX_REL_DATE = 4;
    private static final int INDEX_MOVIE_ID = 5;


    public interface DetailCallback{
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Main Fragment onCreate");
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        if (savedInstanceState==null || !savedInstanceState.containsKey("movielist")) {
            movieArrayList = new ArrayList<Movie>();
            getMovieData(getString(R.string.sort_by_default));
        }
        else {
            this.myBundle = savedInstanceState;
            movieArrayList = savedInstanceState.getParcelableArrayList("movielist");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOG_TAG, "Main Fragment onCreateOptionsMenu");
        // Inflate the fragment menu
        inflater.inflate(R.menu.menu_mainfragment, menu);

        //Create and initialize the spinner object
        MenuItem item = menu.findItem(R.id.sort_spinner);
        sort_spinner = (Spinner) MenuItemCompat.getActionView(item);

        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(spinnerAdapter);
        sort_spinner.setSelection(mSpinnerPosition);

        //Restore spinner state if it was saved
        if(this.myBundle != null){
            sort_spinner.setSelection(myBundle.getInt("sortspinner", 0));
        }

        //Touch Listener for spinner object
        sort_spinner.setOnTouchListener(new AdapterView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelect = true;
                return false;
            }
        });

        //Select Listener for spinner object
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if(userSelect) {
                    switch (position) {
                        case 0:
                            getMovieData("popular");
                            break;
                        case 1:
                            getMovieData("top_rated");
                            break;
                        case 2:
                            getFavoriteMovieData(getActivity());
                            break;
                    }

                    //reset userSelect
                    userSelect = false;

                    //save spinner position
                    mSpinnerPosition = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "Main Fragment onSaveInstanceState");
        super.onSaveInstanceState(outState);
        //Save array list
        outState.putParcelableArrayList("movielist", movieArrayList);
        //Save Spinner State
        outState.putInt("sortspinner", sort_spinner.getSelectedItemPosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Main Fragment onCreateView");

        if(movieAdapter == null) {
            movieAdapter = new MovieAdapter(getActivity(), movieArrayList);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Get reference to Gridview and attach adapter to it
        gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);gridView.setEmptyView(rootView.findViewById(R.id.grid_empty));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieObject = movieAdapter.getItem(position);

                ((DetailCallback) getActivity()).onItemSelected(movieObject);
            }
        });

        return rootView;
    }

    public static void getFavoriteMovieData(Context context){
        //Log.d(LOG_TAG, "getFavoriteMovieData");
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_PROJECTION,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TITLE + " ASC");

        movieArrayList.clear();
        while(cursor.moveToNext()){
            //Set movie object parameters
            String imagePath = cursor.getString(INDEX_POSTER);
            String origTitle = cursor.getString(INDEX_TITLE);
            String overview = cursor.getString(INDEX_SYNOPSIS);
            String voteAvg = cursor.getString(INDEX_USER_RATING);
            String releaseDate = cursor.getString(INDEX_REL_DATE);
            int movieId = cursor.getInt(INDEX_MOVIE_ID);

            Movie movieObject = new Movie(imagePath, origTitle, overview, voteAvg, releaseDate, movieId);
            movieArrayList.add(movieObject);
        }
        movieAdapter.notifyDataSetChanged();
        gridView.smoothScrollToPosition(0);
        cursor.close();
    }

    //Retrofit Async call to retrieve Movie data
    public void getMovieData(String sortBy){
        //Log.d(LOG_TAG, "JSON getMovieData");

        RestInterface service = RestInterface.retrofit.create(RestInterface.class);

        Call<MovieModel> call = service.getPopularMovies(sortBy, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                try {
                    if(response.isSuccessful()) {
                        int pageSize = response.body().getMovieResults().size();

                        //Create movie object
                        Movie[] resultMovies = new Movie[pageSize];

                        for (int i = 0; i < pageSize; i++) {
                            // Get the JSON movie objects
                            String movieImage = response.body().getMovieResults().get(i).getPoster_path();
                            String movieTitle = response.body().getMovieResults().get(i).getOriginal_title();
                            String moviePlot = response.body().getMovieResults().get(i).getOverview();
                            String movieRating = response.body().getMovieResults().get(i).getVote_average().toString();
                            String movieRelDate = response.body().getMovieResults().get(i).getRelease_date();
                            int movieId = response.body().getMovieResults().get(i).getId();

                            //Save the movieImage into Movie object
                            resultMovies[i] = new Movie(movieImage, movieTitle, moviePlot, movieRating, movieRelDate, movieId);
                        }

                        //add data from server
                        if (resultMovies != null) {
                            movieArrayList.clear();
                            for (Movie movieObject : resultMovies) {
                                movieArrayList.add(movieObject);
                            }
                            movieAdapter.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    gridView.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {

            }
        });
    }
}
