package com.example.lenovo.moviereview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lenovo.moviereview.data.MovieContract;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressIndicator;
    private TextView mErrorTextView;
    private final static String popularMovieQuery = "popular";
    private final static String mostVotedMovieQuery = "top_rated";
    private final static String myFavoriteList = "favorite";
    private final static String DECIDE_MODE = "mode";
    private final static String MOVIE_ID = "movieID";
    private static final String USER_CHOICE = "choice";
    private static final String LIST_STATE_KEY = "list_state";
    private String movieSearchParameter = popularMovieQuery;
    private Parcelable listState;
    public String searchQuery = popularMovieQuery;
    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_handle_movie_images);
        mProgressIndicator = (ProgressBar) findViewById(R.id.pb_show_progress);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mMovieAdapter = new MovieAdapter(MainActivity.this, MainActivity.this);
        mRecyclerView.setAdapter(mMovieAdapter);
        // Default call to FetchMovieDetails with popularity parameter
        if(NetworkUtils.isConnected(MainActivity.this)) {
            if(savedInstanceState != null){
                String result = savedInstanceState.getString(USER_CHOICE);
                movieSearchParameter = result;
                Log.v(TAG, "Within onCreate result = " + result);
                new FetchMovieDetails().execute(result);
            } else{
                new FetchMovieDetails().execute(popularMovieQuery);
            }
        }else{
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClickForOnline(MovieDetails movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToSend = new Intent(context, destinationClass);
        intentToSend.putExtra(DECIDE_MODE, "online");
        intentToSend.putExtra("movieObject",movie);
        startActivity(intentToSend);
    }

    @Override
    public void onClickForOffline(String movieID) {
        Log.v(TAG, "Required movie ID = " + movieID);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DECIDE_MODE, "offline");
        intent.putExtra(MOVIE_ID, movieID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(NetworkUtils.isConnected(MainActivity.this)) {
            if (id == R.id.select_popular) {
                Log.v(TAG, "Selected item = popular movies");
                movieSearchParameter = popularMovieQuery;
                new FetchMovieDetails().execute(popularMovieQuery);
            }

            if (id == R.id.select_most_voted) {
                Log.v(TAG, "Selected item = most voted movies");
                movieSearchParameter = mostVotedMovieQuery;
                new FetchMovieDetails().execute(mostVotedMovieQuery);
            }
        }else{
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }

        if(id == R.id.select_my_favourites){
            Log.v(TAG, "Selecting my favourite movies");
            movieSearchParameter = myFavoriteList;
            new FetchMovieDetails().execute(myFavoriteList);
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieDetails extends AsyncTask<String, Void, Void>{

        // Always initialize MovieDetails ArrayList here
        ArrayList<MovieDetails> movieList = new ArrayList<>();
        Cursor cursor = null;
        String searchParameter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.INVISIBLE);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            searchParameter = params[0];
            Log.v(TAG, "result for searchParameter = " + searchParameter);
            if(params[0].equals("favorite")){
                try{
                    cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e){
                    Log.v(TAG, "Failed to load data asynchronously....");
                    e.printStackTrace();
                    return null;
                }

            } else{
                URL movieRequestUrl = NetworkUtils.builtURL(params[0]);
                try{
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                    movieList = MovieJsonUtils
                            .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mProgressIndicator.setVisibility(View.INVISIBLE);

            if(searchParameter.equals("favorite")){
                Log.v(TAG, "Size of cursor = " + cursor.getCount());
                mMovieAdapter.setCursorObject(cursor);
            } else{
                Log.v(TAG, "ArrayList size = " + movieList.size());
                for(int i = 0; i < movieList.size(); i++){
                    Log.v(TAG, "Movie ID = " + movieList.get(i).getMovieId());
                    Log.v(TAG, "Title = " + movieList.get(i).getOriginalTitle());
                    Log.v(TAG, "Image = " + movieList.get(i).getImageThumbnail());
                    Log.v(TAG, "Plot synopsis = " + movieList.get(i).getPlotSynopsis());
                    Log.v(TAG, "Avg vote = " + movieList.get(i).getUserRating());
                    Log.v(TAG, "Release date = " + movieList.get(i).getReleaseDate());
                }
                mMovieAdapter.setMovieObject(movieList);
            }

            Log.v(TAG, "result for searchQuery = " + MainActivity.this.searchQuery);
            if(MainActivity.this.searchQuery.equals(searchParameter)){
                Log.v(TAG, "result for searchQuery = inside if " + MainActivity.this.searchQuery);
                if(listState != null){
                    layoutManager.onRestoreInstanceState(listState);
                }
            } else{
                MainActivity.this.searchQuery = searchParameter;
                Log.v(TAG, "result for searchQuery = inside else " + MainActivity.this.searchQuery);
            }
            Log.v(TAG, "demo result = *********************************************************");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String query = movieSearchParameter;
        outState.putString(USER_CHOICE, query);
        listState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }
}