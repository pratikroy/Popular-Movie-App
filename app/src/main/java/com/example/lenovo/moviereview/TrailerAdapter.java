package com.example.lenovo.moviereview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by LENOVO on 6/22/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();
    private ArrayList<String> trailerDetails = new ArrayList<>();
    private int trailerCount = 1;
    final private TrailerAdapterClickHandler mClickHandler;

    public interface TrailerAdapterClickHandler{
        void onClick(String trailerKey);
    }

    public TrailerAdapter(TrailerAdapterClickHandler clickEvent){
        mClickHandler = clickEvent;
    }

    public void setTrailerObject(ArrayList<String> trailerList){
        trailerDetails = trailerList;
        notifyDataSetChanged();
        Log.v(TAG, "Size of TrailerDetails = " + trailerDetails.size());
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTrailerTextView;

        public TrailerAdapterViewHolder(View view){
            super(view);
            mTrailerTextView = (TextView) view.findViewById(R.id.tv_show_trailer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.v(TAG, "Clicked position = " + position);
            mClickHandler.onClick(trailerDetails.get(position));
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIDForTrailerItem = R.layout.trailer_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIDForTrailerItem, parent, shouldAttachToParentImmediately);

        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        String trailer = "Play trailer " + trailerCount;
        holder.mTrailerTextView.setText(trailer);
        trailerCount++;
    }

    @Override
    public int getItemCount() {
        if(trailerDetails.size() == 0)
            return 0;
        return trailerDetails.size();
    }
}
