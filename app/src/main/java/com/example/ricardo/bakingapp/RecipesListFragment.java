package com.example.ricardo.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class RecipesListFragment extends Fragment implements FetchRecipes.OnTaskCompleted, RecipesAdapter.ListItemClickListener {

    public static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private RecipesAdapter mAdapter;

    public RecipesListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recipes_list_rv);

        ArrayList<String> recipes = new ArrayList<>();
        mAdapter = new RecipesAdapter(recipes, this);

        new FetchRecipes(getContext(), RECIPES_URL, this).execute();

        // TODO set the list with the adapter
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return rootView;
    }

    @Override
    public void onTaskCompleted(ArrayList<String> names) {
        mAdapter.swapList(names);
    }

    @Override
    public void onListItemClickListener(int pos) {

    }
}
