package com.example.ricardo.bakingapp.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.fragments.StepDetailFragment;
import com.example.ricardo.bakingapp.models.Step;

import java.util.ArrayList;

public class StepDetailActivity extends AppCompatActivity {

    private int mPos;
    private ArrayList<Step> mSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail);

        Bundle intentBundle = getIntent().getExtras();

        // Get the steps and the position from the intent
        mSteps = intentBundle.getParcelableArrayList("steps");
        mPos = intentBundle.getInt("position");

        getSupportActionBar().setTitle(mSteps.get(mPos).getShortDescription());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get the previous and next buttons
        Button btnPrev = findViewById(R.id.detail_btn_prev);
        Button btnNext = findViewById(R.id.detail_btn_next);

        // Set the video in fullscreen when the device is in landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }

        // Create a new instance of the StepDetailFragment and set arguments with extras in a bundle
        // the steps and current position
        StepDetailFragment detailFragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("steps", mSteps);
        bundle.putInt("pos", mPos);

        detailFragment.setArguments(bundle);

        // Add the StepDetailFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.details_container, detailFragment)
                .commit();


        // Check that the buttons aren't null to proceed
        if (btnPrev != null && btnNext != null) {

            btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the position is greater than 0
                    if (mPos > 0) {
                        // Check if the position is less than the steps size
                        StepDetailFragment newDetailFragment = new StepDetailFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.details_container, newDetailFragment)
                                .commit();
                        // Decrease the position by 1 every time the user clicks the previous button
                        mPos--;

                        // Change the title of the action bar
                        getSupportActionBar().setTitle(mSteps.get(mPos).getShortDescription());

                        // Call the method setCurrentStep() passing the current step
                        newDetailFragment.setCurrentStep(mSteps.get(mPos));
                    }
                }
            });


            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the position is less than the steps size
                    if (mPos < mSteps.size() - 1) {
                        // Replace the previous StepDetailFragment with the new step
                        StepDetailFragment newDetailFragment = new StepDetailFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.details_container, newDetailFragment)
                                .commit();
                        // Increase the position by 1 every time the user clicks the next button
                        mPos++;

                        // Change the title of the action bar
                        getSupportActionBar().setTitle(mSteps.get(mPos).getShortDescription());

                        // Call the method setCurrentStep() passing the current step
                        newDetailFragment.setCurrentStep(mSteps.get(mPos));
                    }
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
