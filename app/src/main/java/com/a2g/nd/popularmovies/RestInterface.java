package com.a2g.nd.popularmovies;

import com.a2g.nd.popularmovies.models.Model;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestInterface {
    @GET("movie/{sort}")
    Call<Model> getPopularMovies(
            @Path("sort") String sort,
            @Query("api_key") String apiKey);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.themoviedb.org/3/")
            .client(MainActivity.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
