package com.example.ricardo.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;

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


public class FetchRecipesData extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private OnTaskCompleted mCallback;
    private int mCode;
    private int mId;

    private static final String TAG = "FetchRecipesData";

    public static final int RECIPES_CODE = 100;
    public static final int STEPS_CODE = 200;
    public static final int INGREDIENTS_CODE = 300;

    public static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public interface OnTaskCompleted {
        void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps);
    }

    public FetchRecipesData(Context context, OnTaskCompleted callback, int code, int id) {
        mContext = context;
        mCallback = callback;
        mCode = code;
        mId = id;
    }


    @Override
    protected String doInBackground(Void... voids) {

        if (!isOnline(mContext)) return null;

        String recipesData = null;

        try {
            recipesData = getResponseFromHttpUrl(new URL(RECIPES_URL));
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
            mCallback.onTaskCompleted(null, null, null);
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

        try {
            JSONArray recipesData = new JSONArray(jsonData);

            switch (mCode) {

                case RECIPES_CODE:
                    // Create a new ArrayList of Recipes to hold all the recipes
                    ArrayList<Recipe> recipes = new ArrayList<>();

                    // Iterate in the entire JSONArray to get all the recipes
                    for (int i = 0; i < recipesData.length(); i++) {
                        JSONObject object = recipesData.getJSONObject(i);
                        recipes.add(new Recipe(object.getInt("id"),
                                object.getString("name"),
                                object.getInt("servings"),
                                object.getString("image")));
                        Log.i(TAG, "fetchFromJson: " + object.getString("name"));
                    }
                    // Send the array with the results using the callback
                    mCallback.onTaskCompleted(recipes, null, null);
                    break;

                case INGREDIENTS_CODE:
                    // Create a new ArrayList of Ingredients to hold all the ingredients
                    ArrayList<Ingredient> ingredients = new ArrayList<>();

                    // Iterate in the entire JSONArray to find the object with the requested id
                    for (int i = 0; i < recipesData.length(); i++) {
                        JSONObject object = recipesData.getJSONObject(i);

                        // Check the object with the requested id
                        if (object.getInt("id") == mId) {

                            // Get the array of ingredients that match with the id
                            JSONArray arrayIngredients = object.getJSONArray("ingredients");

                            // Iterate in the entire array of ingredients
                            for (i = 0; i < arrayIngredients.length(); i++) {
                                JSONObject currentIngredient = arrayIngredients.getJSONObject(i);
                                ingredients.add(new Ingredient(
                                        currentIngredient.getDouble("quantity"),
                                        currentIngredient.getString("measure"),
                                        currentIngredient.getString("ingredient")));
                            }
                        }
                        Log.i(TAG, "fetchFromJson: " + object.getString("name"));
                        // Send the array with the results using the callback
                        mCallback.onTaskCompleted(null, ingredients, null);
                    }
                    break;

                case STEPS_CODE:
                    // Create a new ArrayList of Steps
                    ArrayList<Step> steps = new ArrayList<>();

                    // Iterate in the entire JSONArray
                    for (int i = 0; i < recipesData.length(); i++) {
                        JSONObject object = recipesData.getJSONObject(i);

                        // Check the id of the object with the passed id
                        if (object.getInt("id") == mId) {
                            // Get all the Array of steps
                            JSONArray arraySteps = object.getJSONArray("steps");

                            // Iterate in all the steps and get the information
                            for (i = 0; i < arraySteps.length(); i++) {
                                JSONObject currentStep = arraySteps.getJSONObject(i);
                                steps.add(new Step(
                                        currentStep.getInt("id"),
                                        currentStep.getString("shortDescription"),
                                        currentStep.getString("description"),
                                        currentStep.getString("videoURL"),
                                        currentStep.getString("thumbnailURL")));

                            }
                        }
                    }
                    // Send the array with the results using the callback
                    mCallback.onTaskCompleted(null, null, steps);
                    break;
            }

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