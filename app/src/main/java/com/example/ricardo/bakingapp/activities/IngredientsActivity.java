package com.example.ricardo.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.IngredientsAdapter;
import com.example.ricardo.bakingapp.models.Ingredient;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.utils.ApiService;
import com.example.ricardo.bakingapp.utils.RetroClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientsActivity extends AppCompatActivity implements Callback<List<Recipe>> {

    // Make the adapter a global variable to swap the list when the fetch is complete
    private IngredientsAdapter mAdapter;
    private int mRecipeId;
    private ArrayList<Ingredient> mIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        // Set the title of the action bar
        getSupportActionBar().setTitle("Ingredients");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create a new ArrayList of Ingredients to inflate the ListView
        mIngredients = new ArrayList<>();

        // Get the extras from the intent in a bundle variable
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (getIntent().hasExtra("ingredients")) {
                // Get the ingredients list from the StepsActivity, no need to fetch data again
                mIngredients = extras.getParcelableArrayList("ingredients");
            }
            // This is launched by the widget and is needed to fetch the data here
            else if (getIntent().hasExtra("recipeId")) {
                // Get the recipe id
                mRecipeId = extras.getInt("recipeId");

                // Use retrofit to fetch data
                ApiService api = RetroClient.getApiService();
                Call<List<Recipe>> call = api.getAllRecipesData();
                call.enqueue(this);
            }
        }


        // Set up the recycler view with the ingredients
        RecyclerView recyclerView = findViewById(R.id.ingredients_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mAdapter = new IngredientsAdapter(mIngredients, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Finish the activity when the back button is pressed
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Finish the activity when is launched from the widget and the activity is no longer visible
        // (e.g user pressed home button)
        if (getIntent().hasExtra("recipeId")) finish();
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        if (response.isSuccessful()) {
            // Loop on the entire list of recipes and find the one with the specified id
            for (Recipe recipe : response.body()) {
                if (recipe.getId().equals(mRecipeId)) {
                    // Add all the ingredients to the adapter list
                    mIngredients.addAll(recipe.getIngredients());
                }
            }
            // Notify the adapter
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        // There was an error
    }
}