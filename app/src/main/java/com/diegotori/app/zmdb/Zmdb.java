package com.diegotori.app.zmdb;

import android.app.Application;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.database.entities.DaoMaster;
import com.diegotori.app.zmdb.database.entities.DaoSession;

import org.greenrobot.greendao.database.Database;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * The ZocDoc Movie DataBase (ZMDB).
 * Created by Diego on 10/21/2016.
 */

public class Zmdb extends Application {
    private static Zmdb instance;
    private Retrofit retrofit;
    private ZmdbApiService zmdbApiService;
    private DaoSession daoSession;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.ZOCDOC_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        zmdbApiService = retrofit.create(ZmdbApiService.class);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "movies-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static Zmdb getInstance() {
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public ZmdbApiService getApiService() {
        return zmdbApiService;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
