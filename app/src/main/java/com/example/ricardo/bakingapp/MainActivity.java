package com.example.ricardo.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipesListFragment fragment = new RecipesListFragment();
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .add(R.id.recipes_list_container, fragment)
                .commit();

    }
}
