package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Diego on 10/27/2016.
 */
@Entity
public class JoinMoviesWithActors {
    @Id
    private Long id;
    private Long movieId;
    private Long actorId;
    @Generated(hash = 937842135)
    public JoinMoviesWithActors(Long id, Long movieId, Long actorId) {
        this.id = id;
        this.movieId = movieId;
        this.actorId = actorId;
    }
    @Generated(hash = 2133223819)
    public JoinMoviesWithActors() {
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
    public Long getActorId() {
        return this.actorId;
    }
    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }
}
