package com.example.ricardo.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.pojos.Recipe;

public class StepsActivity extends AppCompatActivity {

    private Recipe mCurrentRecipe;

    private static final String TAG = "StepsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        if (getIntent().hasExtra("recipe")) {
            mCurrentRecipe = getIntent().getParcelableExtra("recipe");
        }

        Log.i(TAG, "onCreate: " + mCurrentRecipe.getName());

        Bundle bundle = new Bundle();
        bundle.putInt("id", mCurrentRecipe.getId());

        StepsListFragment fragment = new StepsListFragment();
        fragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .add(R.id.steps_container, fragment)
                .commit();
    }
}
