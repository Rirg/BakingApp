package com.example.ricardo.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.example.ricardo.bakingapp.activities.IngredientsActivity;
import com.example.ricardo.bakingapp.activities.MenuActivity;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.utils.ApiService;
import com.example.ricardo.bakingapp.utils.RetroClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientsWidgetConfigure extends AppCompatActivity implements AdapterView.OnItemClickListener, Callback<List<Recipe>> {

    private ArrayList<String> mRecipesNames;
    private List<Recipe> mRecipes;
    private ArrayAdapter<String> mAdapter;
    private int mAppWidgetId;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_widget_configure);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Get the shared preferences for saving the selected recipe id and name
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

        if (MenuActivity.isOnline(this)) {
            // Use retrofit to fetch data
            ApiService api = RetroClient.getApiService();
            Call<List<Recipe>> call = api.getAllRecipesData();
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        if (response.isSuccessful()) {
            mRecipes = response.body();
            for (Recipe recipe : mRecipes) {
                mRecipesNames.add(recipe.getName());
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Save the selected option recipe id and recipe name in SharedPreferences using the
        // AppWidgetId, this way it will be unique for every instance of the widget
        preferences.edit().putInt("recipeId" + String.valueOf(mAppWidgetId), mRecipes.get(i).getId()).apply();
        preferences.edit().putString("recipeName"+ String.valueOf(mAppWidgetId), mRecipes.get(i).getName()).apply();

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

        // Trigger general recipe click
        Intent startActivityIntent = new Intent(getApplicationContext(), IngredientsActivity.class);

        // Send the recipe id as an extra
        startActivityIntent.putExtra("recipeId", mRecipes.get(i).getId());

        // Create the PendingIntent using the AppWidgetId as unique request code to get individual
        // data for every instance of the Widget
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                mAppWidgetId, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_recipe_name, startActivityPendingIntent);

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