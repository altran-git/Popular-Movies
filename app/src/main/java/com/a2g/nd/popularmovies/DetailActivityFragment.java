package com.a2g.nd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private Movie movieObject;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (intent != null && intent.hasExtra("movie_object")) {
            movieObject = intent.getParcelableExtra("movie_object");

            //Setup the string path for the image
            String imagePath = "http://image.tmdb.org/t/p/" + "w500" + movieObject.imagePath;

            //Use Picasso libary to load image into imageView (http://square.github.io/picasso)
            Picasso.with(getContext()).load(imagePath).into((ImageView) rootView.findViewById(R.id.iv_detail_image));

            ((TextView) rootView.findViewById(R.id.tv_detail_origTitle))
                    .setText(movieObject.origTitle);
            ((TextView) rootView.findViewById(R.id.tv_detail_plot))
                    .setText(movieObject.overview);
            ((TextView) rootView.findViewById(R.id.tv_detail_rating))
                    .setText("Rating: " + movieObject.voteAvg);
            ((TextView) rootView.findViewById(R.id.tv_detail_relDate))
                    .setText("Released: " + movieObject.releaseDate);
        }

        return rootView;
    }
}
