package com.example.ricardo.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Ricardo on 9/16/17.
 */


public class FetchRecipes extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private OnTaskCompleted mCallback;
    private String mUrl;

    private static final String TAG = "FetchRecipes";

    public interface OnTaskCompleted {
        void onTaskCompleted(ArrayList<String> names);
    }

    public FetchRecipes(Context context, String url, OnTaskCompleted callback) {
        mContext = context;
        mCallback = callback;
        mUrl = url;
    }



    @Override
    protected String doInBackground(Void... voids) {

        if (!isOnline(mContext)) return null;

        String recipesData = null;
        String finalUrl = mUrl;

        try {
            recipesData = getResponseFromHttpUrl(new URL(finalUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipesData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s != null && !s.isEmpty()) {
            fetchFromJson(s);
        } else {
            mCallback.onTaskCompleted(null);
        }
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void fetchFromJson(String jsonData) {

        ArrayList<String> names = new ArrayList<>();

        try {
            JSONArray recipes = new JSONArray(jsonData);

            for (int i = 0; i < recipes.length(); i++) {
                JSONObject object = recipes.getJSONObject(i);
                names.add(object.getString("name"));
                Log.i(TAG, "fetchFromJson: " + object.getString("name"));
            }
            mCallback.onTaskCompleted(names);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Method for checking the current network status
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}