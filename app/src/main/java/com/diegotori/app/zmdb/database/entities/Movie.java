package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Diego on 10/27/2016.
 */

@Entity(
        indexes = {
                @Index(value = "id", unique = true)
        }
)
public class Movie {
    @Id
    private long id;
    private int rank;
    private String name;
    private String description;
    private String director;
    private String duration;

    @ToMany
    @JoinEntity(
            entity = MovieActors.class,
            sourceProperty = "movieId",
            targetProperty = "actorId"
    )
    private List<Actors> actors;

    @ToMany
    @JoinEntity(
            entity = MovieGenres.class,
            sourceProperty = "movieId",
            targetProperty = "genreId"
    )
    private List<Genres> genres;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1042217376)
    private transient MovieDao myDao;

    @Generated(hash = 1510043095)
    public Movie(long id, int rank, String name, String description,
            String director, String duration) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.description = description;
        this.director = director;
        this.duration = duration;
    }

    @Generated(hash = 1263461133)
    public Movie() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return this.director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1610048790)
    public List<Actors> getActors() {
        if (actors == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ActorsDao targetDao = daoSession.getActorsDao();
            List<Actors> actorsNew = targetDao._queryMovie_Actors(id);
            synchronized (this) {
                if (actors == null) {
                    actors = actorsNew;
                }
            }
        }
        return actors;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 122178469)
    public synchronized void resetActors() {
        actors = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 887003539)
    public List<Genres> getGenres() {
        if (genres == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GenresDao targetDao = daoSession.getGenresDao();
            List<Genres> genresNew = targetDao._queryMovie_Genres(id);
            synchronized (this) {
                if (genres == null) {
                    genres = genresNew;
                }
            }
        }
        return genres;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1988821389)
    public synchronized void resetGenres() {
        genres = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 215161401)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieDao() : null;
    }
}
