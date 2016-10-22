package com.diegotori.app.zmdb;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.model.MovieItem;
import com.diegotori.app.zmdb.model.MovieRankItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Diego on 10/22/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ZmdbApiServiceAcceptanceTest {
    private Retrofit retrofit;
    private ZmdbApiService zmdbApiService;

    @Before
    public void setUp() throws Exception {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.ZOCDOC_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        zmdbApiService = retrofit.create(ZmdbApiService.class);
    }

    @After
    public void tearDown() throws Exception {
        retrofit = null;
        zmdbApiService = null;
    }

    @Test
    public void allMovies() throws Exception {
        final Call<List<MovieItem>> networkCall = zmdbApiService.allMovies();

        final Response<List<MovieItem>> response = networkCall.execute();

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.errorBody()).isNull();
        final List<MovieItem> actualList = response.body();
        assertThat(actualList).isNotNull().isNotEmpty();
    }

    @Test
    public void moviesByRank() throws IOException {
        final Call<List<MovieRankItem>> networkCall = zmdbApiService.moviesByRank(1, 10);

        final Response<List<MovieRankItem>> response = networkCall.execute();

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.errorBody()).isNull();
        final List<MovieRankItem> actualList = response.body();
        assertThat(actualList).isNotNull().isNotEmpty();
    }

    @Test
    public void movieDetails() throws IOException {
        final Call<List<MovieItem>> networkCall = zmdbApiService.movieDetails(332, 290);

        final Response<List<MovieItem>> response = networkCall.execute();

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.errorBody()).isNull();
        final List<MovieItem> actualList = response.body();
        assertThat(actualList).isNotNull().isNotEmpty();
        assertThat(actualList.size()).isEqualTo(2);
    }
}
