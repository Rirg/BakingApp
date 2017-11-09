package com.example.ricardo.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.RecipesAdapter;
import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.ListItemClickListener,
        FetchRecipesData.OnTaskCompleted {

    private RecipesAdapter mAdapter;
    private ArrayList<Recipe> mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check the bundle to retrieve the list if there is
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
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);


        // Fetch data just if the recipes list is null
        if (mRecipes == null) new FetchRecipesData(this, this, FetchRecipesData.RECIPES_CODE, -1).execute();

    }

    @Override
    public void onListItemClickListener(Recipe selectedRecipe) {
        // Create and intent for the StepsActivity and send the selected recipe as an extra
        Intent intent = new Intent(this, StepsActivity.class);
        intent.putExtra("recipe", selectedRecipe);
        startActivity(intent);
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
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
