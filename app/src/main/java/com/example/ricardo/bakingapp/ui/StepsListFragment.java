package com.example.ricardo.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.StepsAdapter;
import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class StepsListFragment extends Fragment implements FetchRecipesData.OnTaskCompleted, StepsAdapter.ListItemClickListener {

    private static final String TAG = "StepsListFragment";

    private Recipe mRecipe;
    private StepsAdapter mAdapter;

    public StepsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_list, container, false);

        mRecipe = getArguments().getParcelable("recipe");

        Log.i(TAG, "onCreateView: " + mRecipe.getName());

        mAdapter = new StepsAdapter(new ArrayList<Step>(), this);
        RecyclerView rv = rootView.findViewById(R.id.steps_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

        Log.i(TAG, "onCreateView: " + mRecipe.getId());
        new FetchRecipesData(getContext(), this, FetchRecipesData.STEPS_CODE, mRecipe.getId()).execute();

        return rootView;
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        if (steps != null) {
            mAdapter.swapList(steps);
        }
    }

    @Override
    public void onListItemClickListener(int pos) {

    }
}