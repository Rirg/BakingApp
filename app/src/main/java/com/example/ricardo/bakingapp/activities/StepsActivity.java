package com.example.ricardo.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.fragments.IngredientsFragment;
import com.example.ricardo.bakingapp.fragments.StepDetailFragment;
import com.example.ricardo.bakingapp.fragments.StepsListFragment;
import com.example.ricardo.bakingapp.models.Ingredient;
import com.example.ricardo.bakingapp.models.Recipe;
import com.example.ricardo.bakingapp.models.Step;
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

public class StepsActivity extends AppCompatActivity implements StepsListFragment.OnStepSelected, Callback<List<Recipe>> {

    private Recipe mCurrentRecipe;
    @State
    ArrayList<Step> mSteps;
    @State
    ArrayList<Ingredient> mIngredients;

    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;

    @BindView(R.id.error_message_tv)
    TextView mErrorMessage;

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        ButterKnife.bind(this);

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
            mProgressBar.setVisibility(View.VISIBLE);

            // Check the internet connection
            if (MenuActivity.isOnline(this)) {
                ApiService api = RetroClient.getApiService();
                Call<List<Recipe>> call = api.getAllRecipesData();
                call.enqueue(this);
            } else {
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Finish the activity when the back button is pressed
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    // Save the steps and ingredients list in the bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onStepSelected(int pos, int id) {
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

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        // Hide the progress bar when the download is completed
        mProgressBar.setVisibility(View.INVISIBLE);

        if (response.isSuccessful()) {
            // Hide the error message if the response is successful
            mErrorMessage.setVisibility(View.INVISIBLE);

            // Create a new steps list and ingredients list to hold the downloaded data
            mSteps = new ArrayList<>();
            mIngredients = new ArrayList<>();

            // Loop on all the recipes data and
            for (Recipe recipe : response.body()) {
                // Check the corresponding recipe id
                if (recipe.getId().equals(mCurrentRecipe.getId())) {
                    // Get the steps list and ingredients list and add them to the global variables
                    mSteps.addAll(recipe.getSteps());
                    mIngredients.addAll(recipe.getIngredients());
                }
            }
            FragmentManager manager = getSupportFragmentManager();
            Bundle bundle;

            if (mSteps != null) {

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

        } else {
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}