package com.example.lenovo.moviereview;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.moviereview.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterClickHandler{

    public static final String TAG = DetailActivity.class.getSimpleName();

    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mRecyclerViewForTrailer;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView mRecyclerViewForReview;
    private TextView mMovieTitle;
    private TextView mPlotSynopsis;
    private TextView mUserRating;
    private TextView mReleaseDate;
    private ImageView mMovieImage;
    private MovieDetails moviedata;
    private Button mButton;
    private String mMovieId;
    private String[] onlyYear;
    private Cursor cursor = null;
    // Strings of keys will be passed in the AsyncTask class
    String[] requiredKeys = {"videos" ,"reviews"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        mRecyclerViewForTrailer = (RecyclerView) findViewById(R.id.rv_handle_movie_trailers);
        LinearLayoutManager layoutManagerForTrailer =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewForTrailer.setHasFixedSize(true);
        mRecyclerViewForTrailer.setLayoutManager(layoutManagerForTrailer);
        mTrailerAdapter = new TrailerAdapter(DetailActivity.this);
        mRecyclerViewForTrailer.setAdapter(mTrailerAdapter);

        mRecyclerViewForReview = (RecyclerView) findViewById(R.id.rv_handle_movie_comments);
        LinearLayoutManager linearLayoutManagerForReview =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewForReview.setHasFixedSize(true);
        mRecyclerViewForReview.setLayoutManager(linearLayoutManagerForReview);
        mReviewAdapter = new ReviewAdapter();
        mRecyclerViewForReview.setAdapter(mReviewAdapter);

        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mPlotSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mMovieImage = (ImageView) findViewById(R.id.iv_movie_image);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
            String checkMode = intentThatStartedThisActivity.getExtras().getString("mode");
            if(checkMode.equals("online")){
                moviedata = intentThatStartedThisActivity.getExtras().getParcelable("movieObject");
                mMovieId = moviedata.getMovieId();
                mMovieTitle.setText(moviedata.getOriginalTitle());
                mPlotSynopsis.setText(moviedata.getPlotSynopsis());
                // Extract only year from date of release
                onlyYear = moviedata.getReleaseDate().split("-");
                mReleaseDate.setText(onlyYear[0]);
                mUserRating.setText(moviedata.getUserRating());
                Picasso.with(this).load(moviedata.getImageThumbnail()).resize(400,600).into(mMovieImage);
            }else {
                mMovieId = intentThatStartedThisActivity.getExtras().getString("movieID");
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
                String[] selectionArgs = {mMovieId};
                try{
                    cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            selection,
                            selectionArgs,
                            null);
                    int movieNameIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
                    int ratingIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
                    int yearIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_YEAR);
                    int synopsisIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
                    int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
                    //Move cursor to the first position
                    cursor.moveToFirst();
                    mMovieTitle.setText(cursor.getString(movieNameIndex));
                    mPlotSynopsis.setText(cursor.getString(synopsisIndex));
                    mReleaseDate.setText(cursor.getString(yearIndex));
                    mUserRating.setText(cursor.getString(ratingIndex));
                    byte[] imgByte = cursor.getBlob(posterIndex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    mMovieImage.setImageBitmap(bitmap);
                }catch (Exception e){
                    Log.v(TAG, "Unable to load data from DB...");
                    e.printStackTrace();
                }
            }

        }

        if(NetworkUtils.isConnected(DetailActivity.this))
            new FetchVideosAndReviews().execute(requiredKeys);

        mButton = (Button) findViewById(R.id.bv_favourite_btn);
        //Initial initialization of button's text
        if(DecisionMaker.searchDataBase(DetailActivity.this, mMovieId))
            mButton.setText("Delete from favourite");
        else
            mButton.setText("Add to favourite");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean decision = DecisionMaker.searchDataBase(DetailActivity.this, mMovieId);
                if(decision)
                    mButton.setText("Delete from favourite");
                else
                    mButton.setText("Add to favourite");

                if(!decision){
                    BitmapDrawable drawable = (BitmapDrawable) mMovieImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    byte[] dbMovieImage = getBitmapAsByteArray(bitmap);
                    Uri uri = DecisionMaker.insertIntoDB(DetailActivity.this,
                            mMovieId,
                            mMovieTitle.getText().toString(),
                            mUserRating.getText().toString(),
                            onlyYear[0],
                            mPlotSynopsis.getText().toString(),
                            dbMovieImage);
                    if(uri != null){
                        Log.v(TAG, "Inserted Uri = " + uri.toString());
                    }
                    mButton.setText("Delete from favourite");
                } else{
                    int deleteMovie = DecisionMaker.deleteFromDB(DetailActivity.this, mMovieId);
                    if(deleteMovie != 0)
                        Log.v(TAG, "Successfully deleted movie from DB");
                    mButton.setText("Add to favourite");
                }
            }
        });
    }

    @Override
    public void onClick(String trailerKey) {
        Log.v(TAG, "Trailer key = " + trailerKey);
        //Built an intent
        Uri trailerUrlForWeb = Uri.parse("https://www.youtube.com/watch?v=" + trailerKey);
        Uri trailerUrlForYoutube = Uri.parse("vnd.youtube:" + trailerKey);
        Intent trailerIntentForWeb = new Intent(Intent.ACTION_VIEW, trailerUrlForWeb);
        Intent trailerIntentForYoutube = new Intent(Intent.ACTION_VIEW, trailerUrlForYoutube);

        try{
            // First try to run Youtube app if it is available
            startActivity(trailerIntentForYoutube);
        } catch (ActivityNotFoundException e){
            Log.v(TAG, "Youtube app is not present");
            //verify it resolves
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(trailerIntentForWeb, 0);
            boolean isIntentSafe = activities.size() > 0;
            //start an activity if it's safe
            if(isIntentSafe)
                startActivity(trailerIntentForWeb);
        }


    }

    public class FetchVideosAndReviews extends AsyncTask<String, Void, Void>{

        private ArrayList<String> trailerIDs = new ArrayList<>();
        private ReviewDetails mReviewDetails;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            URL urlForTrailers = NetworkUtils.builtUrlForVideosAndReviews(mMovieId, params[0]);
            URL urlForReviews = NetworkUtils.builtUrlForVideosAndReviews(mMovieId, params[1]);
            Log.v(TAG, "urlForTrailers = " + urlForTrailers);
            Log.v(TAG, "urlForReviews = " + urlForReviews);

            try{
                String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(urlForTrailers);
                trailerIDs = MovieJsonUtils.getSimpleTrailerFromJson(DetailActivity.this, jsonTrailerResponse);
                Log.v(TAG, "Length of trailerIDs = " + trailerIDs.size());

                String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(urlForReviews);
                mReviewDetails = MovieJsonUtils.getSimpleReviewsFromJson(DetailActivity.this, jsonReviewResponse);
                Log.v(TAG, "Length of ReviewDetails = " + mReviewDetails.getAuthorNames().size());

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mTrailerAdapter.setTrailerObject(trailerIDs);
            mReviewAdapter.setReviewObject(mReviewDetails);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                super. onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
