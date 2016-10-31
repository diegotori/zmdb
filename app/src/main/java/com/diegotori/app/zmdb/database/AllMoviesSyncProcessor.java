package com.diegotori.app.zmdb.database;

import android.annotation.SuppressLint;

import com.diegotori.app.zmdb.database.entities.Actors;
import com.diegotori.app.zmdb.database.entities.ActorsDao;
import com.diegotori.app.zmdb.database.entities.DaoSession;
import com.diegotori.app.zmdb.database.entities.Genres;
import com.diegotori.app.zmdb.database.entities.GenresDao;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithActors;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithActorsDao;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithGenres;
import com.diegotori.app.zmdb.database.entities.JoinMoviesWithGenresDao;
import com.diegotori.app.zmdb.database.entities.Movies;
import com.diegotori.app.zmdb.database.entities.MoviesDao;
import com.diegotori.app.zmdb.mvp.model.MovieItem;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Diego on 10/28/2016.
 */

public class AllMoviesSyncProcessor {
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    private DaoSession daoSession;
    private MoviesDao moviesDao;
    private JoinMoviesWithActorsDao movieActorsDao;
    private JoinMoviesWithGenresDao movieGenresDao;
    private final HashMap<Long, MovieItem> movieIdsToMovieItems;
    private final HashMap<Long, List<String>> movieIdsToActors;
    private final HashMap<Long, List<String>> movieIdsToGenres;
    private List<DeleteQuery<JoinMoviesWithActors>> movieActorsToDelete;
    private List<DeleteQuery<JoinMoviesWithGenres>> movieGenresToDelete;
    private HashMap<String, List<Movies>> moviesDbCrudOps;


    @SuppressLint("UseSparseArrays")
    public AllMoviesSyncProcessor(DaoSession daoSession){
        this.daoSession = daoSession;
        this.moviesDao = daoSession.getMoviesDao();
        this.movieActorsDao = daoSession.getJoinMoviesWithActorsDao();
        this.movieGenresDao = daoSession.getJoinMoviesWithGenresDao();
        this.moviesDbCrudOps = new HashMap<>();
        this.movieActorsToDelete = new ArrayList<>();
        this.movieGenresToDelete = new ArrayList<>();
        this.movieIdsToMovieItems = new HashMap<>();
        this.movieIdsToActors = new HashMap<>();
        this.movieIdsToGenres = new HashMap<>();
    }

    public void processIncomingMovies(final List<MovieItem> movieItems) {
        Timber.d("---> Start processIncomingMovies();");
        if(movieItems != null && movieItems.size() > 0 ) {
            mapIncomingMovieItemData(movieItems);
            mergeIncomingMovieData();
            addNewMovieEntries();
        }
        Timber.d("---> End processIncomingMovies();");
    }

