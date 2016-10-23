package com.diegotori.app.zmdb.mvp.presenters;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.model.MovieRankItem;
import com.diegotori.app.zmdb.mvp.views.TopTenMoviesView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Diego on 10/23/2016.
 */

public class TopTenMoviesPresenter extends MvpBasePresenter<TopTenMoviesView> {
    private final ZmdbApiService apiService;
    private Call<List<MovieRankItem>> networkCall;

    public TopTenMoviesPresenter(ZmdbApiService apiService){
        //TODO: Add cache here.
        this.apiService = apiService;
    }

    public void loadTopTenMovies(final boolean pullToRefresh){
        if(getView() != null){
            getView().showLoading(pullToRefresh);
        }
        if (networkCall != null && !networkCall.isCanceled()) {
            networkCall.cancel();
        }
        networkCall = apiService.moviesByRank(1, 10);
        networkCall.enqueue(new Callback<List<MovieRankItem>>() {
            @Override
            public void onResponse(Call<List<MovieRankItem>> call,
                                   Response<List<MovieRankItem>> response) {
                final List<MovieRankItem> items = response.body();
                //TODO: Can persist/cache data here if we like
                final TopTenMoviesView view = getView();
                if(view != null && isViewAttached()){
                    if(items != null && !(items.isEmpty())){
                        view.setData(items);
                        view.showContent();
                    } else {
                        view.showEmpty();
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
