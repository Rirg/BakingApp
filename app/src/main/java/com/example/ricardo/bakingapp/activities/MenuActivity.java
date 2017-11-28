package com.example.ricardo.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ricardo.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.RecipesAdapter;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.utils.ApiService;
import com.example.ricardo.bakingapp.utils.RetroClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity implements RecipesAdapter.ListItemClickListener, Callback<List<Recipe>> {

    private RecipesAdapter mAdapter;
    @State
    ArrayList<Recipe> mRecipes;

    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;

    @BindView(R.id.error_message_tv)
    TextView mErrorMessage;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);

        // Restore the state using Icepick
        Icepick.restoreInstanceState(this, savedInstanceState);

        // If the ArrayList is not null, then pass it the a new instance of the adapter
        if (savedInstanceState != null) {
            mAdapter = new RecipesAdapter(mRecipes, this);
        }
        // Else, just send an empty list and swap the list later in the onTaskCompleted callback
        else {
            mRecipes = new ArrayList<>();
            mAdapter = new RecipesAdapter(mRecipes, this);
        }

        RecyclerView recyclerView;
        // Check if is a tablet or cellphone
        if (findViewById(R.id.recipes_grid_rv) != null) {
            recyclerView = findViewById(R.id.recipes_grid_rv);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView = findViewById(R.id.recipes_list_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
        }

        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);

    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    // Implement this method to fetch just after the onCreate() method and use correctly the idle
    // resource
    @Override
    protected void onStart() {
        super.onStart();

        // Fetch data just if the recipes list is null or empty
        if (mRecipes == null || mRecipes.size() == 0) {
            // Set the idling state to false before start fetching the data
            if (mIdlingResource != null) mIdlingResource.setIdleState(false);

            // Show the progress bar
            mProgressBar.setVisibility(View.VISIBLE);

            // Check the internet connection
            if (isOnline(this)) {
                // Use retrofit to fetch data
                ApiService api = RetroClient.getApiService();
                Call<List<Recipe>> call = api.getAllRecipesData();
                call.enqueue(this);
            } else {
                // Show an error message if there isn't internet connection
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onListItemClickListener(Recipe selectedRecipe) {
        // Create and intent for the StepsActivity and send the selected recipe as an extra
        Intent intent = new Intent(this, StepsActivity.class);
        intent.putExtra("recipe", selectedRecipe);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);

    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        // Set the idle state of the idling resource to true when the download is completed
        if (mIdlingResource != null) mIdlingResource.setIdleState(true);

        // Hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        if (response.isSuccessful()) {
            // Hide the error message if the response is successful
            mErrorMessage.setVisibility(View.INVISIBLE);

            // Add all the recipe list from the response to the recipe list from the adapter
            mRecipes.addAll(response.body());
            mAdapter.notifyDataSetChanged();
        } else {
            // Show the error message if the response wasn't successful
            mErrorMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        // Set the idle state of the idling resource to true when the download is completed
        if (mIdlingResource != null) mIdlingResource.setIdleState(true);

        // Hide the progress bar and show an error message
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}