package com.diegotori.app.zmdb.utils;

import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.diegotori.app.zmdb.mvp.model.MovieRankItem;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Diego on 10/23/2016.
 */

public class ZmdbCache {
    private final Cache<String, Object> runtimeCache;
    private static final String TOP_TEN_MOVIES = "top_ten_movies";

    private ZmdbCache() {
        this.runtimeCache = Cache2kBuilder.of(String.class, Object.class)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .entryCapacity(20)
                .build();
    }

    public void cacheTopTenMovies(final List<MovieRankItem> itemsToCache){
        runtimeCache.put(TOP_TEN_MOVIES, itemsToCache);
    }

    public List<MovieRankItem> getCachedTopTenMovies() {
        //noinspection unchecked
        return (List<MovieRankItem>) runtimeCache.get(TOP_TEN_MOVIES);
    }

    public void cacheMovieDetails(final int movieId, final MovieItem movieDetails){
        runtimeCache.put(Integer.toString(movieId), movieDetails);
    }

    public MovieItem getCachedMovieDetails(final int movieId) {
        return (MovieItem) runtimeCache.get(Integer.toString(movieId));
    }

    public void clear() {
        runtimeCache.clear();
    }

    public static ZmdbCache getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ZmdbCache INSTANCE = new ZmdbCache();
    }
}
