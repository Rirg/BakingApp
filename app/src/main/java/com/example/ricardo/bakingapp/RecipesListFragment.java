package com.example.ricardo.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class RecipesListFragment extends Fragment implements FetchRecipes.OnTaskCompleted {

    public static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private ArrayList<String> mRecipes;

    public RecipesListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        new FetchRecipes(getContext(), RECIPES_URL, this);

        // TODO set the list with the adapter

        return rootView;
    }

    @Override
    public void onTaskCompleted(ArrayList<String> names) {

    }
}
