package com.diegotori.app.zmdb.mvp.presenters;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.mvp.ZmdbBasePresenter;
import com.diegotori.app.zmdb.mvp.model.MovieRankItem;
import com.diegotori.app.zmdb.mvp.views.TopTenMoviesView;
import com.diegotori.app.zmdb.utils.ZmdbCache;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Diego on 10/23/2016.
 */

public class TopTenMoviesPresenter extends ZmdbBasePresenter<TopTenMoviesView> {

    private Call<List<MovieRankItem>> networkCall;

    public TopTenMoviesPresenter(ZmdbApiService apiService, ZmdbCache cache){
        super(apiService, cache);
    }

    public void loadTopTenMovies(final boolean pullToRefresh){
        final TopTenMoviesView view = getView();
        if(view != null){
            view.showLoading(pullToRefresh);
        }
        if (networkCall != null && !networkCall.isCanceled()) {
            networkCall.cancel();
        }
        final List<MovieRankItem> cachedItems = getZmdbCache().getCachedTopTenMovies();
        if(cachedItems != null && !(cachedItems.isEmpty())){
            //Send back cached data.
            if(view != null){
                view.setData(cachedItems);
                view.showContent();
            }
            return;
        }
        networkCall = getApiService().moviesByRank(0, 11);
        networkCall.enqueue(new Callback<List<MovieRankItem>>() {
            @Override
            public void onResponse(Call<List<MovieRankItem>> call,
                                   Response<List<MovieRankItem>> response) {
                final List<MovieRankItem> items = response.body();
                final TopTenMoviesView currentView = getView();
                if(currentView != null && isViewAttached()){
                    if(items != null && !(items.isEmpty())){
                        getZmdbCache().cacheTopTenMovies(items);
                        currentView.setData(items);
                        currentView.showContent();
                    } else {
                        currentView.showEmpty();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MovieRankItem>> call, Throwable t) {
                final TopTenMoviesView view = getView();
                if(view != null && isViewAttached()){
                    view.showError(t, pullToRefresh);
                }
            }
        });
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            if (networkCall != null) {
                networkCall.cancel();
            }
        }
    }
}
