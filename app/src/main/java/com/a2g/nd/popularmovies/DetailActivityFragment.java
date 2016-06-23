package com.a2g.nd.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.a2g.nd.popularmovies.data.MovieContract;
import com.a2g.nd.popularmovies.models.ReviewModel;
import com.a2g.nd.popularmovies.models.SimpleDividerItemDecoration;
import com.a2g.nd.popularmovies.models.VideoModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String MOVIEOBJ_ARG = "MVOBJARG";

    private Movie movieObject;
    private RecyclerViewEmptySupport recyclerView;
    private MovieDetailAdapter movieDetailAdapter;
    private List<String> trailerList = new ArrayList<>();
    private List<String> reviewList = new ArrayList<>();
    private List<String> reviewerList = new ArrayList<>();

    MenuItem fave;
    MenuItem unfave;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Detail Fragment onCreate");
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "Detail Fragment onPerpareOptionsMenu");

        fave = menu.findItem(R.id.action_favorite);
        unfave = menu.findItem(R.id.action_unfavorite);

        if(movieObject != null) {
            //Check if the movie selected is in the DB (this means that it is a favorite)
            Cursor movieCursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID},
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{Integer.toString(movieObject.id)},
                    null);

            //If movie exists in the DB then show the Unfavorite button and hide the Favorite button
            if (movieCursor.moveToFirst()) {
                fave.setVisible(false);
                unfave.setVisible(true);
            } else {
                fave.setVisible(true);
                unfave.setVisible(false);
            }

            movieCursor.close();
        } else {
            fave.setVisible(false);
            unfave.setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the fragment menu
        inflater.inflate(R.menu.menu_detailfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.action_favorite:
                // User chose the "Favorites" item, add movie to database
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieObject.id);
                contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movieObject.imagePath);
                contentValues.put(MovieContract.MovieEntry.COLUMN_REL_DATE, movieObject.releaseDate);
                contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieObject.overview);
                contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieObject.origTitle);
                contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movieObject.voteAvg);
                Uri movieInsertUri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                fave.setVisible(false);
                unfave.setVisible(true);
                Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();

                intent.putExtra("Favorite", true);
                getActivity().setResult(Activity.RESULT_OK, intent);
                return true;
            case R.id.action_unfavorite:
                // User chose the "Unfavorites" item, delete movie from database
                getContext().getContentResolver().delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movieObject.id)}
                );
                fave.setVisible(true);
                unfave.setVisible(false);
                Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();

                intent.putExtra("Favorite", false);
                getActivity().setResult(Activity.RESULT_OK, intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "Detail Fragment onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Detail Fragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Setup the recycler view
        recyclerView = (RecyclerViewEmptySupport) rootView.findViewById(R.id.movies_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(container.findViewById(R.id.recycler_empty));

        Bundle arguments = getArguments();
        if (arguments != null){
            movieObject = arguments.getParcelable(MOVIEOBJ_ARG);

            if(movieObject != null) {
                //Attach adapter
                movieDetailAdapter = new MovieDetailAdapter(getActivity(), movieObject, trailerList, reviewList, reviewerList);
                recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
                recyclerView.setAdapter(movieDetailAdapter);

                //Get Trailer and Reviews
                getVideoData(Integer.toString(movieObject.id));
                getReviewData(Integer.toString(movieObject.id));
            }
        }
        return rootView;
    }

    //Retrofit Async call to retrieve Movie Trailers
    public void getVideoData(String movieId){
        //Log.d(LOG_TAG, "JSON getVideoData");

        RestInterface service = RestInterface.retrofit.create(RestInterface.class);

        Call<VideoModel> call = service.getMovieTrailers(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                try {

                    if(response.isSuccessful()) {
                        int trailerCount = response.body().getVideoResults().size();

                        for (int i = 0; i < trailerCount; i++) {
                            trailerList.add(i, response.body().getVideoResults().get(i).getKey());
                        }

                        if (trailerCount != 0) {
                            movieDetailAdapter.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {

            }
        });
    }

    //Retrofit Async call to retrieve Movie Reviews
    public void getReviewData(String movieId){
        //Log.d(LOG_TAG, "JSON getReviewData");

        RestInterface service = RestInterface.retrofit.create(RestInterface.class);

        Call<ReviewModel> call = service.getMovieReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                try {

                    if(response.isSuccessful()) {
                        int reviewCount = response.body().getReviewResults().size();

                        for (int i = 0; i < reviewCount; i++) {
                            reviewList.add(i, response.body().getReviewResults().get(i).getContent());
                            reviewerList.add(i, response.body().getReviewResults().get(i).getAuthor());
                        }

                        if (reviewCount != 0) {
                            movieDetailAdapter.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {

            }
        });
    }
}
