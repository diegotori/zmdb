package com.diegotori.app.zmdb.mvp;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.utils.ZmdbCache;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Diego on 10/23/2016.
 */

public abstract class ZmdbBasePresenter<V extends MvpView> extends MvpBasePresenter<V> {
    private final ZmdbApiService apiService;
    private final ZmdbCache cache;

    public ZmdbBasePresenter(ZmdbApiService apiService, ZmdbCache cache){
        this.apiService = apiService;
        this.cache = cache;
    }

    protected ZmdbCache getZmdbCache() {
        return cache;
    }

    protected ZmdbApiService getApiService() {
        return apiService;
    }
}
