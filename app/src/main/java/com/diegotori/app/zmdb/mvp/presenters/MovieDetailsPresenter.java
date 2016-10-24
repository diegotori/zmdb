package com.diegotori.app.zmdb.mvp.presenters;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.mvp.ZmdbBasePresenter;
import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.diegotori.app.zmdb.mvp.views.MovieDetailsView;
import com.diegotori.app.zmdb.utils.ZmdbCache;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Diego on 10/23/2016.
 */

public class MovieDetailsPresenter extends ZmdbBasePresenter<MovieDetailsView> {
    private Call<List<MovieItem>> networkCall;

    public MovieDetailsPresenter(ZmdbApiService apiService, ZmdbCache cache) {
        super(apiService, cache);
    }

    public void loadMovieDetails(int movieId) {
        final MovieDetailsView view = getView();
        if(view != null){
            view.showLoading(false);
        }
        if (networkCall != null && !networkCall.isCanceled()) {
            networkCall.cancel();
        }
        final MovieItem cachedMovieDetails = getZmdbCache().getCachedMovieDetails(movieId);
        if(cachedMovieDetails != null){
            //Send back cached data.
            if(view != null){
                view.setData(cachedMovieDetails);
                view.showContent();
            }
            return;
        }
        networkCall = getApiService().movieDetails(movieId);
        networkCall.enqueue(new Callback<List<MovieItem>>() {
            @Override
            public void onResponse(Call<List<MovieItem>> call,
                                   Response<List<MovieItem>> response) {
                final List<MovieItem> items = response.body();
                final MovieDetailsView currentView = getView();
                if(currentView != null && isViewAttached()){
                    if(items != null && !(items.isEmpty())){
                        final MovieItem soleMovieDetail = items.get(0);
                        getZmdbCache().cacheMovieDetails(soleMovieDetail.getId(), soleMovieDetail);
                        currentView.setData(soleMovieDetail);
                        currentView.showContent();
                    } else {
                        currentView.showError(null, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MovieItem>> call, Throwable t) {
                final MovieDetailsView view = getView();
                if(view != null && isViewAttached()){
                    view.showError(t, false);
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
