package com.example.ricardo.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.pojos.Ingredient;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private ArrayList<Ingredient> mIngredients;

    public IngredientsAdapter(ArrayList<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView ingredient;
        TextView measure;
        TextView quantity;

        public ViewHolder(View itemView) {

            super(itemView);
            ingredient = itemView.findViewById(R.id.ingredient_name_tv);
            measure = itemView.findViewById(R.id.ingredient_measure_tv);
            quantity = itemView.findViewById(R.id.ingredient_quantity_tv);
        }

        void bind(int pos) {
            ingredient.setText(mIngredients.get(pos).getName());
            measure.setText("Measure: " + mIngredients.get(pos).getMeasure());
            quantity.setText("Quantity: " + String.valueOf(mIngredients.get(pos).getQuantity()));
        }

    }

    public void swapList(ArrayList<Ingredient> ingredients) {
        if (ingredients != null) {
            mIngredients = ingredients;
            notifyDataSetChanged();
        }
    }
}