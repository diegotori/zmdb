package com.diegotori.app.zmdb.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diegotori.app.zmdb.R;
import com.diegotori.app.zmdb.Zmdb;
import com.diegotori.app.zmdb.mvp.TopTenMoviesAdapter;
import com.diegotori.app.zmdb.mvp.model.MovieRankItem;
import com.diegotori.app.zmdb.mvp.presenters.TopTenMoviesPresenter;
import com.diegotori.app.zmdb.mvp.views.TopTenMoviesView;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment;

import java.util.List;

/**
 * Created by Diego on 10/23/2016.
 */

public class TopTenMoviesFragment extends MvpLceFragment<SwipeRefreshLayout, List<MovieRankItem>,
        TopTenMoviesView, TopTenMoviesPresenter> implements TopTenMoviesView,
        SwipeRefreshLayout.OnRefreshListener {
    private Zmdb zmdbApp;
    private TopTenMoviesAdapter adapter;

    public TopTenMoviesFragment() {
        this(Zmdb.getInstance());
    }

    @SuppressLint("ValidFragment")
    TopTenMoviesFragment(Zmdb zmdbApp){
        this.zmdbApp = zmdbApp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_ten_movies, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        contentView.setOnRefreshListener(this);
        final RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.top_ten_movies_recyclerview);

        adapter = new TopTenMoviesAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        loadData(false);
    }

    @Override
    public void onRefresh() {
        presenter.loadTopTenMovies(true);
    }

    @Override
    public void showEmpty() {}

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.top_ten_movies_error_label);
    }

    @NonNull
    @Override
    public TopTenMoviesPresenter createPresenter() {
        return new TopTenMoviesPresenter(zmdbApp.getApiService());
    }

    @Override
    public void setData(List<MovieRankItem> data) {
        adapter.setMovieRankItems(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadTopTenMovies(pullToRefresh);
    }

    @Override public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
    }
}
