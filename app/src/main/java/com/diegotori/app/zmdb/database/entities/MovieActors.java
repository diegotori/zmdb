package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Diego on 10/27/2016.
 */
@Entity
public class MovieActors {
    @Id
    private long id;
    private long movieId;
    private long actorId;
    @Generated(hash = 356266024)
    public MovieActors(long id, long movieId, long actorId) {
        this.id = id;
        this.movieId = movieId;
        this.actorId = actorId;
    }
    @Generated(hash = 658641299)
    public MovieActors() {
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
    public long getActorId() {
        return this.actorId;
    }
    public void setActorId(long actorId) {
        this.actorId = actorId;
    }
}
