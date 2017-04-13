package com.example.android.movifo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adapts movie review data to a recycler view.
 * Created by Carl on 4/12/2017.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    private final String TAG = MovieReviewsAdapter.class.getSimpleName();
    private String[] reviewData;

    // The maximum number of reviews to display.
    private final int MAX_DISPLAY_REVIEWS = 10;

    private final String REVIEW_USER_KEY = "author";
    private final String REVIEW_TEXT_KEY = "content";

    @Override
    public MovieReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieReviewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieReviewsViewHolder holder, int position) {
        // Catch index out of bounds before it happens.
        if(position >= reviewData.length) return;

        // Cache the JSON string for this position.
        String reviewJsonString = reviewData[position];

        // Attempt to parse and load data from JSON into UI.
        try {
            JSONObject reviewJsonObject = new JSONObject(reviewJsonString);
            holder.mReviewUser.setText(reviewJsonObject.getString(REVIEW_USER_KEY));
            holder.mReviewText.setText(reviewJsonObject.getString(REVIEW_TEXT_KEY));
        } catch (JSONException e) {
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return reviewData == null ? 0 : Math.min(reviewData.length, MAX_DISPLAY_REVIEWS);
    }

    public void setReviewData(String[] newReviewData) {
        this.reviewData = newReviewData;
        notifyDataSetChanged();
    }

    class MovieReviewsViewHolder extends RecyclerView.ViewHolder {

        private TextView mReviewUser;
        private TextView mReviewText;

        public MovieReviewsViewHolder(View itemView) {
            super(itemView);
            mReviewUser = (TextView) itemView.findViewById(R.id.tv_review_user);
            mReviewText = (TextView) itemView.findViewById(R.id.tv_review);
        }
    }
}
