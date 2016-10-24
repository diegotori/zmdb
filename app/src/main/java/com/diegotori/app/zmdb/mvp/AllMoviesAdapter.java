package com.diegotori.app.zmdb.mvp;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.diegotori.app.zmdb.R;
import com.diegotori.app.zmdb.mvp.model.MovieItem;
import com.diegotori.app.zmdb.mvp.model.MovieRankItem;
import com.diegotori.app.zmdb.utils.ZmdbUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Diego on 10/23/2016.
 */

public class AllMoviesAdapter extends RecyclerView.Adapter<AllMoviesAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<MovieItem> movieRankItems;

    public AllMoviesAdapter(final Context context){
        this.inflater = LayoutInflater.from(context);
        this.movieRankItems = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = inflater.inflate(R.layout.item_movie_rank, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(movieRankItems.get(position));
    }

    public void setMovieRankItems(final List<MovieItem> newItems){
        movieRankItems.clear();
        if(newItems != null && !(newItems.isEmpty())){
            movieRankItems.addAll(newItems);
        }
    }

    public MovieItem getItemAtPosition(int position){
        return movieRankItems.get(position);
    }

    @Override
    public int getItemCount() {
        return movieRankItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle;
        TextView movieRank;
        ImageView moviePoster;
        Picasso picasso;
        Resources res;
        public ViewHolder(View itemView) {
            super(itemView);
            this.movieTitle = (TextView) itemView.findViewById(R.id.movieTitle);
            this.movieRank = (TextView) itemView.findViewById(R.id.movieRank);
            this.moviePoster = (ImageView) itemView.findViewById(R.id.moviePoster);
            this.picasso = Picasso.with(itemView.getContext());
            this.res = itemView.getResources();
        }

        void bindItem(final MovieItem item) {
            final String placeholderUrl = ZmdbUtils.generatePlaceholderUrl(res, 80, 120);
            picasso.load(placeholderUrl).into(moviePoster);

            movieTitle.setText("");
            movieTitle.setVisibility(GONE);
            final String title = item.getName();
            if(title != null && !(title.isEmpty())){
                movieTitle.setText(title);
                movieTitle.setVisibility(VISIBLE);
            }

            movieRank.setText("");
            movieRank.setVisibility(GONE);
            final int rank = item.getRank();
            if(rank > 0){
                movieRank.setText(res.getString(R.string.movie_rank_label, rank));
                movieRank.setVisibility(VISIBLE);
            }
        }
    }
}
