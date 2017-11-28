package com.example.ricardo.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.models.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ricardo on 9/16/17.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private ArrayList<Ingredient> mIngredients;
    private Context mContext;

    public IngredientsAdapter(ArrayList<Ingredient> ingredients, Context context) {
        mIngredients = ingredients;
        mContext = context;
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
        @BindView(R.id.ingredient_name_tv) TextView ingredient;
        @BindView(R.id.ingredient_quantity_tv) TextView quantity;
        @BindView(R.id.ingredient_measure_tv) TextView measure;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int pos) {
            ingredient.setText(mIngredients.get(pos).getIngredient());
            measure.setText(mContext.getString(R.string.ingredient_measure) + mIngredients.get(pos).getMeasure());
            quantity.setText(mContext.getString(R.string.ingredient_quantity) + String.valueOf(mIngredients.get(pos).getQuantity()));
        }
    }
}