package com.example.android.movifo.util;

import android.app.Activity;

/**
 * Created by Carl on 4/9/2017.
 */

public class DeviceUtility {

    /**
     * Helper function for getting the current orientation.
     * @param activity Activity context for checking orientation.
     * @return The current device's screen orientation.
     */
    public static int getDeviceOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation;
    }

}
