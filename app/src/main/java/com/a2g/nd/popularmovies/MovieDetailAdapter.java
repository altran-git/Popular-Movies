package com.a2g.nd.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();

    private final int VIEW_TYPE_DETAILS = 0;
    private final int VIEW_TYPE_TRAILERS = 1;
    private final int VIEW_TYPE_REVIEWS = 2;

    private Movie movieObject;
    private List<String> trailerList;
    private List<String> reviewList;
    private List<String> reviewerList;
    Context context;

    public MovieDetailAdapter(Context context, Movie movieObject, List<String> trailerList, List<String> reviewList, List<String> reviewerList) {
        this.context = context;
        this.movieObject = movieObject;
        this.trailerList = trailerList;
        this.reviewList = reviewList;
        this.reviewerList = reviewerList;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        public final ImageView imageView;
        public final TextView titleView;
        public final TextView plotView;
        public final TextView ratingView;
        public final TextView dateView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.iv_detail_image);
            titleView = (TextView) itemView.findViewById(R.id.tv_detail_origTitle);
            plotView = (TextView) itemView.findViewById(R.id.tv_detail_plot);
            ratingView = (TextView) itemView.findViewById(R.id.tv_detail_rating);
            dateView = (TextView) itemView.findViewById(R.id.tv_detail_relDate);
        }

        public void bind() {
            //Setup the string path for the image
            String imagePath = "http://image.tmdb.org/t/p/" + "w342" + movieObject.imagePath;

            //Use Picasso libary to load image into imageView (http://square.github.io/picasso)
            Picasso.with(context).load(imagePath).into(imageView);

            titleView.setText(movieObject.origTitle);
            plotView.setText(movieObject.overview);
            ratingView.setText("Rating: " + movieObject.voteAvg);
            dateView.setText("Released: " + movieObject.releaseDate);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView titleView;
        public final ImageView iconView;
        public final TextView textView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.tv_section_title_trailer);
            iconView = (ImageView) itemView.findViewById(R.id.iv_detail_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_detail_trailer);
            itemView.setOnClickListener(this);
        }

        public void bind(int position){
            iconView.setImageResource(R.drawable.ic_play_circle);
            textView.setText("Trailer " + position);
            titleView.setText("Trailers:");
            titleView.setVisibility(position == 1 && trailerList.size() != 0 ? View.VISIBLE : View.GONE);
        }

        //Onclick event for Trailer items will launch Youtube
        @Override
        public void onClick(View v) {
            launchYoutube(trailerList.get(getAdapterPosition()-1));
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        public final TextView titleView;
        public final TextView textView;
        public final TextView reviewerView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.tv_section_title_review);
            reviewerView = (TextView) itemView.findViewById(R.id.tv_detail_reviewer);
            textView = (TextView) itemView.findViewById(R.id.tv_detail_review);
        }

        public void bind(int position) {
            reviewerView.setText(reviewerList.get(position-trailerList.size()-1));
            textView.setText(reviewList.get(position-trailerList.size()-1));
            titleView.setText("Reviews:");
            titleView.setVisibility(position == 1 + trailerList.size() && reviewList.size() != 0 ? View.VISIBLE : View.GONE);
        }
    }

    //Launches Youtube App using Implicit intent, fallback to browser if app is not installed
    public void launchYoutube(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_DETAILS;
        }
        else if(position != 0 && position <= trailerList.size()) {
            return VIEW_TYPE_TRAILERS;
        }
        else {
            return VIEW_TYPE_REVIEWS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem;

        switch (viewType) {
            case VIEW_TYPE_DETAILS:
                viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_movie_item, parent, false);
                return new MovieViewHolder(viewItem);
            case VIEW_TYPE_TRAILERS:
                viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_video_item, parent, false);
                return new VideoViewHolder(viewItem);
            case VIEW_TYPE_REVIEWS:
                viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_review_item, parent, false);
                return new ReviewViewHolder(viewItem);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_DETAILS:
                MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                movieViewHolder.bind();
                break;
            case VIEW_TYPE_TRAILERS:
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
                videoViewHolder.bind(position);

                break;
            case VIEW_TYPE_REVIEWS:
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
                reviewViewHolder.bind(position);
        }

    }

    @Override
    public int getItemCount() {
        return (1 + trailerList.size() + reviewList.size());
    }
}
