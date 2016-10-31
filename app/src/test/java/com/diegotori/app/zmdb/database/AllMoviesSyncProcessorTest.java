package com.diegotori.app.zmdb.database;

import android.content.Context;
import android.support.test.filters.SmallTest;

import com.diegotori.app.zmdb.BuildConfig;
import com.diegotori.app.zmdb.database.entities.Actors;
import com.diegotori.app.zmdb.database.entities.DaoMaster;
import com.diegotori.app.zmdb.database.entities.DaoSession;
import com.diegotori.app.zmdb.database.entities.Genres;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithActors;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithActorsDao;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithGenres;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithGenresDao;
import com.diegotori.app.zmdb.database.entities.Movies;
import com.diegotori.app.zmdb.mvp.model.MovieItem;

import org.greenrobot.greendao.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Diego on 10/30/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE)
@SmallTest
public class AllMoviesSyncProcessorTest {
    private DaoSession daoSession;

    private AllMoviesSyncProcessor processor;

    static {
        ShadowLog.stream = System.out;
    }

    @Before
    public void setUp() throws Exception {
        final Context roboContext = RuntimeEnvironment.application.getApplicationContext();
        final DaoMaster.DevOpenHelper helper =
                new DaoMaster.DevOpenHelper(roboContext, "movies-db");
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

    @Test
    public void processIncomingMovies__NewMovieInsert() {
        final MovieItem mockMovieItem = mock(MovieItem.class);
        final ArrayList<String> mockActors = new ArrayList<>();
        mockActors.add("Joe Actor");
        mockActors.add("Jane Actress");
        final ArrayList<String> mockGenres = new ArrayList<>();
        mockGenres.add("Action");
        mockGenres.add("Suspense");
        when(mockMovieItem.getId()).thenReturn(123);
        when(mockMovieItem.getRank())
                .thenReturn(10);
        when(mockMovieItem.getName())
                .thenReturn("Some Movie");
        when(mockMovieItem.getDescription())
                .thenReturn("Some Description");
        when(mockMovieItem.getDirector())
                .thenReturn("Alan Smithee");
        when(mockMovieItem.getDuration())
                .thenReturn("120 min.");
        when(mockMovieItem.getActors())
                .thenReturn(mockActors);
        when(mockMovieItem.getGenres())
                .thenReturn(mockGenres);
        final List<MovieItem> mockMovieItems = new ArrayList<>();
        mockMovieItems.add(mockMovieItem);

        processor.processIncomingMovies(mockMovieItems);

        final List<Movies> actualMovieEntries = daoSession.getMoviesDao().loadAll();
        assertThat(actualMovieEntries).isNotNull().isNotEmpty();
        final Movies capturedMovieEntry = actualMovieEntries.get(0);
        assertThat(capturedMovieEntry).isNotNull();
        assertThat(capturedMovieEntry.getId())
                .isNotZero().isEqualTo(123);
        assertThat(capturedMovieEntry.getRank())
                .isNotZero().isEqualTo(10);
        assertThat(capturedMovieEntry.getName())
                .isNotNull().isNotEmpty().isEqualTo("Some Movie");
        assertThat(capturedMovieEntry.getDescription())
                .isNotNull().isNotEmpty().isEqualTo("Some Description");
        assertThat(capturedMovieEntry.getDirector())
                .isNotNull().isNotEmpty().isEqualTo("Alan Smithee");
        assertThat(capturedMovieEntry.getDuration())
                .isNotNull().isNotEmpty().isEqualTo("120 min.");
    }

    @Test
    public void processIncomingMovies__NewMovie__InsertNewActors() {
        final MovieItem mockMovieItem = mock(MovieItem.class);
        final ArrayList<String> mockActors = new ArrayList<>();
        mockActors.add("Joe Actor");
        mockActors.add("Jane Actress");
        when(mockMovieItem.getId()).thenReturn(123);
        when(mockMovieItem.getRank())
                .thenReturn(10);
        when(mockMovieItem.getName())
                .thenReturn("Some Movie");
        when(mockMovieItem.getDescription())
                .thenReturn("Some Description");
        when(mockMovieItem.getDirector())
                .thenReturn("Alan Smithee");
        when(mockMovieItem.getDuration())
                .thenReturn("120 min.");
        when(mockMovieItem.getActors())
                .thenReturn(mockActors);
        final List<MovieItem> mockMovieItems = new ArrayList<>();
        mockMovieItems.add(mockMovieItem);

        processor.processIncomingMovies(mockMovieItems);

        final List<Actors> actualActorEntries = daoSession.getActorsDao().loadAll();
        assertThat(actualActorEntries).isNotNull().isNotEmpty();
        final Actors firstCapturedActor = actualActorEntries.get(0);
        assertThat(firstCapturedActor)
                .isNotNull();
        assertThat(firstCapturedActor.getName())
                .isNotNull().isNotEmpty().isEqualTo("Joe Actor");
        final Actors secondCapturedActor = actualActorEntries.get(1);
        assertThat(secondCapturedActor)
                .isNotNull();
        assertThat(secondCapturedActor.getName())
                .isNotNull().isNotEmpty().isEqualTo("Jane Actress");
    }

    @Test
    public void processIncomingMovies__NewMovie__InsertJoinedMovieActors() {
        //Given mock MovieItem data
        final MovieItem mockMovieItem = mock(MovieItem.class);
        final ArrayList<String> mockActors = new ArrayList<>();
        mockActors.add("Joe Actor");
        mockActors.add("Jane Actress");
        mockActors.add("John Doe");
        when(mockMovieItem.getId()).thenReturn(123);
        when(mockMovieItem.getRank())
                .thenReturn(10);
        when(mockMovieItem.getName())
                .thenReturn("Some Movie");
        when(mockMovieItem.getDescription())
                .thenReturn("Some Description");
        when(mockMovieItem.getDirector())
                .thenReturn("Alan Smithee");
        when(mockMovieItem.getDuration())
                .thenReturn("120 min.");
        when(mockMovieItem.getActors())
                .thenReturn(mockActors);
        final List<MovieItem> mockMovieItems = new ArrayList<>();
        mockMovieItems.add(mockMovieItem);

        //When processing said mock data
        processor.processIncomingMovies(mockMovieItems);

        //Then verify that it inserted exactly three rows for all three actors.
        final JoinMoviesWithActorsDao movieActorsCount = daoSession.getJoinMoviesWithActorsDao();
        final long rowCount = movieActorsCount.queryBuilder()
                .where(JoinMoviesWithActorsDao.Properties.MovieId.eq(123)).count();
        assertThat(rowCount)
                .isNotZero().isEqualTo(3);
    }

    @Test
    public void processIncomingMovies__NewMovie__InsertNewGenres() {
        final MovieItem mockMovieItem = mock(MovieItem.class);
        final ArrayList<String> mockGenres = new ArrayList<>();
        mockGenres.add("Action");
        mockGenres.add("Suspense");
        when(mockMovieItem.getId()).thenReturn(123);
        when(mockMovieItem.getRank())
                .thenReturn(10);
        when(mockMovieItem.getName())
                .thenReturn("Some Movie");
        when(mockMovieItem.getDescription())
                .thenReturn("Some Description");
        when(mockMovieItem.getDirector())
                .thenReturn("Alan Smithee");
        when(mockMovieItem.getDuration())
                .thenReturn("120 min.");
        when(mockMovieItem.getGenres())
                .thenReturn(mockGenres);
        final List<MovieItem> mockMovieItems = new ArrayList<>();
        mockMovieItems.add(mockMovieItem);

        processor.processIncomingMovies(mockMovieItems);

        final List<Genres> actualGenreEntries = daoSession.getGenresDao().loadAll();
        assertThat(actualGenreEntries).isNotNull().isNotEmpty();
        final Genres firstCapturedGenre = actualGenreEntries.get(0);
        assertThat(firstCapturedGenre)
                .isNotNull();
        assertThat(firstCapturedGenre.getName())
                .isNotNull().isNotEmpty().isEqualTo("Action");
        final Genres secondCapturedGenre = actualGenreEntries.get(1);
        assertThat(secondCapturedGenre)
                .isNotNull();
        assertThat(secondCapturedGenre.getName())
                .isNotNull().isNotEmpty().isEqualTo("Suspense");
    }

    @Test
    public void processIncomingMovies__NewMovie__InsertJoinedMovieGenres() {
        //Given mock MovieItem data
        final MovieItem mockMovieItem = mock(MovieItem.class);
        final ArrayList<String> mockGenres = new ArrayList<>();
        mockGenres.add("Action");
        mockGenres.add("Suspense");
        mockGenres.add("Drama");
        when(mockMovieItem.getId()).thenReturn(123);
        when(mockMovieItem.getRank())
                .thenReturn(10);
        when(mockMovieItem.getName())
                .thenReturn("Some Movie");
        when(mockMovieItem.getDescription())
                .thenReturn("Some Description");
        when(mockMovieItem.getDirector())
                .thenReturn("Alan Smithee");
        when(mockMovieItem.getDuration())
                .thenReturn("120 min.");
        when(mockMovieItem.getGenres())
                .thenReturn(mockGenres);
        final List<MovieItem> mockMovieItems = new ArrayList<>();
        mockMovieItems.add(mockMovieItem);

        //When processing said mock data
        processor.processIncomingMovies(mockMovieItems);

        //Then verify that it created exactly three rows for all three genres
        final JoinMoviesWithGenresDao movieGenresDao = daoSession.getJoinMoviesWithGenresDao();
        final long queryCount = movieGenresDao.queryBuilder()
                .where(JoinMoviesWithGenresDao.Properties.MovieId.eq(123)).count();
        assertThat(queryCount)
                .isNotZero().isEqualTo(3);
    }
}
