package com.a2g.nd.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.DetailCallback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String MOVIEOBJ_ARG = "MVOBJARG";
    private static final String MOVIEOBJ_PARCEL = "MVOBJPARCEL";
    public static boolean mTwoPane;

    static OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Main onCreate");

        //Client data for Stethos
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp-land). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
            else {
                //Only replace fragment if it doesn't already exist
                DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                if (df == null){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                            .commit();
                }
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "Main onPause");
        super.onPause();

        //Need to remove the fragment on orientation change (going from landscape to portait on tablets)
        DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (df != null){
            getSupportFragmentManager().beginTransaction()
                    .remove(df)
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Using onActivityResult to pass data from DetailActivityFragment
        //When a user unfavorites a movie and clicks back into the Favorites list
        //it will refresh the Adapter and remove the movie from the array list
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(!data.getBooleanExtra("Favorite", false)){
                int spinnerState = MainActivityFragment.mSpinnerPosition;
                if(spinnerState == 2) {
                    //only refresh adapater and arraylist if Spinner is on Favorites
                    MainActivityFragment.getFavoriteMovieData(this);
                }
            }
        }
    }

    //onItemSelected is used as a Callback (interface DetailCallback) for MainActivityFragment
    @Override
    public void onItemSelected(Movie movieObject){
        if(mTwoPane == true){
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            //Add movieObject as argument to fragment
            Bundle args = new Bundle();
            args.putParcelable(MOVIEOBJ_ARG, movieObject);

            //Create new DetailActivityFragment and set the arguments
            DetailActivityFragment fragment  = new DetailActivityFragment();
            fragment.setArguments(args);

            //Replace the container with 'fragment'
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else{
            //If not in TwoPane mode, then just start the DetailActivity class and pass it a movieObject
            Intent detailActivityIntent = new Intent(this, DetailActivity.class)
                        .putExtra(MOVIEOBJ_PARCEL, movieObject);
                startActivityForResult(detailActivityIntent, 1);
        }
    }
}
