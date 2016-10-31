package com.diegotori.app.zmdb.mvp.model;

import android.os.AsyncTask;

import com.diegotori.app.zmdb.database.AllMoviesSyncProcessor;
import com.diegotori.app.zmdb.database.entities.DaoSession;
import com.diegotori.app.zmdb.database.entities.Movies;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Simple {@link AsyncTask} implementation that performs our "AllMovies" network call
 * and re-syncs the persisted data with the incoming data all from a background thread.
 *
 * Created by Diego on 10/27/2016.
 */

public class AllMoviesSyncLoader extends AsyncTask<Void, Void, List<Movies>> {
    private final Call<List<MovieItem>> networkCall;
    private final DaoSession daoSession;
    private WeakReference<SyncLoaderListener> weakRefdListener;
    private AllMoviesSyncProcessor processor;

    public AllMoviesSyncLoader(final Call<List<MovieItem>> networkCall,
                               final DaoSession daoSession,
                               final SyncLoaderListener listener){
        this.networkCall = networkCall;
        this.daoSession = daoSession;
        this.weakRefdListener = new WeakReference<>(listener);
        this.processor = new AllMoviesSyncProcessor(daoSession);
    }

    @Override
    protected List<Movies> doInBackground(Void... params) {
        Response<List<MovieItem>> response = null;
        List<Movies> result = null;
        try {
            response = networkCall.execute();
        } catch (Exception ignored) {}
        if(response != null){
            final List<MovieItem> incomingMovies = response.body();
            processor.processIncomingMovies(incomingMovies);
            result = daoSession.loadAll(Movies.class);
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<Movies> movies) {
        super.onPostExecute(movies);
        if (isCancelled()) {
            return;
        }
        final SyncLoaderListener listener = weakRefdListener.get();
        if(listener != null){
            listener.onSuccess(movies);
        }
    }


    public interface SyncLoaderListener {
        public void onSuccess(List<Movies> movies);

        public void onError(Exception e);
    }
}
