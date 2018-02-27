package com.example.android.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popularity) {
            return true;
        }
        if (id == R.id.action_top_rated) {
            return true;
        }
        if (id == R.id.action_favourite) {
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
        Intent intent =  new Intent(MainActivity.this,MovieDetailsActivity.class);
        intent.putExtra(Constants.PARCEL,movie);
        startActivity(intent);

    }
}
