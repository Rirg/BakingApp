package com.example.ricardo.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.IngredientsAdapter;
import com.example.ricardo.bakingapp.pojos.Ingredient;
import com.example.ricardo.bakingapp.pojos.Recipe;
import com.example.ricardo.bakingapp.pojos.Step;
import com.example.ricardo.bakingapp.utils.FetchRecipesData;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity implements FetchRecipesData.OnTaskCompleted {

    IngredientsAdapter mAdapter;
    private static final String TAG = "IngredientsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        RecyclerView recyclerView = findViewById(R.id.ingredients_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new IngredientsAdapter(new ArrayList<Ingredient>());
        recyclerView.setAdapter(mAdapter);

        int id = getIntent().getIntExtra("id", -1);
        if (id != -1) {

            new FetchRecipesData(this, this,  FetchRecipesData.INGREDIENTS_CODE, id).execute();


        }
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> recipes, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        Log.i(TAG, "onTaskCompleted: " + ingredients);
        mAdapter.swapList(ingredients);

    }
}
