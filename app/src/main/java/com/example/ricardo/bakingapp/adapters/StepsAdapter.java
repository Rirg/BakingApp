package com.example.ricardo.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.models.Step;

import java.util.ArrayList;

/**
 * Created by Ricardo on 9/18/17.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private static final String TAG = "StepsAdapter";

    private ArrayList<Step> mSteps;
    private ListItemClickListener mListener;

    public interface ListItemClickListener {
        void onListItemClickListener(int pos);
    }

    public StepsAdapter(ArrayList<Step> steps, ListItemClickListener listener) {
        mSteps = steps;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView shortDescriptionTv;

         ViewHolder(View itemView) {
            super(itemView);

            shortDescriptionTv = itemView.findViewById(R.id.step_short_description_tv);
            itemView.setOnClickListener(this);
        }

        void bind(int pos) {
            shortDescriptionTv.setText(mSteps.get(pos).getShortDescription());
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: " + getAdapterPosition());
            mListener.onListItemClickListener(getAdapterPosition());
        }
    }

    public void swapList(ArrayList<Step> steps) {
        if (steps != null) {
            mSteps = steps;
            notifyDataSetChanged();
        }
    }
}

