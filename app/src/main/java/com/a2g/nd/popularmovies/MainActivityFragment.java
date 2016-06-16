package com.a2g.nd.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.a2g.nd.popularmovies.models.MovieModel;

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
    String sortBy;
    SharedPreferences sharedPref;
    Spinner sort_spinner;
    Bundle myBundle;
    boolean userSelect = false;

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

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
        // Inflate the fragment menu
        inflater.inflate(R.menu.menu_mainfragment, menu);

        //Create and initialize the spinner object
        MenuItem item = menu.findItem(R.id.sort_spinner);
        sort_spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(adapter);

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
                    //clear adapter before resorting
                    movieAdapter.clear();

                    //Depending on which item in the spinner is selected, write the value to
                    //the shared preference key
                    SharedPreferences.Editor editor = sharedPref.edit();

                    switch (position) {
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

                    sortBy = sharedPref.getString(getString(R.string.sort_by_key), getString(R.string.sort_by_default));
                    getMovieData(sortBy);

                    userSelect = false;
                }
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
        //Save Spinner State
        outState.putInt("sortspinner", sort_spinner.getSelectedItemPosition());
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
        Log.d(LOG_TAG, "JSON getMovieData");

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
                            for (Movie movieObject : resultMovies) {
                                movieArrayList.add(movieObject);
                            }
                            movieAdapter.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {

            }
        });
    }
}
