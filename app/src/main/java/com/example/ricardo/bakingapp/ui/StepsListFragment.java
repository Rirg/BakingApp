package com.example.ricardo.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class StepsListFragment extends Fragment implements FetchRecipesData.OnTaskCompleted, StepsAdapter.ListItemClickListener, View.OnClickListener {

    private static final String TAG = "StepsListFragment";

    private StepsAdapter mAdapter;

    public StepsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_list, container, false);

        TextView ingredientsTv = rootView.findViewById(R.id.recipe_ingredients_tv);
        ingredientsTv.setOnClickListener(this);

        int id = getArguments().getInt("id");


        mAdapter = new StepsAdapter(new ArrayList<Step>(), this);
        RecyclerView rv = rootView.findViewById(R.id.steps_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

        new FetchRecipesData(getContext(), this, FetchRecipesData.STEPS_CODE, id).execute();

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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        // TODO launch an intent for the ingredients

    }
}