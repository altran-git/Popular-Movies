package com.a2g.nd.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ND on 6/9/2016.
 */
public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
