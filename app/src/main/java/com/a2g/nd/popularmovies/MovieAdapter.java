package com.a2g.nd.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ND on 5/6/2016.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Gets the Movie object from the adapter at position
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.img_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.img_item_poster_imageview);

        //Setup the string path for the image
        String imagePath = "http://image.tmdb.org/t/p/" + "w185" + movie.imagePath;

        //Use Picasso libary to load image into imageView (http://square.github.io/picasso)
        Picasso.with(getContext()).load(imagePath).into(imageView);

        return convertView;
    }


}

