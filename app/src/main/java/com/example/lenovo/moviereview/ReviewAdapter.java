package com.example.lenovo.moviereview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 6/22/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private ArrayList<String> authorNames = new ArrayList<>();
    private ArrayList<String> detailComment = new ArrayList<>();
    private int reviewTrack = 0;

    public void setReviewObject(ReviewDetails details){
        authorNames = details.getAuthorNames();
        notifyDataSetChanged();
        detailComment = details.getDetailComments();
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        public TextView mAuthorTextView;
        public TextView mCommentTextView;

        public ReviewAdapterViewHolder(View view){
            super(view);
            mAuthorTextView = (TextView) view.findViewById(R.id.tv_author_name);
            mCommentTextView = (TextView) view.findViewById(R.id.tv_detail_comment);
        }
    }
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForReviewItem = R.layout.review_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForReviewItem, parent, shouldAttachToParentImmediately);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        holder.mAuthorTextView.setText(authorNames.get(position));
        holder.mCommentTextView.setText(detailComment.get(position));
    }

    @Override
    public int getItemCount() {
        if(authorNames.size() == 0) return 0;
        return authorNames.size();
    }
}
