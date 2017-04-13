package com.example.android.movifo.util;

import android.util.Log;
import android.widget.ImageView;

import com.example.android.movifo.data.Movie;
import com.squareup.picasso.Picasso;

/**
 * Picasso utility function library.
 * Created by Carl on 4/10/2017.
 */

public class PicassoUtility {

    private static final String TAG = PicassoUtility.class.toString();

    public static void loadPosterImageIntoImageView(Movie movieData, ImageView targetView) {

        // Extract poster image URL from JSON.
        final String imageURLString = movieData.posterPath;
        Log.d(TAG, "Loaded image URL string is " + imageURLString);

        final String fullImageURLPath = NetworkUtility.constructImageURLString(imageURLString);
        Log.d(TAG, "Full image URL string is " + fullImageURLPath);

        // Load the view with the image.
        Picasso.with(targetView.getContext()).load(fullImageURLPath).into(targetView);
    }
}
