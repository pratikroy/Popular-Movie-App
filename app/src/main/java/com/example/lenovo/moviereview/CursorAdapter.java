package com.example.lenovo.moviereview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.moviereview.data.MovieContract;

/**
 * Created by LENOVO on 6/25/2018.
 */

public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.MovieViewHolder> {

    private Cursor mCursor = null;
    private Context mContext;

    public CursorAdapter(Context context){
        this.mContext = context;
    }

    public Cursor updateCursor(Cursor c){
        if(mCursor == c){
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        if(c != null){
            this.notifyDataSetChanged();
        }

        return temp;
    }

    @Override
    public CursorAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.favourite_movies, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CursorAdapter.MovieViewHolder holder, int position) {
        int movieNameIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
        int movieImageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        mCursor.moveToPosition(position);

        String movieName = mCursor.getString(movieNameIndex);
        byte[] imgByte = mCursor.getBlob(movieImageIndex);
        Bitmap image = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);;

        holder.mMovieName.setText(movieName);
        holder.mMoviePoster.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;
        }

        return mCursor.getCount();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView mMovieName;
        ImageView mMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMovieName = (TextView) itemView.findViewById(R.id.tv_favourite_movie_name);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_favorite_movie);
        }
    }


}
