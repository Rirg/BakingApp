package com.example.ricardo.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.IngredientsAdapter;
import com.example.ricardo.bakingapp.pojos.Ingredient;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity {

    IngredientsAdapter mAdapter;
    private static final String TAG = "IngredientsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        // Get the ingredients ArrayList from the intent
        if (getIntent().hasExtra("ingredients")) {
            ingredients = getIntent().getExtras().getParcelableArrayList("ingredients");
        }

        // Set up the recycler view with the ingredients
        RecyclerView recyclerView = findViewById(R.id.ingredients_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new IngredientsAdapter(ingredients);
        recyclerView.setAdapter(mAdapter);


    }


}
