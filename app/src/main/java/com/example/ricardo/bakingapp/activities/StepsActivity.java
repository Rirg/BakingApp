package com.example.ricardo.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.fragments.IngredientsFragment;
import com.example.ricardo.bakingapp.fragments.StepDetailFragment;
import com.example.ricardo.bakingapp.fragments.StepsListFragment;
import com.example.ricardo.bakingapp.models.Ingredient;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.models.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;

public class StepsActivity extends AppCompatActivity implements FetchRecipesData.OnTaskCompleted,
        StepsListFragment.OnStepSelected {

    private Recipe mCurrentRecipe;
    @State
    ArrayList<Step> mSteps;
    @State
    ArrayList<Ingredient> mIngredients;

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        getSupportActionBar().setTitle("Steps");

        if (getIntent().hasExtra("recipe")) {
            // Get the current selected recipe
            mCurrentRecipe = getIntent().getParcelableExtra("recipe");
        }

        // Check if is two pane or single
        if (findViewById(R.id.details_container) != null) mTwoPane = true;

        // Restore the state using Icepick
        Icepick.restoreInstanceState(this, savedInstanceState);

        // Fetch the steps and ingredients just if we have null list
        if (mSteps == null || mIngredients == null) {
            new FetchRecipesData(this, this, FetchRecipesData.STEPS_CODE, mCurrentRecipe.getId()).execute();
            new FetchRecipesData(this, this, FetchRecipesData.INGREDIENTS_CODE, mCurrentRecipe.getId()).execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        // Variables that we will need
        Bundle bundle;
        FragmentManager manager = getSupportFragmentManager();

        // Save the ingredients array in the global variable just if it isn't null
        if (ingredients != null) mIngredients = ingredients;

        // Make sure that we don't have a null array to proceed
        if (steps != null) {
            mSteps = steps;

            // Create a bundle to save and send all the steps and the id of the current recipe
            bundle = new Bundle();
            bundle.putParcelableArrayList("steps", mSteps);
            bundle.putInt("recipeId", mCurrentRecipe.getId());

            // Add a new instance of the StepsListFragment using the fragment manager
            // to show the steps list.
            StepsListFragment fragment = new StepsListFragment();
            fragment.setArguments(bundle);

            manager.beginTransaction()
                    .add(R.id.steps_container, fragment)
                    .commit();


            // If it's a tablet, inflate the StepDetailFragment and send all the steps in a bundle
            if (mTwoPane) {
                bundle = new Bundle();
                bundle.putParcelableArrayList("steps", mSteps);

                StepDetailFragment detailFragment = new StepDetailFragment();
                detailFragment.setArguments(bundle);

                manager.beginTransaction()
                        .add(R.id.details_container, detailFragment)
                        .commit();
            }
        }
    }

    // Save the steps and ingredients list in the bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onStepSelected(int pos, int id) {
        // TODO fix overexposed fragments "Recipe introduction"
        // Is a step option
        if (id == -1) {
            // If is two pane, replace the details_container with the new step
            if (mTwoPane) {
                StepDetailFragment detailFragment = new StepDetailFragment();

                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.details_container, detailFragment)
                        .commit();

                detailFragment.setCurrentStep(mSteps.get(pos));
            }
            // Else, use an intent to start the StepDetailActivity
            else {
                Intent intent = new Intent(this, StepDetailActivity.class);
                intent.putExtra("position", pos);
                intent.putParcelableArrayListExtra("steps", mSteps);
                startActivity(intent);
            }
        }
        // Is the ingredients option
        else {
            if (mTwoPane) {
                // If is two pane, replace the details_container with the IngredientsFragment
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ingredients", mIngredients);

                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setArguments(bundle);

                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.details_container, ingredientsFragment)
                        .commit();
            }
            // Else, use an intent to start the IngredientsActivity passing the Ingredients ArrayList
            else {
                Intent intent = new Intent(this, IngredientsActivity.class);

                // Send the id of the current recipe to the IngredientsActivity
                intent.putParcelableArrayListExtra("ingredients", mIngredients);
                startActivity(intent);
            }
        }
    }
}