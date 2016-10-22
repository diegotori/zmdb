package com.diegotori.app.zmdb.api_service;

import com.diegotori.app.zmdb.BuildConfig;
import com.diegotori.app.zmdb.model.MovieItem;
import com.diegotori.app.zmdb.model.MovieRankItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Diego on 10/21/2016.
 */

public interface ZmdbApiService {
    @GET("/api/1/FEE/AllMovies?authToken=" + BuildConfig.ZOCDOC_AUTH_TOKEN)
    Call<List<MovieItem>> allMovies();

    @GET("/api/1/FEE/MoviesByRank?authToken=" + BuildConfig.ZOCDOC_AUTH_TOKEN)
    Call<List<MovieRankItem>> moviesByRank(@Query("startRankIndex") int startRankIndex,
                                           @Query("numMovies") int numMovies);

    @GET("/api/1/FEE/MovieDetails?authToken=" + BuildConfig.ZOCDOC_AUTH_TOKEN)
    Call<List<MovieItem>> movieDetails(@Query("movieIds") int... movieIds);
}
