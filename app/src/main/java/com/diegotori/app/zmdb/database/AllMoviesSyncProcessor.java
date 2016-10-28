package com.diegotori.app.zmdb.database;

import android.annotation.SuppressLint;

import com.diegotori.app.zmdb.database.entities.Actors;
import com.diegotori.app.zmdb.database.entities.ActorsDao;
import com.diegotori.app.zmdb.database.entities.DaoSession;
import com.diegotori.app.zmdb.database.entities.GenresDao;
import com.diegotori.app.zmdb.database.entities.MovieActorsDao;
import com.diegotori.app.zmdb.database.entities.MovieGenresDao;
import com.diegotori.app.zmdb.database.entities.Movies;
import com.diegotori.app.zmdb.database.entities.MoviesDao;
import com.diegotori.app.zmdb.mvp.model.MovieItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Diego on 10/28/2016.
 */

public class AllMoviesSyncProcessor {
    private DaoSession daoSession;
    private MoviesDao moviesDao;
    private ActorsDao actorsDao;
    private GenresDao genresDao;
    private MovieActorsDao movieActorsDao;
    private MovieGenresDao movieGenresDao;
    private final HashMap<Long, MovieItem> movieIdsToMovieItems;
    private final List<Movies> localMoviesToUpdate;
    private final List<Movies> localMoviesToDelete;
    private final List<Movies> newMoviesToAdd;

    @SuppressLint("UseSparseArrays")
    public AllMoviesSyncProcessor(DaoSession daoSession){
        this.daoSession = daoSession;
        this.moviesDao = daoSession.getMoviesDao();
        this.actorsDao = daoSession.getActorsDao();
        this.genresDao = daoSession.getGenresDao();
        this.movieActorsDao = daoSession.getMovieActorsDao();
        this.movieGenresDao = daoSession.getMovieGenresDao();
        this.movieIdsToMovieItems = new HashMap<>();
        this.localMoviesToUpdate = new ArrayList<>();
        this.localMoviesToDelete = new ArrayList<>();
        this.newMoviesToAdd = new ArrayList<>();
    }

    public void processIncomingMovies(final List<MovieItem> movieItems) {
        Timber.d("---> Start processIncomingMovies();");
        if(movieItems != null && movieItems.size() > 0 ) {
            mapIncomingArticleData(movieItems);
            mergeIncomingMovieData();
            addNewMovieEntries();
            commitChangesToDb();
        }
        Timber.d("---> End processIncomingMovies();");
    }

    private void mapIncomingArticleData(final List<MovieItem> movieItems){
        if(movieItems != null && movieItems.size() > 0){
            Timber.d("Mapping incoming MovieItem data by Movie ID....");
            //clear any existing merge data (thus allowing one to reuse this object more than once).
            if(movieIdsToMovieItems.size() > 0){
                Timber.d("Cleared out previous MovieItem map entries.");
                movieIdsToMovieItems.clear();
            }

            for(MovieItem movieItem : movieItems){
                movieIdsToMovieItems.put((long) movieItem.getId(), movieItem);
            }
            Timber.d("Incoming MovieItem data mapping complete.");
        }
    }

    private void mergeIncomingMovieData() {
        final List<Movies> localMovieEntries = moviesDao.loadAll();
        if(localMovieEntries != null && localMovieEntries.size() > 0) {
            //We have existing local entries.
            Timber.i("Found %d local movies. Computing merge solution...", localMovieEntries.size());
            //find stale movie data;
            long movieId;
            int rank;
            String name;
            String description;
            String director;
            String duration;
            for(Movies localMovieEntry : localMovieEntries) {
                movieId = localMovieEntry.getId();
                rank = localMovieEntry.getRank();
                name = localMovieEntry.getName();
                description = localMovieEntry.getDescription();
                director = localMovieEntry.getDirector();
                duration = localMovieEntry.getDuration();
                final MovieItem match = movieIdsToMovieItems.get(movieId);
                if (match != null) {
                    //Movie exists, remove from the map
                    movieIdsToMovieItems.remove(movieId);
                    if(isStringDirty(match.getName(), name) ||
                            (match.getRank() != rank) ||
                            isStringDirty(match.getDescription(), description) ||
                            isStringDirty(match.getDirector(), director) ||
                            isStringDirty(match.getDuration(), duration)){
                        //Current local data is dirty, time to clean it up.
                        localMovieEntry.setName(match.getName());
                        localMovieEntry.setRank(match.getRank());
                        localMovieEntry.setDescription(match.getDescription());
                        localMovieEntry.setDirector(match.getDirector());
                        localMovieEntry.setDuration(match.getDuration());
                        localMoviesToUpdate.add(localMovieEntry);
                    } else {
                        Timber.i("No update required for existing Movie ID %d.", movieId);
                    }
                    //TODO: Do likewise for existing actors and genres
                } else {
                    //Movie doesn't exist, remove from DB.
                    localMoviesToDelete.add(localMovieEntry);
                }
            }
        } else {
            //We don't have any local article data, thus new entries.
            Timber.d("No local movie data exists.");
        }
    }

    private void addNewMovieEntries() {
        //Add new articles
        for (MovieItem movieItem : movieIdsToMovieItems.values()) {
            final Movies newEntry = new Movies();
            newEntry.setId(movieItem.getId());
            newEntry.setRank(movieItem.getRank());
            newEntry.setDescription(movieItem.getDescription());
            newEntry.setDirector(movieItem.getDirector());
            newEntry.setDuration(movieItem.getDuration());
            final List<String> actorsToAdd = movieItem.getActors();
            if(actorsToAdd != null && actorsToAdd.size() > 0) {
                //TODO: finish me.
            }
            newMoviesToAdd.add(newEntry);
        }
    }

    private void commitChangesToDb() {
        if(localMoviesToUpdate.size() > 0) {
            moviesDao.updateInTx(localMoviesToUpdate);
            localMoviesToUpdate.clear();
        }
        if(localMoviesToDelete.size() > 0) {
            moviesDao.deleteInTx(localMoviesToDelete);
            localMoviesToDelete.clear();
        }
        if(newMoviesToAdd.size() > 0) {
            moviesDao.insertInTx(newMoviesToAdd);
            newMoviesToAdd.clear();
        }
    }

    private boolean isStringDirty(final String incoming, final String existing){
        return (incoming != null && !(incoming.equals(existing)));
    }
}
