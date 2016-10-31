package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Diego on 10/27/2016.
 */
@Entity
public class JoinMoviesWithGenres {
    @Id
    private Long id;
    private Long movieId;
    private Long genreId;
    @Generated(hash = 318792057)
    public JoinMoviesWithGenres(Long id, Long movieId, Long genreId) {
        this.id = id;
        this.movieId = movieId;
        this.genreId = genreId;
    }
    @Generated(hash = 1744236931)
    public JoinMoviesWithGenres() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMovieId() {
        return this.movieId;
    }
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    public Long getGenreId() {
        return this.genreId;
    }
    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}