    private void mapIncomingMovieItemData(final List<MovieItem> movieItems){
        if(movieItems != null && movieItems.size() > 0){
            Timber.d("Mapping incoming MovieItem data by Movie ID....");
            for(MovieItem movieItem : movieItems){
                movieIdsToMovieItems.put((long) movieItem.getId(), movieItem);
                movieIdsToActors.put((long) movieItem.getId(), movieItem.getActors());
                movieIdsToGenres.put((long) movieItem.getId(), movieItem.getGenres());
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
                        queueMoviesDbCrudOp(UPDATE, localMovieEntry);
                    } else {
                        Timber.i("No update required for existing Movie ID %d.", movieId);
                    }
                    mergeMovieActorData((int) movieId, localMovieEntry);
                    mergeMovieGenreData((int) movieId, localMovieEntry);
                } else {
                    //Movie doesn't exist, remove from DB, as well as its actors and genres.
                    Timber.i("Deleting stale Movie entry with ID %d.", movieId);
                    localMovieEntry.getActors().clear();
                    localMovieEntry.getGenres().clear();
                    queueMoviesDbCrudOp(DELETE, localMovieEntry);
                    final DeleteQuery<JoinMoviesWithActors> movieActorsDeleteQuery =
                            movieActorsDao.queryBuilder()
                            .where(JoinMoviesWithActorsDao.Properties.MovieId.eq(movieId))
                            .buildDelete();
                    movieActorsToDelete.add(movieActorsDeleteQuery);
                    final DeleteQuery<JoinMoviesWithGenres> movieGenresDeleteQuery =
                            movieGenresDao.queryBuilder()
                                    .where(JoinMoviesWithGenresDao.Properties.MovieId.eq(movieId))
                                    .buildDelete();
                    movieGenresToDelete.add(movieGenresDeleteQuery);
                }
            }
        } else {
            //We don't have any local article data, thus new entries.
            Timber.d("No local movie data exists.");
        }
    }

    private void mergeMovieActorData(final int movieId, Movies existingMovie){
        final List<Actors> existingActors = existingMovie.getActors();
        if(existingActors != null && existingActors.size() > 0){
            Timber.i("Found %d local actor entries for Movie ID %d. Computing merge solution...",
                    existingActors.size(),
                    movieId);
            final List<String> actorsList = movieIdsToActors.get((long) movieId);
            if(actorsList != null && actorsList.size() > 0) {
                //Iterate through existing actors, and prune out existing entries
                for(Actors existingActor : existingActors) {
                    final String existingActorName = existingActor.getName();
                    if(actorsList.contains(existingActorName)) {
                        //Remove so that we don't re-insert it.
                        actorsList.remove(existingActorName);
                    } else {
                        //Remove existing actor ID for this Movie's ID
                        existingActors.remove(existingActor);
                        DeleteQuery<JoinMoviesWithActors> deleteQuery = movieActorsDao.queryBuilder()
                                .where(JoinMoviesWithActorsDao.Properties.ActorId.eq(existingActor.getId()),
                                        JoinMoviesWithActorsDao.Properties.MovieId.eq(movieId)).buildDelete();
                        movieActorsToDelete.add(deleteQuery);
                    }
                }
            } else {
                //Get rid of existing actors for movie ID;
                existingActors.clear();
                DeleteQuery<JoinMoviesWithActors> deleteQuery = movieActorsDao.queryBuilder()
                        .where(JoinMoviesWithActorsDao.Properties.MovieId.eq(movieId))
                        .buildDelete();
                movieActorsToDelete.add(deleteQuery);
            }
        } else {
            Timber.i("No Actors exist for Movie ID %d.", movieId);
        }
    }

    private void mergeMovieGenreData(final int movieId, Movies existingMovie){
        final List<Genres> existingGenres = existingMovie.getGenres();
        if(existingGenres != null && existingGenres.size() > 0){
            Timber.i("Found %d local genres entries for Movie ID %d. Computing merge solution...",
                    existingGenres.size(),
                    movieId);
            final List<String> genresList = movieIdsToGenres.get((long) movieId);
            if(genresList != null && genresList.size() > 0) {
                //Iterate through existing genres, and prune out existing entries
                for(Genres existingGenre : existingGenres) {
                    final String existingGenreName = existingGenre.getName();
                    if(genresList.contains(existingGenreName)) {
                        //Remove so that we don't re-insert it.
                        genresList.remove(existingGenreName);
                    } else {
                        //Remove existing genre ID for this Movie's ID
                        existingGenres.remove(existingGenre);
                        final DeleteQuery<JoinMoviesWithGenres> deleteQuery =
                                movieGenresDao.queryBuilder()
                                .where(JoinMoviesWithGenresDao.Properties.GenreId
                                        .eq(existingGenre.getId()),
                                        JoinMoviesWithGenresDao.Properties.MovieId
                                                .eq(movieId)).buildDelete();
                        movieGenresToDelete.add(deleteQuery);
                    }
                }
            } else {
                //Get rid of existing genres for movie ID
                existingGenres.clear();
                final DeleteQuery<JoinMoviesWithGenres> deleteQuery = movieGenresDao.queryBuilder()
                        .where(JoinMoviesWithGenresDao.Properties.MovieId.eq(movieId))
                        .buildDelete();
                movieGenresToDelete.add(deleteQuery);
            }
        } else {
            Timber.i("No Genres exist for Movie ID %d.", movieId);
        }
    }

    private void addNewMovieEntries() {
        //Delete queued up actors and genres.
        if(movieActorsToDelete.size() > 0) {
            for(DeleteQuery<JoinMoviesWithActors> movieActorToDelete : movieActorsToDelete) {
                //By now, these entries are now decoupled from the cached model
                movieActorToDelete.executeDeleteWithoutDetachingEntities();
            }
            //Finally, clear the list of pending movie actor deletes
            movieActorsToDelete.clear();
        }
        if(movieGenresToDelete.size() > 0) {
            for(DeleteQuery<JoinMoviesWithGenres> movieGenreToDelete : movieGenresToDelete) {
                //By now, these entries are now decoupled from the cached model
                movieGenreToDelete.executeDeleteWithoutDetachingEntities();
            }
            //Finally, clear the list of pending genre actor deletes
            movieGenresToDelete.clear();
        }

        //Update or delete existing movie DB entries
        if(moviesDbCrudOps.size() > 0) {
            for(Map.Entry<String, List<Movies>> entry : moviesDbCrudOps.entrySet() ){
                final String currentKey = entry.getKey();
                final List<Movies> currentValue = entry.getValue();
                if(currentValue != null && currentValue.size() > 0) {
                    if(currentKey.equals(DELETE)){
                        moviesDao.deleteInTx(currentValue);
                    } else if(currentKey.equals(UPDATE)){
                        moviesDao.updateInTx(currentValue);
                    }
                }
            }
            //Finally, clear the list of pending movie DB ops.
            moviesDbCrudOps.clear();
        }

        if(movieIdsToMovieItems.size() > 0){
            //We have new movies to add.
            final ActorsDao actorsDao = daoSession.getActorsDao();
            final GenresDao genresDao = daoSession.getGenresDao();
            final List<Movies> newMovieEntries = new ArrayList<>();
            final List<Actors> newActorEntries = new ArrayList<>();
            final List<JoinMoviesWithActors> newMovieActorEntries = new ArrayList<>();
            final List<Genres> newGenreEntries = new ArrayList<>();
            final List<JoinMoviesWithGenres> newMovieGenreEntries = new ArrayList<>();
            //First populate the new movie entries to add to the DB.
            for (MovieItem movieItem : movieIdsToMovieItems.values()) {
                final Movies newMovieEntry = new Movies();
                newMovieEntry.setId(movieItem.getId());
                newMovieEntry.setRank(movieItem.getRank());
                newMovieEntry.setDescription(movieItem.getDescription());
                newMovieEntry.setDirector(movieItem.getDirector());
                newMovieEntry.setDuration(movieItem.getDuration());
                newMovieEntries.add(newMovieEntry);
            }
            //Insert the new movie entries first.
            moviesDao.insertInTx(newMovieEntries);
            //Then start populating the new actors and genre entries for each new movie added
            for (Movies newMovieEntry : newMovieEntries) {
                final List<String> actorsToAdd = movieIdsToActors.get(newMovieEntry.getId());
                if(actorsToAdd != null && actorsToAdd.size() > 0) {
                    /**
                     * GreenDAO lazily accesses the "to-many" relations of a given entity.
                     * As a result, their data model is NOT updated when related entries are added to
                     * the DB. As a result, we have to manually add new actor entries to our new
                     * Movie DB entries within its entity.
                     */
                    final List<Actors> newMovieEntryActors = newMovieEntry.getActors();
                    for(String actor : actorsToAdd) {
                        final Actors newActorEntry = new Actors();
                        newActorEntry.setName(actor);
                        final JoinMoviesWithActors newMovieActorEntry = new JoinMoviesWithActors();
                        newMovieActorEntry.setMovieId(newMovieEntry.getId());
                        newMovieActorEntry.setActorId(newActorEntry.getId());
                        //Add new actor entry to the new movie entry's list of actors
                        newMovieEntryActors.add(newActorEntry);
                        //Queue up new entries to the DB
                        newActorEntries.add(newActorEntry);
                        newMovieActorEntries.add(newMovieActorEntry);
                    }
                }
                final List<String> genresToAdd = movieIdsToGenres.get(newMovieEntry.getId());
                if(genresToAdd != null && genresToAdd.size() > 0) {
                    /**
                     * GreenDAO lazily accesses the "to-many" relations of a given entity.
                     * As a result, their data model is NOT updated when related entries are added to
                     * the DB. As a result, we have to manually add new genre entries to our new
                     * Movie DB entries within its entity.
                     */
                    final List<Genres> newMovieEntryGenres = newMovieEntry.getGenres();
                    for(String genre : genresToAdd) {
                        final Genres newGenreEntry = new Genres();
                        newGenreEntry.setName(genre);
                        final JoinMoviesWithGenres newMovieGenreEntry = new JoinMoviesWithGenres();
                        newMovieGenreEntry.setMovieId(newMovieEntry.getId());
                        newMovieGenreEntry.setGenreId(newGenreEntry.getId());
                        //Add new genre entry to the new movie entry's list of genres
                        newMovieEntryGenres.add(newGenreEntry);
                        //Queue up new entries to the DB
                        newGenreEntries.add(newGenreEntry);
                        newMovieGenreEntries.add(newMovieGenreEntry);
                    }
                }
            }
            actorsDao.insertInTx(newActorEntries);
            movieActorsDao.insertInTx(newMovieActorEntries);
            genresDao.insertInTx(newGenreEntries);
            movieGenresDao.insertInTx(newMovieGenreEntries);
            //clear any existing merge data (thus allowing one to reuse this object more than once).
            Timber.d("Cleared out temporary MovieItem map entries.");
            movieIdsToMovieItems.clear();
            Timber.d("Cleared out temporary MovieItem Actor map entries.");
            movieIdsToActors.clear();
            Timber.d("Cleared out temporary MovieItem Genre map entries.");
            movieIdsToGenres.clear();
        }
    }

    private boolean isStringDirty(final String incoming, final String existing){
        return (incoming != null && !(incoming.equals(existing)));
    }

    private void queueMoviesDbCrudOp(final String crudType, final Movies movieEntry){
        List<Movies> currentCrudOps = moviesDbCrudOps.get(crudType);
        if(currentCrudOps == null){
            currentCrudOps = new ArrayList<>();
        }
        currentCrudOps.add(movieEntry);
        moviesDbCrudOps.put(crudType, currentCrudOps);
    }
}