package com.example.ricardo.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.IngredientsAdapter;
import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity implements FetchRecipesData.OnTaskCompleted {

    // Make the adapter a global variable to swap the list when the fetch is complete
    private IngredientsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        // Create a new ArrayList of Ingredients to inflate the ListView
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        // Save the extras from the intent in a bundle variable
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Get the ingredients ArrayList from the intent sent by
            if (getIntent().hasExtra("ingredients")) {
                ingredients = extras.getParcelableArrayList("ingredients");
            } else if (getIntent().hasExtra("recipeId")) {
                // Fetch new data to get the ingredients of the corresponding recipe id
                new FetchRecipesData(this, this, FetchRecipesData.INGREDIENTS_CODE,
                        extras.getInt("recipeId")).execute();
            }
        }

        // Set up the recycler view with the ingredients
        RecyclerView recyclerView = findViewById(R.id.ingredients_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new IngredientsAdapter(ingredients);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        if (ingredients != null) mAdapter.swapList(ingredients);
    }
}
