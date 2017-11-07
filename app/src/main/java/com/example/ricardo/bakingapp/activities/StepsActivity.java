package com.example.ricardo.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.fragments.IngredientsFragment;
import com.example.ricardo.bakingapp.fragments.StepDetailFragment;
import com.example.ricardo.bakingapp.fragments.StepsListFragment;
import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

public class StepsActivity extends AppCompatActivity implements FetchRecipesData.OnTaskCompleted, StepsListFragment.OnStepSelected {

    private Recipe mCurrentRecipe;
    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;

    private static final String TAG = "StepsActivity";
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        if (getIntent().hasExtra("recipe")) {
            mCurrentRecipe = getIntent().getParcelableExtra("recipe");
        }

        Log.i(TAG, "onCreate: " + mCurrentRecipe.getName());


        if (findViewById(R.id.details_container) != null) mTwoPane = true;

        // Fetch the steps and ingredients
        new FetchRecipesData(this, this, FetchRecipesData.STEPS_CODE, mCurrentRecipe.getId()).execute();
        new FetchRecipesData(this, this, FetchRecipesData.INGREDIENTS_CODE, mCurrentRecipe.getId()).execute();
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

            // Inflate a new instance of the StepsListFragment using the fragment manager
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

    @Override
    public void onStepSelected(int pos, int id) {
        // Is a step option
        if (id == -1) {
            if (mTwoPane) {
                StepDetailFragment detailFragment = new StepDetailFragment();

                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.details_container, detailFragment)
                        .commit();

                detailFragment.setCurrentStep(mSteps.get(pos));
            } else {
                Intent intent = new Intent(this, StepDetailActivity.class);
                intent.putExtra("position", pos);
                intent.putParcelableArrayListExtra("steps", mSteps);
                startActivity(intent);
            }
        }
        // Is the ingredients option
        else {
            if (mTwoPane) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ingredients", mIngredients);

                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setArguments(bundle);

                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.details_container, ingredientsFragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, IngredientsActivity.class);

                // Send the id of the current recipe to the IngredientsActivity
                intent.putParcelableArrayListExtra("ingredients", mIngredients);
                startActivity(intent);
            }
        }
    }
}