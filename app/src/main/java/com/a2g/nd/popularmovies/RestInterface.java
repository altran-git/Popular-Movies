package com.a2g.nd.popularmovies;

import com.a2g.nd.popularmovies.models.MovieModel;
import com.a2g.nd.popularmovies.models.ReviewModel;
import com.a2g.nd.popularmovies.models.VideoModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestInterface {
    // Construct the URL for MovieDB query
    //Possible parameters are available at themoviedb API page, at
    // http://docs.themoviedb.apiary.io/

    @GET("movie/{sort}")
    Call<MovieModel> getPopularMovies(
            @Path("sort") String sort,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideoModel> getMovieTrailers(
            @Path("id") String movieId,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewModel> getMovieReviews(
            @Path("id") String movieId,
            @Query("api_key") String apiKey);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.themoviedb.org/3/")
            .client(MainActivity.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
