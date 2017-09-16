package com.example.ricardo.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    ArrayList<String> mRecipes;

    public RecipesAdapter(ArrayList<String> recipes) {
        mRecipes = recipes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView recipeTitle = (TextView) itemView.findViewById(R.id.recipe_name_tv);

        public ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(int pos) {
            recipeTitle.setText(mRecipes.get(pos));
        }

        @Override
        public void onClick(View view) {

        }
    }
}