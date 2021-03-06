package com.example.ricardo.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.models.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ricardo on 9/16/17.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private ArrayList<Recipe> mRecipes;
    private ListItemClickListener mOnListItemClickListener;

    public interface ListItemClickListener {
        void onListItemClickListener(Recipe recipe);
    }

    public RecipesAdapter(ArrayList<Recipe> recipes, ListItemClickListener listener) {
        mRecipes = recipes;
        mOnListItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipes_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_name_tv) TextView recipeTitle;
        @BindView(R.id.recipe_image_view) ImageView recipeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int pos) {
            recipeTitle.setText(mRecipes.get(pos).getName());
            // Check if there is an image and load it using Glide if there is
            if (mRecipes.get(pos).getImage() != null && !mRecipes.get(pos).getImage().isEmpty()) {
                Glide.with(itemView)
                        .load(mRecipes.get(pos).getImage())
                        .into(recipeImage);
            }
        }

        @Override
        public void onClick(View view) {
            mOnListItemClickListener.onListItemClickListener(mRecipes.get(getAdapterPosition()));
        }
    }
}