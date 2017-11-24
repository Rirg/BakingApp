package com.example.ricardo.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.ricardo.bakingapp.activities.IngredientsActivity;

public class IngredientsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get the shared preferences using the context to retrieve the recipe name and recipe id
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        // update each of the app widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {

            // Set up the intent that starts the ListViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListWidgetService.class);

            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

            // Get the recipe name from the Shared Preferences using the AppWidget Id as key
            String recipeName = preferences.getString("recipeName" + String.valueOf(appWidgetId), "");
            views.setTextViewText(R.id.appwidget_recipe_name, recipeName);

            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            views.setRemoteAdapter(R.id.appwidget_listview, intent);

            // Trigger general recipe click
            Intent startActivityIntent = new Intent(context, IngredientsActivity.class);

            // Send the recipe id as extra in the intent, retrieve it from SharedPreferences
            startActivityIntent.putExtra("recipeId", preferences.getInt("recipeId" + String.valueOf(appWidgetId), 1));

            // Create the PendingIntent using the AppWidgetId as unique request code to get individual
            // data for every instance of the Widget
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context,
                    appWidgetId, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.appwidget_recipe_name, startActivityPendingIntent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews  object above.
            views.setEmptyView(R.id.appwidget_listview, R.id.empty_view);

            //
            // Do additional processing specific to this app widget...
            //
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}