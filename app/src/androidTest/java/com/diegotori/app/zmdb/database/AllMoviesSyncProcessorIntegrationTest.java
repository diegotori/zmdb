package com.diegotori.app.zmdb.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.diegotori.app.zmdb.AndroidTestUtils;
import com.diegotori.app.zmdb.database.entities.Actors;
import com.diegotori.app.zmdb.database.entities.DaoMaster;
import com.diegotori.app.zmdb.database.entities.DaoSession;
import com.diegotori.app.zmdb.database.entities.Genres;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithActors;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithGenres;
import com.diegotori.app.zmdb.database.entities.Movies;
import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.greenrobot.greendao.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Diego on 10/31/2016.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class AllMoviesSyncProcessorIntegrationTest {
    private Gson localGson;
    private DaoSession daoSession;
    private AllMoviesSyncProcessor processor;
    private Context context;
    private Context targetContext;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();
        localGson = new GsonBuilder().create();
        final DaoMaster.DevOpenHelper helper =
                new DaoMaster.DevOpenHelper(targetContext, "movies-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        processor = new AllMoviesSyncProcessor(daoSession);
    }

    @After
    public void tearDown() throws Exception {
        Timber.uprootAll();
        processor = null;
        daoSession.getActorsDao().deleteAll();
        daoSession.getMoviesDao().deleteAll();
        daoSession.getGenresDao().deleteAll();
        daoSession.getJoinMoviesWithActorsDao().deleteAll();
        daoSession.getJoinMoviesWithGenresDao().deleteAll();
        daoSession = null;
        localGson = null;
        context = null;
        targetContext = null;
    }

    private List<MovieItem> loadMockResponseFromAssets(final String path) {
        final Pair<Integer, InputStream> assetStream = AndroidTestUtils.loadAssetStream(context, path);
        List<MovieItem> result = null;
        final TypeToken<List<MovieItem>> typeToken = new TypeToken<List<MovieItem>>() {};
        if(assetStream != null) {
            final InputStream is = assetStream.second;
            try {
                final JsonReader jsonReader = localGson.newJsonReader(new InputStreamReader(is));
                result = localGson.fromJson(jsonReader, typeToken.getType());
            } catch (Exception ignored){
            } finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (Exception ignored) {}
                }
            }
        }
        return result;
    }

    @Test
    public void processIncomingMovies__NullData() {
        processor.processIncomingMovies(null);

        final List<Movies> actualMovieEntries = daoSession.getMoviesDao().loadAll();
        assertThat(actualMovieEntries)
                .isNullOrEmpty();
        final List<Actors> actualActorEntries = daoSession.getActorsDao().loadAll();
        assertThat(actualActorEntries)
                .isNullOrEmpty();
        final List<Genres> actualGenreEntries = daoSession.getGenresDao().loadAll();
        assertThat(actualGenreEntries)
                .isNullOrEmpty();
        final List<JoinMoviesWithActors> actualMovieActorEntries =
                daoSession.getJoinMoviesWithActorsDao().loadAll();
        assertThat(actualMovieActorEntries)
                .isNullOrEmpty();
        final List<JoinMoviesWithGenres> actualMovieGenreEntries =
                daoSession.getJoinMoviesWithGenresDao().loadAll();
        assertThat(actualMovieGenreEntries)
                .isNullOrEmpty();
    }

    @Test
    public void processIncomingMovies__NewMovieInsert() {
        final List<MovieItem> mockMovieItems = loadMockResponseFromAssets("all_movies_mock_response.json");

        processor.processIncomingMovies(mockMovieItems);

        final List<Movies> actualMovieEntries = daoSession.getMoviesDao().loadAll();
        assertThat(actualMovieEntries).isNotNull().isNotEmpty();
        assertThat(actualMovieEntries.size())
                .isEqualTo(2);
    }

    @Test
    public void processIncomingMovies__EmptyData() {
        processor.processIncomingMovies(new ArrayList<MovieItem>());

        final List<Movies> actualMovieEntries = daoSession.getMoviesDao().loadAll();
        assertThat(actualMovieEntries)
                .isNullOrEmpty();
        final List<Actors> actualActorEntries = daoSession.getActorsDao().loadAll();
        assertThat(actualActorEntries)
                .isNullOrEmpty();
        final List<Genres> actualGenreEntries = daoSession.getGenresDao().loadAll();
        assertThat(actualGenreEntries)
                .isNullOrEmpty();
        final List<JoinMoviesWithActors> actualMovieActorEntries =
                daoSession.getJoinMoviesWithActorsDao().loadAll();
        assertThat(actualMovieActorEntries)
                .isNullOrEmpty();
        final List<JoinMoviesWithGenres> actualMovieGenreEntries =
                daoSession.getJoinMoviesWithGenresDao().loadAll();
        assertThat(actualMovieGenreEntries)
                .isNullOrEmpty();
    }
}
