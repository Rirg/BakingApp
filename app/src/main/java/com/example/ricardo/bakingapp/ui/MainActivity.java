package com.example.ricardo.bakingapp.ui;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recipes ArrayList
        ArrayList<Recipe> recipes = new ArrayList<>();
        RecyclerView recyclerView;
        mAdapter = new RecipesAdapter(recipes, this);

        // Check if is a tablet or cellphone
        if (findViewById(R.id.recipes_grid_rv) != null) {
            recyclerView = (RecyclerView) findViewById(R.id.recipes_grid_rv);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.recipes_list_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);

        new FetchRecipesData(this, this, FetchRecipesData.RECIPES_CODE, -1).execute();

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
            mAdapter.swapList(recipes);
        }
    }
}
