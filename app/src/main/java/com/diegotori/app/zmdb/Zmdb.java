package com.diegotori.app.zmdb;

import android.app.Application;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The ZocDoc Movie DataBase (ZMDB).
 * Created by Diego on 10/21/2016.
 */

public class Zmdb extends Application {
    private static Zmdb instance;
    private Retrofit retrofit;
    private ZmdbApiService zmdbApiService;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://interview.zocdoc.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        zmdbApiService = retrofit.create(ZmdbApiService.class);
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
}
