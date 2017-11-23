package com.example.ricardo.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.ricardo.bakingapp.activities.MenuActivity;
import com.example.ricardo.bakingapp.models.Ingredient;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.models.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

/**
 * Created by Ricardo on 11/9/17.
 */

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, FetchRecipesData.OnTaskCompleted {
    private static final String TAG = "ListRemoteViewsFactory";
    private Context mContext;
    private ArrayList<String> mIngredients;
    private int mAppWidgetId;
    private int mRecipeId;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        // Get the recipe id from the shared preferences using the AppWidget id
        mRecipeId = mContext.getSharedPreferences(mContext.getPackageName(),
                Context.MODE_PRIVATE).getInt("recipeId" + String.valueOf(mAppWidgetId), 1);
    }

    // Initialize the data set.
    @Override
    public void onCreate() {

        // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        mIngredients = new ArrayList<>();
        Log.i(TAG, "onCreate: " + "entra al oncreate");
        new FetchRecipesData(mContext, this, FetchRecipesData.INGREDIENTS_CODE, mRecipeId).execute();
    }

    // Given the position (index) of a WidgetItem in the array, use the item's text value in
    // combination with the app widget item XML file to construct a RemoteViews object.
    @Override
    public RemoteViews getViewAt(int position) {

        // position will always range from 0 to getCount() - 1.
        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        // feed row
        String ingredient = mIngredients.get(position);
        rv.setTextViewText(R.id.widget_ingredient_name_tv, ingredient);

        // end feed row
        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in ListViewWidgetProvider.
        Bundle extras = new Bundle();

        //extras.putInt(IngredientsWidgetProvider.EXTRA_ITEM, position);

        Intent fillInIntent = new Intent(mContext, MenuActivity.class);
        fillInIntent.putExtra("homescreen_meeting", ingredient);
        fillInIntent.putExtras(extras);

        // Return the RemoteViews object.
        return rv;
    }

    @Override
    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDestroy() {
        mIngredients.clear();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients,
                                ArrayList<Step> steps) {
        // Check if the ArrayList isn't null
        if (ingredients != null) {
            // Clear the list before adding new items
            mIngredients.clear();
            // Foreach loop to get all the ingredients names and add them to the list
            for (Ingredient ingredient : ingredients) {
                mIngredients.add(ingredient.getName());
            }
            // Notify the AppWidget using the AppWidgetManager
            AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,
                    R.id.appwidget_listview);
        }
    }
}