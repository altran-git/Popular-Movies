package com.a2g.nd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String MOVIEOBJ_ARG = "MVOBJARG";
    private static final String MOVIEOBJ_PARCEL = "MVOBJPARCEL";
    private Movie movieObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Detail onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Intent intent = getIntent();

            //If there is a Parcelable, it was passed from MainActivity
            if (intent != null && intent.hasExtra(MOVIEOBJ_PARCEL)) {
                movieObject = intent.getParcelableExtra(MOVIEOBJ_PARCEL);
            }

            //Add movieObject as argument to fragment
            Bundle args = new Bundle();
            args.putParcelable(MOVIEOBJ_ARG, movieObject);

            //Create new DetailActivityFragment and set the arguments
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            //Add the container with 'fragment'
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
