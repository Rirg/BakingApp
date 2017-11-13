package com.example.ricardo.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

public class IngredientsWidgetConfigure extends AppCompatActivity implements
        FetchRecipesData.OnTaskCompleted, AdapterView.OnItemClickListener {

    private ArrayList<String> mRecipesNames;
    private ArrayList<Recipe> mRecipes;
    private ArrayAdapter<String> mAdapter;
    private int mAppWidgetId;
    private SharedPreferences preferences;

    private static final String TAG = "IngredientsWidgetConfig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_widget_configure);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // Get the widget id
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // Create a new ArrayList to hold the data
        mRecipesNames = new ArrayList<>();

        ListView listView = findViewById(R.id.widget_config_listview);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mRecipesNames);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        new FetchRecipesData(this, this, FetchRecipesData.RECIPES_CODE, -1).execute();
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients,
                                ArrayList<Step> steps) {
        if (recipes != null) {
            Log.i(TAG, "onTaskCompleted: " + recipes.get(1).getName());
            mRecipes = recipes;
            for (Recipe recipe : recipes) {
                mRecipesNames.add(recipe.getName());
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Save the selected option in SharedPreferences using the AppWidgetId, this way it will be
        // unique for every instance of the widget
        Log.i(TAG, "onItemClick: WIDGET ID: " + String.valueOf(mAppWidgetId));
        preferences.edit().putInt("recipeId" + String.valueOf(mAppWidgetId), mRecipes.get(i).getId()).apply();
        preferences.edit().putString("recipeName"+ String.valueOf(mAppWidgetId), mRecipes.get(i).getName()).apply();
        Log.i(TAG, "onItemClick: " + mRecipesNames.get(i));

        // Inflate the initial layout when the user place the widget for the first time
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.ingredients_widget);

        Intent intent = new Intent(getApplicationContext(), ListWidgetService.class);
        intent.putExtra("recipeId", mRecipes.get(i).getId());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // Set the name of the recipe in the corresponding TextView
        views.setTextViewText(R.id.appwidget_recipe_name, mRecipes.get(i).getName());

        // Set the Remote Adapter to inflate the ListView with the ingredients
        views.setRemoteAdapter(R.id.appwidget_listview, intent);

        // Set an empty view
        views.setEmptyView(R.id.appwidget_listview, R.id.empty_view);

        // Call the updateAppWidget method using the AppWidgetManager
        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}