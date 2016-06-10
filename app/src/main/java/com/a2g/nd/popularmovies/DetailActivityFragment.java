package com.a2g.nd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a2g.nd.popularmovies.models.ReviewModel;
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

    private Movie movieObject;
    private List<String> trailerList;
    private List<String> reviewList;

    private RecyclerView recyclerView;
    private MovieDetailAdapter movieDetailAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (intent != null && intent.hasExtra("movie_object")) {
            movieObject = intent.getParcelableExtra("movie_object");

//            trailerList = new ArrayList<String>();
//            trailerList.add(0, "testvideo");
//            reviewList = new ArrayList<String>();
//            reviewList.add(0, "testreview");
            movieDetailAdapter = new MovieDetailAdapter(getActivity(), movieObject, trailerList, reviewList);
            recyclerView.setAdapter(movieDetailAdapter);

            getVideoData(Integer.toString(movieObject.id));
            getReviewData(Integer.toString(movieObject.id));

            //movieDetailAdapter.setTrailerCount(1);

        }

        return rootView;
    }

    public void getVideoData(String movieId){
        Log.d(LOG_TAG, "JSON getVideoData");

        RestInterface service = RestInterface.retrofit.create(RestInterface.class);

        Call<VideoModel> call = service.getMovieTrailers(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                try {
                    int trailerCount = response.body().getVideoResults().size();

                    movieDetailAdapter.setTrailerCount(trailerCount);

                    trailerList = new ArrayList<String>();
                    for(int i=0; i<trailerCount; i++){
                        trailerList.add(i, response.body().getVideoResults().get(i).getKey());
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

    public void getReviewData(String movieId){
        Log.d(LOG_TAG, "JSON getReviewData");

        RestInterface service = RestInterface.retrofit.create(RestInterface.class);

        Call<ReviewModel> call = service.getMovieReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        call.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                try {
                    int reviewCount = response.body().getReviewResults().size();
                    reviewList = new ArrayList<String>();
                    for(int i=0; i<reviewCount; i++){
                        reviewList.add(i, response.body().getReviewResults().get(i).getContent());
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
