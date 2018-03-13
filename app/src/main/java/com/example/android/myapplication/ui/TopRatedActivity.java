package com.example.android.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.myapplication.R;
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
 * Created by Ghena on 13/03/2018.
 */

public class TopRatedActivity extends AppCompatActivity implements MovieAdapter.OnItemClicked {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private RestManager mManager;
    private static final String RECYCLERVIEW_STATE = "state";
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.network_error)
    TextView mStatus;
    GridLayoutManager manager;
    private List<MovieResult> movieResults;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.top_rated));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mManager = new RestManager();
        mAdapter = new MovieAdapter(new ArrayList<MovieResult>(), this);
        manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);
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
            Intent intent = new Intent(TopRatedActivity.this,MainPopularActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_favourite) {
            Intent intent = new Intent(TopRatedActivity.this,FavouriteActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //This method checks if there is Network if there is we fetch the data from internet
    // otherwise we inform the user of an Unavailable Network
    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }
    private void getTopRatedMovies(){
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
    }
    private void ifConnected() {
        if (isConnected()) {
            getTopRatedMovies();
        } else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText(R.string.mobile_network_not_available);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClickedMovie(MovieResult movie) {
        Intent intent = new Intent(TopRatedActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Constants.PARCEL, movie);
        startActivity(intent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<MovieResult> movie = mAdapter.getMovies();
        if (movie != null && !movie.isEmpty()) {
            outState.putParcelableArrayList(RECYCLERVIEW_STATE, (ArrayList<? extends Parcelable>) movie);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECYCLERVIEW_STATE)) {
                List<MovieResult> movieResultList = savedInstanceState.getParcelableArrayList(RECYCLERVIEW_STATE);
                mAdapter.addMovieResult(movieResultList);
                mProgressBar.setVisibility(View.GONE);
                mStatus.setVisibility(View.GONE);
            }
        }
    }

}
