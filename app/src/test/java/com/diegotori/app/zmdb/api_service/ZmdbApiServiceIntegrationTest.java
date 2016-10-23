package com.diegotori.app.zmdb.api_service;

import android.support.test.filters.MediumTest;

import com.diegotori.app.zmdb.TestUtils;
import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.diegotori.app.zmdb.mvp.model.MovieRankItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Diego on 10/22/2016.
 */
@RunWith(BlockJUnit4ClassRunner.class)
@MediumTest
public class ZmdbApiServiceIntegrationTest {
    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private ClassLoader classLoader;
    private Retrofit retrofit;
    private ZmdbApiService zmdbApiService;

    @Before
    public void setUp() throws Exception {
        classLoader = getClass().getClassLoader();
        retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        zmdbApiService = retrofit.create(ZmdbApiService.class);
    }

    @After
    public void tearDown() throws Exception {
        classLoader = null;
        retrofit = null;
        zmdbApiService = null;
    }

    @Test
    public void allMovies() throws Exception {
        final MockResponse mockResponse =
                TestUtils.createMockResponse(classLoader, "all_movies_mock_response.json");
        mockWebServer.enqueue(mockResponse);
        final Call<List<MovieItem>> networkCall = zmdbApiService.allMovies();

        final Response<List<MovieItem>> response = networkCall.execute();

        assertThat(response).isNotNull();
        final List<MovieItem> actualList = response.body();
        assertThat(actualList).isNotNull().isNotEmpty();
        assertThat(actualList.size()).isEqualTo(2);
    }

    @Test
    public void moviesByRank() throws IOException {
        final MockResponse mockResponse =
                TestUtils.createMockResponse(classLoader, "movies_by_rank_mock_response.json");
        mockWebServer.enqueue(mockResponse);
        final Call<List<MovieRankItem>> networkCall = zmdbApiService.moviesByRank(1, 10);

        final Response<List<MovieRankItem>> response = networkCall.execute();

        assertThat(response).isNotNull();
        final List<MovieRankItem> actualList = response.body();
        assertThat(actualList).isNotNull().isNotEmpty();
        assertThat(actualList.size()).isEqualTo(3);
    }

    @Test
    public void movieDetails() throws IOException {
        final MockResponse mockResponse =
                TestUtils.createMockResponse(classLoader, "movie_details_mock_response.json");
        mockWebServer.enqueue(mockResponse);
        final Call<List<MovieItem>> networkCall = zmdbApiService.movieDetails(332, 290);

        final Response<List<MovieItem>> response = networkCall.execute();

        assertThat(response).isNotNull();
        final List<MovieItem> actualList = response.body();
        assertThat(actualList).isNotNull().isNotEmpty();
        assertThat(actualList.size()).isEqualTo(2);
    }
}
