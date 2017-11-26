package com.example.ricardo.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.adapters.StepsAdapter;
import com.example.ricardo.bakingapp.models.Step;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class StepsListFragment extends Fragment implements StepsAdapter.ListItemClickListener {

    private static final String TAG = "StepsListFragment";

    private OnStepSelected mCallback;
    private int mId;

    public interface OnStepSelected{
        void onStepSelected(int pos, int id);
    }

    private StepsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Makes sure that the StepsActivity implemented the callback interface
        try {
            mCallback = (OnStepSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepSelected");
        }

    }

    public StepsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_list, container, false);

        TextView ingredientsTv = rootView.findViewById(R.id.recipe_ingredients_tv);

        // Retrieve all the steps and id from the current recipe
        ArrayList<Step> steps = getArguments().getParcelableArrayList("steps");
        mId = getArguments().getInt("recipeId");

        mAdapter = new StepsAdapter(steps, this);

        RecyclerView rv = rootView.findViewById(R.id.steps_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

        // Set the onClickListener for the Ingredients TextView
        ingredientsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the callback to send the id to the StepsActivity
                mCallback.onStepSelected(-1, mId);
            }
        });

        return rootView;
    }


    @Override
    public void onListItemClickListener(int pos) {
        Log.i(TAG, "onListItemClickListener: " + pos);
        // Send the position using the callback to the StepsActivity
        mCallback.onStepSelected(pos, -1);
    }

}