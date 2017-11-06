package com.example.ricardo.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ricardo.bakingapp.R;
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

        new FetchRecipesData(this, this, FetchRecipesData.STEPS_CODE, mCurrentRecipe.getId()).execute();

    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {

        mSteps = steps;

        // Create a bundle to save and send all the steps and the id of the current recipe
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("steps", mSteps);
        bundle.putInt("recipeId", mCurrentRecipe.getId());

        StepsListFragment fragment = new StepsListFragment();
        fragment.setArguments(bundle);


        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .add(R.id.steps_container, fragment)
                .commit();

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

    @Override
    public void onStepSelected(int pos, int id) {
        // Is a step option
        if (pos != -1) {
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
                // TODO inflate the Ingredients fragment
            } else {
                // TODO launch an intent for the Ingredients activity
                Intent intent = new Intent(this, IngredientsActivity.class);
                // Send the id of the current recipe to the IngredientsActivity
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }
    }

}