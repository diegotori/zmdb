package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Diego on 10/27/2016.
 */
@Entity
public class MovieGenres {
    @Id
    private long id;
    private long movieId;
    private long genreId;
    @Generated(hash = 1426358006)
    public MovieGenres(long id, long movieId, long genreId) {
        this.id = id;
        this.movieId = movieId;
        this.genreId = genreId;
    }
    @Generated(hash = 1474546429)
    public MovieGenres() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getMovieId() {
        return this.movieId;
    }
    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
    public long getGenreId() {
        return this.genreId;
    }
    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }
}
