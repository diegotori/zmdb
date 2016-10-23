package com.diegotori.app.zmdb.mvp.views;

import com.diegotori.app.zmdb.mvp.model.MovieRankItem;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

/**
 * Created by Diego on 10/23/2016.
 */

public interface TopTenMoviesView extends MvpLceView<List<MovieRankItem>> {
    void showEmpty();
}
