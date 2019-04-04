package com.example.lenovo.moviereview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lenovo.moviereview.data.MovieContract;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by LENOVO on 5/23/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<MovieDetails> imageUrlSource = new ArrayList<>();
    private Cursor cursor = null;
    private Context context;
    final private MovieAdapterClickHandler mClickHandler;
    private int modeChecker;

    public interface MovieAdapterClickHandler{
        void onClickForOnline(MovieDetails movie);
        void onClickForOffline(String movieID);
    }

    public MovieAdapter(Context context, MovieAdapterClickHandler movie){
        this.context = context;
        mClickHandler = movie;
    }

    public void setMovieObject(ArrayList<MovieDetails> sampleMovies){
        imageUrlSource = sampleMovies;
        modeChecker = 0;
        notifyDataSetChanged();
        Log.v(TAG, "imageUrlSource size = " + imageUrlSource.size());
    }

    public void setCursorObject(Cursor c){
        cursor = c;
        modeChecker = 1;
        notifyDataSetChanged();
        Log.v(TAG, "Size of cursor = " + cursor.getCount());
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mMoviePoster;

        public MovieAdapterViewHolder(View view){
            super(view);
            mMoviePoster = (ImageView) view.findViewById(R.id.iv_movie_posters);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(modeChecker == 1){
                int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                cursor.moveToPosition(position);
                String movieId = cursor.getString(movieIdIndex);
                mClickHandler.onClickForOffline(movieId);
            } else{
                String movieName = imageUrlSource.get(position).getOriginalTitle();
                Log.v(TAG, "Clicked image = " + movieName);
                mClickHandler.onClickForOnline(imageUrlSource.get(position));
            }
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForImageItem = R.layout.image_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForImageItem, parent, shouldAttachToParentImmediately);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        if(modeChecker == 1){
            int movieImageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            cursor.moveToPosition(position);
            byte[] imageByte = cursor.getBlob(movieImageIndex);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            holder.mMoviePoster.setImageBitmap(bitmap);
        } else{
            Picasso.with(context).load(imageUrlSource.get(position).
                    getImageThumbnail()).into(holder.mMoviePoster);
        }
    }

    @Override
    public int getItemCount() {
        if(modeChecker == 1){
            if(cursor.getCount() == 0) return 0;
            return cursor.getCount();
        } else {
            if(imageUrlSource == null) return 0;
            return imageUrlSource.size();
        }

    }
}
