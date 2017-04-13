package com.example.android.movifo.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Carl on 4/10/2017.
 */

public class JsonUtility {
    private static String TAG = JsonUtility.class.getSimpleName();

    /**
     * Extracts individual json strings from bulk results string.
     * @param jsonString Incoming JSON string to parse.
     * @return Array of strings that encompasses individual result objects.
     */
    public static String[] expandResultsString(String jsonString) {

        String[] results = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            final String ATTR_RESULT_ARRAY = "results";
            JSONArray jsonArray = jsonObject.getJSONArray(ATTR_RESULT_ARRAY);

            if(jsonArray.length() > 0) {
                // Initialize results array equal to size of results array.
                results = new String[jsonArray.length()];
                // Push all individual objects' string representation to the array.
                for (int i = 0; i < jsonArray.length(); ++i) {
                    results[i] = jsonArray.get(i).toString();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "expandResultsString: " + e.toString());
        }

        return results;
    }
}
