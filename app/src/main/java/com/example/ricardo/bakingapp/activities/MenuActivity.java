package com.example.ricardo.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ricardo.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.RecipesAdapter;
import com.example.ricardo.bakingapp.models.Ingredient;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.models.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements RecipesAdapter.ListItemClickListener,
        FetchRecipesData.OnTaskCompleted {

    private RecipesAdapter mAdapter;
    private ArrayList<Recipe> mRecipes;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check the bundle to retrieve the list if there is one
        if (savedInstanceState != null) {
            mRecipes = savedInstanceState.getParcelableArrayList("recipes");
            mAdapter = new RecipesAdapter(mRecipes, this);
        }
        // Else, just send an empty list and swap the list later in the onTaskCompleted callback
        else {
            mAdapter = new RecipesAdapter(new ArrayList<Recipe>(), this);
        }

        RecyclerView recyclerView;
        // Check if is a tablet or cellphone
        if (findViewById(R.id.recipes_grid_rv) != null) {
            recyclerView =  findViewById(R.id.recipes_grid_rv);
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

    // Implement this method to fetch after the just onCreate() method and use correctly the idle
    // resource
    @Override
    protected void onStart() {
        super.onStart();

        // Fetch data just if the recipes list is null
        if (mRecipes == null) {
            mIdlingResource = (SimpleIdlingResource) getIdlingResource();
            // Set the idling state to false before start fetching the data
            mIdlingResource.setIdleState(false);
            new FetchRecipesData(this, this,
                    FetchRecipesData.RECIPES_CODE, -1).execute();
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
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients,
                                ArrayList<Step> steps) {
        // Set the idle state of the idling resource to true when the download is completed
        mIdlingResource.setIdleState(true);
        if (recipes != null) {
            mRecipes = recipes;
            mAdapter.swapList(recipes);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("recipes", mRecipes);

    }
}
