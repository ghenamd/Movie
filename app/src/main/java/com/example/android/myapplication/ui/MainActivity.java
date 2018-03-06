package com.example.android.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.data.MovieContract;
import com.example.android.myapplication.data.MovieDbHelper;
import com.example.android.myapplication.model.Movie;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.network.RestManager;
import com.example.android.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ghena on 24/02/2018.
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClicked {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private RestManager mManager;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.network_error)
    TextView mStatus;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        ifConnected();
        mAdapter = new MovieAdapter(new ArrayList<MovieResult>(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Fetch and Sort Movies by Popularity
        if (id == R.id.action_popularity) {
            setTitle(getString(R.string.popular));
            if (isConnected()) {
                mManager = new RestManager();
                Call<Movie> movieCall = mManager.getMovieClient().getPopularMovies(Constants.API_KEY);
                movieCall.enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        List<MovieResult> results = response.body().getResults();
                        mAdapter.addMovieResult(results);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                    }
                });
                return true;
            } else {
                mStatus.setVisibility(View.VISIBLE);
                mStatus.setText(R.string.mobile_network_not_available);

            }
        }
        else if (id == R.id.action_top_rated) {
            setTitle(getString(R.string.top_rated));
            //Fetch and sort Movies by Rating
            mManager = new RestManager();
            Call<Movie> movieCall = mManager.getMovieClient().getTopRatedMovies(Constants.API_KEY);
            movieCall.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    List<MovieResult> movieResults = response.body().getResults();
                    mAdapter.addMovieResult(movieResults);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mStatus.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {

                }
            });
            return true;
        }
        else if (id == R.id.action_favourite) {
            setTitle(getString(R.string.favourite));
            mStatus.setVisibility(View.INVISIBLE);
            // A list of Favourite Movies
            setMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setManager() {
        mManager = new RestManager();
        Call<Movie> movieCall = mManager.getMovieClient().getUpcomingMovies(Constants.API_KEY);
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                List<MovieResult> movieResults = response.body().getResults();
                mAdapter.addMovieResult(movieResults);
                mProgressBar.setVisibility(View.INVISIBLE);
                mStatus.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    //This method checks if there is Network if there is we fetch the data from internet
    // otherwise we inform the user of an Unavailable Network
    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    //If the user is connected to the Internet we set the RestManger
    private void ifConnected() {
        if (isConnected()) {
            setManager();
        } else {
            mStatus.setText(R.string.mobile_network_not_available);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClickedMovie(MovieResult movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Constants.PARCEL, movie);
        startActivity(intent);
    }
    // Method that creates a List of movies from the local database and passes it to the MovieAdapter
    public void setMovie() {
        MovieDbHelper mMovieDbHelper = new MovieDbHelper(this);
        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_ID,
                MovieContract.MovieEntry.COLUMN_PICTURE,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
        };
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        Log.v(TAG, DatabaseUtils.dumpCursorToString(cursor));
        if (cursor.getCount() == 0) {
            mStatus.setText(R.string.no_favourite_movie);
            mStatus.setVisibility(View.VISIBLE);
        }
        List<MovieResult> movieResults = new ArrayList<>();
        while (cursor.moveToNext()) {
            MovieResult result = new MovieResult();
            result.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            result.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
            result.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PICTURE)));
            result.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            result.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            result.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movieResults.add(result);
        }
        mAdapter.clear();
        mAdapter.addMovieResult(movieResults);
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If the title of the ActionBar is Favourite
        //then we setMovie method so we get and updated list of
        //the favourite movies
        CharSequence sequence = getSupportActionBar().getTitle();
        Log.v(TAG, String.valueOf(sequence));
        if (String.valueOf(sequence).equals(Constants.FAVOURITE)){
            setMovie();
        }
    }
}
