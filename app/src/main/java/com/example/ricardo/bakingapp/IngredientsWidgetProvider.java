package com.example.ricardo.bakingapp;
/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.ricardo.bakingapp.activities.MainActivity;

import java.util.ArrayList;

public class IngredientsWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "IngredientsWidgetProvid";

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String EXTRA_ITEM = "com.example.edockh.EXTRA_ITEM";
    private ArrayList<String> mIngredients = new ArrayList<>();
    Context mContext;
    int[] mAppWidgetIds;

    @Override
    public void onReceive(Context context, Intent intent) {


        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        Log.i(TAG, "onReceive: " + "Entra al onreceive");

        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, IngredientsWidgetProvider.class));
            Log.i("received", intent.getAction());
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_listview);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        mAppWidgetIds = appWidgetIds;

        Log.i(TAG, "onUpdate: " + "entered onUpdate");

        // update each of the app widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {

            // Set up the intent that starts the ListViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListWidgetService.class);

            // Add the app widget ID to the intent extras.
            intent.putStringArrayListExtra("ingredients", mIngredients);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

            // Get the recipe name from the Shared Preferences using the AppWidget Id as key
            String recipeName = mContext.getSharedPreferences(mContext.getPackageName(),
                    Context.MODE_PRIVATE).getString("recipeName" + String.valueOf(appWidgetId), "");
            views.setTextViewText(R.id.appwidget_recipe_name, recipeName);

            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            views.setRemoteAdapter(R.id.appwidget_listview, intent);

            // Trigger ListView item click
            Intent startActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context,
                    0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //views.setPendingIntentTemplate(R.id.appwidget_listview, startActivityPendingIntent);

            views.setOnClickPendingIntent(R.id.appwidget_parent, startActivityPendingIntent);

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