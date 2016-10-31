package com.diegotori.app.zmdb.database.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Diego on 10/27/2016.
 */
@Entity
public class Genres {
    @Id
    private Long id;

    @Unique
    private String name;

    @Generated(hash = 1663930015)
    public Genres(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 319014004)
    public Genres() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
