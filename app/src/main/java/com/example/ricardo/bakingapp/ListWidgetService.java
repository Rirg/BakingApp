package com.example.ricardo.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

/**
 * Created by Ricardo on 11/9/17.
 */

public class ListWidgetService extends RemoteViewsService {

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<String> mIngredients;


    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    // Initialize the data set.

    public void onCreate() {

        // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        mIngredients = new ArrayList<>();
        mIngredients.add("sal");
        mIngredients.add("jengibre");
        mIngredients.add("azucar");
        mIngredients.add("pimienta");

    }

    // Given the position (index) of a WidgetItem in the array, use the item's text value in
    // combination with the app widget item XML file to construct a RemoteViews object.
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

        extras.putInt(IngredientsWidgetProvider.EXTRA_ITEM, position);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("homescreen_meeting", ingredient);
        fillInIntent.putExtras(extras);

        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.ingredient_name_tv, fillInIntent);

        // Return the RemoteViews object.
        return rv;
    }

    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    public void onDataSetChanged() {
        // Fetching JSON data from server and add them to mIngredients arraylist
        new FetchRecipesData(mContext, new FetchRecipesData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
                for (Ingredient ingredient : ingredients) {
                    mIngredients.add(ingredient.getName());
                }
            }
        }, FetchRecipesData.INGREDIENTS_CODE, 2);
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public void onDestroy() {
        mIngredients.clear();
    }

    public boolean hasStableIds() {
        return true;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

}
