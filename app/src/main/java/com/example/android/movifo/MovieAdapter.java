package com.example.android.movifo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movifo.data.Movie;
import com.example.android.movifo.util.PicassoUtility;

/**
 * Adapter for populating movie posters on main activity.
 * Created by Carl on 4/9/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // Internal cursor reference for iterating over query result.
    private Cursor mCursor;

    private Context mContext;
    private MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onImageClick(Movie movie);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler onClickHandler) {
        this.mContext = context;
        this.mMovieAdapterOnClickHandler = onClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // If cursor movement is successful...
        if(mCursor.moveToPosition(position)) {
            // Load the poster image with Picasso.
            PicassoUtility.loadPosterImageIntoImageView(Movie.createMovieFromCursor(mCursor), holder.mPosterImageView);

            final Movie movieAtCursor = Movie.createMovieFromCursor(mCursor);

            holder.mPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieAdapterOnClickHandler.onImageClick(movieAtCursor);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_poster);
        }
    }

    public void setMovieData(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }
}
