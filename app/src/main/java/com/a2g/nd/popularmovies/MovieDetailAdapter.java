package com.a2g.nd.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_DETAILS = 0;
    private final int VIEW_TYPE_TRAILERS = 1;
    private final int VIEW_TYPE_REVIEWS = 2;

    private int trailerCount = 0;

    private Movie movieObject;
    private List<String> trailerList;
    private List<String> reviewList;
    Context context;

    public MovieDetailAdapter(Context context, Movie movieObject, List<String> trailerList, List<String> reviewList) {
        this.context = context;
        this.movieObject = movieObject;
        this.trailerList = trailerList;
        this.reviewList = reviewList;
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
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        public final ImageView iconView;
        public final TextView textView;

        public VideoViewHolder(View itemView) {
            super(itemView);

            iconView = (ImageView) itemView.findViewById(R.id.iv_detail_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_detail_trailer);
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        public final TextView textView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.tv_detail_review);
        }
    }

    public void setTrailerCount(int trailerCount){
        this.trailerCount = trailerCount;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_DETAILS;
        }
        else if(position != 0 && position <= trailerCount) {
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

                //Setup the string path for the image
                String imagePath = "http://image.tmdb.org/t/p/" + "w500" + movieObject.imagePath;

                //Use Picasso libary to load image into imageView (http://square.github.io/picasso)
                Picasso.with(context).load(imagePath).into(movieViewHolder.imageView);

                movieViewHolder.titleView.setText(movieObject.origTitle);
                movieViewHolder.plotView.setText(movieObject.overview);
                movieViewHolder.ratingView.setText("Rating: " + movieObject.voteAvg);
                movieViewHolder.dateView.setText("Released: " + movieObject.releaseDate);
                break;
            case VIEW_TYPE_TRAILERS:
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

                videoViewHolder.iconView.setImageResource(R.drawable.ic_play_circle);
                videoViewHolder.textView.setText(trailerList.get(position-trailerCount));
                break;
            case VIEW_TYPE_REVIEWS:
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;

                reviewViewHolder.textView.setText(reviewList.get(position-trailerCount-1));
        }

    }

    @Override
    public int getItemCount() {
        return (1 + trailerList.size() + reviewList.size());
    }
}
