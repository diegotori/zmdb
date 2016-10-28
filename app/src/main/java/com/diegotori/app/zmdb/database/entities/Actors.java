package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Diego on 10/27/2016.
 */
@Entity
public class Actors {
    @Id(autoincrement = true)
    private long id;

    @Unique
    private String name;

    @Generated(hash = 2046427353)
    public Actors(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 403017033)
    public Actors() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
