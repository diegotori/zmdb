package com.diegotori.app.zmdb.mvp.presenters;

import com.diegotori.app.zmdb.api_service.ZmdbApiService;
import com.diegotori.app.zmdb.mvp.ZmdbBasePresenter;
import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.diegotori.app.zmdb.mvp.views.AllMoviesView;
import com.diegotori.app.zmdb.utils.ZmdbCache;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Diego on 10/24/2016.
 */

public class AllMoviesPresenter extends ZmdbBasePresenter<AllMoviesView> {
    private Call<List<MovieItem>> networkCall;
    public AllMoviesPresenter(ZmdbApiService apiService, ZmdbCache cache) {
        super(apiService, cache);
    }

    public void loadAllMovies(boolean pullToRefresh) {
        final AllMoviesView view = getView();
        if(view != null){
            view.showLoading(pullToRefresh);
        }
        if (networkCall != null && !networkCall.isCanceled()) {
            networkCall.cancel();
        }
        final List<MovieItem> cachedMovies = getZmdbCache().getAllCachedMovies();
        if(cachedMovies != null){
            //Send back cached data.
            if(view != null){
                view.setData(cachedMovies);
                view.showContent();
            }
            return;
        }
        networkCall = getApiService().allMovies();
        networkCall.enqueue(new Callback<List<MovieItem>>() {
            @Override
            public void onResponse(Call<List<MovieItem>> call,
                                   Response<List<MovieItem>> response) {
                final List<MovieItem> items = response.body();
                final AllMoviesView currentView = getView();
                if(currentView != null && isViewAttached()){
                    if(items != null && !(items.isEmpty())){
                        currentView.setData(items);
                        currentView.showContent();
                    } else {
                        currentView.showError(null, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MovieItem>> call, Throwable t) {
                final AllMoviesView view = getView();
                if(view != null && isViewAttached()){
                    view.showError(t, false);
                }
            }
        });
    }

    public void filterMovies(final String actor, final String genre){
        final List<MovieItem> filteredMovies = new ArrayList<>();
        final List<MovieItem> cachedMovies = getZmdbCache().getAllCachedMovies();
        if(cachedMovies != null){
            final boolean hasValidActor = actor != null && !(actor.isEmpty());
            final boolean hasValidGenre = genre != null && !(genre.isEmpty());
            for(MovieItem cachedMovie : cachedMovies) {
                final List<String> actors = cachedMovie.getActors();
                final List<String> genres = cachedMovie.getGenres();
                final boolean hasValidActors = actors != null && !(actors.isEmpty());
                final boolean hasValidGenres = genres != null && !(genres.isEmpty());
                final boolean containsActor = hasValidActor && hasValidActors && actors.contains(actor);
                final boolean containsGenre = hasValidGenre && hasValidGenres && genres.contains(genre);
                if(containsActor && containsGenre) {
                    filteredMovies.add(cachedMovie);
                } else if (containsActor){
                    filteredMovies.add(cachedMovie);
                } else if (containsGenre){
                    filteredMovies.add(cachedMovie);
                }
            }
            if(getView() != null){
                getView().setData(filteredMovies);
            }
        }
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
