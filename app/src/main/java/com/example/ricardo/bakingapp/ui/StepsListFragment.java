package com.example.ricardo.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
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
import com.example.ricardo.bakingapp.pojos.Step;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/16/17.
 */

public class StepsListFragment extends Fragment implements StepsAdapter.ListItemClickListener, View.OnClickListener {

    private static final String TAG = "StepsListFragment";

    private OnStepSelected mCallback;

    public interface OnStepSelected{
        void onStepSelected(int pos);
    }

    private StepsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStepSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }

    }

    public StepsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_list, container, false);

        TextView ingredientsTv = rootView.findViewById(R.id.recipe_ingredients_tv);
        ingredientsTv.setOnClickListener(this);

     /*   ArrayList<Step> steps = getArguments().getParcelableArrayList("steps");

        mAdapter = new StepsAdapter(steps, this);

        RecyclerView rv = rootView.findViewById(R.id.steps_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);
*/
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Step> steps = getArguments().getParcelableArrayList("steps");

        mAdapter = new StepsAdapter(steps, this);

        RecyclerView rv = view.findViewById(R.id.steps_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

    }

    @Override
    public void onListItemClickListener(int pos) {
        Log.i(TAG, "onListItemClickListener: " + pos);
        mCallback.onStepSelected(pos);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        // TODO launch an intent for the ingredients

    }
}