package com.example.android.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
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
import com.example.android.myapplication.ui.adapters.MovieAdapter;
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
    private static final String RECYCLERVIEW_STATE = "state";
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.network_error)
    TextView mStatus;
    Parcelable parcelable;
    GridLayoutManager manager;
    private List<MovieResult> movieResults;
    private static final String TAG = "MainActivity";
    // we will be loading 15 items per page or per load
    // you can change this to fit your specifications.
    // When you change this, there will be no need to update your php page,
    // as php will be ordered what to load and limit by android java
    private static final int LOAD_LIMIT = 20;

    // last id to be loaded from php page,
    // we will need to keep track or database id field to know which id was loaded last
    // and where to begin loading
    private int lastId = 0; // this will issued to php page, so no harm make it string

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mManager = new RestManager();
        mAdapter = new MovieAdapter(new ArrayList<MovieResult>(), this);
         manager= new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }
                }

            }
        });
        mRecyclerView.setHasFixedSize(true);
        ifConnected();
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

                Call<Movie> movieCall = mManager.getMovieClient().getPopularMovies(Constants.API_KEY);
                movieCall.enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        movieResults = response.body().getResults();
                        mAdapter.addMovieResult(movieResults);
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
        } else if (id == R.id.action_top_rated) {
            setTitle(getString(R.string.top_rated));
            //Fetch and sort Movies by Rating
            Call<Movie> movieCall = mManager.getMovieClient().getTopRatedMovies(Constants.API_KEY);
            movieCall.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    movieResults = response.body().getResults();
                    mAdapter.addMovieResult(movieResults);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mStatus.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {

                }
            });
            return true;
        } else if (id == R.id.action_favourite) {
            setTitle(getString(R.string.favourite));
            mStatus.setVisibility(View.INVISIBLE);
            // A list of Favourite Movies
            setMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setManager() {
        itShouldLoadMore = false;
        Call<Movie> movieCall = mManager.getMovieClient().getUpcomingMovies(Constants.API_KEY);
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                itShouldLoadMore = true;
                movieResults = response.body().getResults();
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
            mStatus.setVisibility(View.VISIBLE);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (parcelable!=null){
            manager.onRestoreInstanceState(parcelable);
        }

        //If the title of the ActionBar is Favourite
        //then we setMovie method so we get and updated list of
        //the favourite movies
        CharSequence sequence = getSupportActionBar().getTitle();
        Log.v(TAG, String.valueOf(sequence));
        if (String.valueOf(sequence).equals(Constants.FAVOURITE)) {
            setMovie();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(manager);
        }
    }

    public class MovieLoader extends AsyncTaskLoader<Cursor> {

        public MovieLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            MovieDbHelper mMovieDbHelper = new MovieDbHelper(getContext());
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
            if (cursor.getCount() == 0) {
                mStatus.setText(R.string.no_favourite_movie);
                mStatus.setVisibility(View.VISIBLE);
            }

            return cursor;
        }
    }

    /*This method uses the cursor retrieved from the custom MovieLoad class and
    uses it to create a List of MovieResults and attache it to the adapter
     */
    public void setMovie() {
        MovieLoader movieLoader = new MovieLoader(this);
        Cursor cursor = movieLoader.loadInBackground();

        List<MovieResult> movieResult = new ArrayList<>();
        while (cursor.moveToNext()) {
            MovieResult result = new MovieResult();
            result.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            result.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
            result.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PICTURE)));
            result.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            result.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            result.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movieResult.add(result);
        }
        movieResults.clear();
        mAdapter.clear();
        mAdapter.addMovieResult(movieResult);
        cursor.close();
    }

    private void loadMore() {
        itShouldLoadMore = false;
        Call<Movie> movieCall = mManager.getMovieClient().getUpcomingMovies(Constants.API_KEY);
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                itShouldLoadMore = true;
                movieResults = response.body().getResults();
                mAdapter.addMovieResult(movieResults);
                mProgressBar.setVisibility(View.INVISIBLE);
                mStatus.setVisibility(View.INVISIBLE);
                lastId = movieResults.size();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         parcelable= manager.onSaveInstanceState();

        outState.putParcelable(RECYCLERVIEW_STATE, parcelable);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            parcelable = savedInstanceState.getParcelable(RECYCLERVIEW_STATE);
        }

    }

}
