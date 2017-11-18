package com.example.ricardo.bakingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.IngredientsAdapter;
import com.example.ricardo.bakingapp.models.Ingredient;

import java.util.ArrayList;

/**
 * Created by Ricardo on 11/6/17.
 */

public class IngredientsFragment extends android.support.v4.app.Fragment  {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        ArrayList<Ingredient> ingredients = getArguments().getParcelableArrayList("ingredients");

        RecyclerView recyclerView = rootView.findViewById(R.id.ingredients_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
        recyclerView.setAdapter(adapter);


        return rootView;
    }


}
