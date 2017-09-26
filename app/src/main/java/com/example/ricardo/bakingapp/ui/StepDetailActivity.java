package com.example.ricardo.bakingapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.pojos.Step;

import java.util.ArrayList;

public class StepDetailActivity extends AppCompatActivity {

    int mPos;
    ArrayList<Step> mSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail);

        Button btnPrev = (Button) findViewById(R.id.detail_btn_prev);
        Button btnNext = (Button) findViewById(R.id.detail_btn_next);

        // Set the video in fullscreen when the device is in landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }

        Bundle intentBundle = getIntent().getExtras();

        mSteps = intentBundle.getParcelableArrayList("steps");
        mPos = intentBundle.getInt("position");

        StepDetailFragment detailFragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("steps", mSteps);
        bundle.putInt("pos", mPos);

        detailFragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.details_container, detailFragment)
                .commit();


        if (btnPrev != null && btnNext != null) {

            btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPos > 0) {

                        StepDetailFragment newDetailFragment = new StepDetailFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.details_container, newDetailFragment)
                                .commit();

                        mPos--;
                        newDetailFragment.setCurrentStep(mSteps.get(mPos));
                    }
                }
            });


            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPos < mSteps.size() - 1) {
                        StepDetailFragment newDetailFragment = new StepDetailFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.details_container, newDetailFragment)
                                .commit();

                        mPos++;
                        newDetailFragment.setCurrentStep(mSteps.get(mPos));
                    }
                }
            });
        }


    }
}
