package com.diegotori.app.zmdb.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diegotori.app.zmdb.R;
import com.diegotori.app.zmdb.Zmdb;
import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.diegotori.app.zmdb.mvp.presenters.MovieDetailsPresenter;
import com.diegotori.app.zmdb.mvp.views.MovieDetailsView;
import com.diegotori.app.zmdb.utils.ZmdbCache;
import com.diegotori.app.zmdb.utils.ZmdbUtils;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Diego on 10/23/2016.
 */

public class MovieDetailsFragment extends
        MvpLceFragment<ScrollView, MovieItem, MovieDetailsView, MovieDetailsPresenter>
        implements MovieDetailsView {
    private static final String EXTRA_MOVIE_ID = "extra_movie_id";
    private Zmdb zmdbApp;
    private ZmdbCache zmdbCache;
    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieDirector;
    private TextView movieDuration;
    private TextView movieDescription;
    private TextView movieActors;
    private TextView movieGenres;


    public static MovieDetailsFragment newInstance(final int movieId){
        final MovieDetailsFragment frag = new MovieDetailsFragment();
        final Bundle args = new Bundle();
        args.putInt(EXTRA_MOVIE_ID, movieId);
        frag.setArguments(args);
        return frag;
    }

    public MovieDetailsFragment() {
        this(Zmdb.getInstance(), ZmdbCache.getInstance());
    }

    @SuppressLint("ValidFragment")
    MovieDetailsFragment(Zmdb zmdbApp, ZmdbCache zmdbCache){
        this.zmdbApp = zmdbApp;
        this.zmdbCache = zmdbCache;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moviePoster = (ImageView) view.findViewById(R.id.movie_details_poster);
        movieTitle = (TextView) view.findViewById(R.id.movie_details_title);
        movieDirector = (TextView) view.findViewById(R.id.movie_details_director);
        movieDuration = (TextView) view.findViewById(R.id.movie_details_duration);
        movieDescription = (TextView) view.findViewById(R.id.movie_details_description);
        movieActors = (TextView) view.findViewById(R.id.movie_details_actors);
        movieGenres = (TextView) view.findViewById(R.id.movie_details_genres);
        final Button buyTicketsButton = (Button) view.findViewById(R.id.buy_tickets_button);
        buyTicketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zocdoc.com"));
                v.getContext().startActivity(intent);
            }
        });
        loadData(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBarTitle();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            // Set title
            setActionBarTitle();
        }
    }

    public void setActionBarTitle() {
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null) {
            // Set title
            actionBar.setTitle(R.string.movie_details_label);
        }
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.movie_details_error_label);
    }

    @NonNull
    @Override
    public MovieDetailsPresenter createPresenter() {
        return new MovieDetailsPresenter(zmdbApp.getApiService(), zmdbCache);
    }

    @Override
    public void setData(MovieItem data) {
        final String placeholderUrl = ZmdbUtils.generatePlaceholderUrl(getResources(), 120, 180);
        Picasso.with(getContext()).load(placeholderUrl).into(moviePoster);
        movieTitle.setText(data.getName());
        movieDirector.setText(getString(R.string.movie_director_label, data.getDirector()));
        movieDuration.setText(getString(R.string.movie_duration_label, data.getDuration()));
        movieDescription.setText(data.getDescription());
        movieActors.setText(TextUtils.join(", ", data.getActors()));
        movieGenres.setText(TextUtils.join(", ", data.getGenres()));
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        final int movieId = getArguments() != null ? getArguments().getInt(EXTRA_MOVIE_ID) : 0;
        presenter.loadMovieDetails(movieId);
    }
}
