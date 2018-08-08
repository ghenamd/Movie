package com.example.android.myapplication.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
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
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.ui.adapters.CustomMovieAdapter;
import com.example.android.myapplication.utils.Constants;
import com.example.android.myapplication.viewmodel.MovieViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ghena on 24/02/2018.
 */

public class MainPopularActivity extends AppCompatActivity implements CustomMovieAdapter.OnItemClicked {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    public static boolean popular = true;
    private CustomMovieAdapter mMovieAdapter;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.network_error)
    TextView mStatus;
    GridLayoutManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.popular));
        ButterKnife.bind(this);
        mMovieAdapter = new CustomMovieAdapter(this,this);
        manager = new GridLayoutManager(this, 2);
        ifConnected();

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
        if (id == R.id.action_top_rated) {
            Intent intent = new Intent(MainPopularActivity.this, TopRatedActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_favourite) {
            mStatus.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(MainPopularActivity.this, FavouriteActivity.class);
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

    //If the user is connected to the Internet we set the RestManger
    private void ifConnected() {
        if (isConnected()) {
            setupViewModel();
        } else {
            mStatus.setVisibility(View.VISIBLE);
            mStatus.setText(R.string.mobile_network_not_available);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void setupViewModel(){
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
       movieViewModel.getMovieResultLiveData().observe(this, new Observer<PagedList<MovieResult>>() {
           @Override
           public void onChanged(@Nullable PagedList<MovieResult> movieResults) {
               mRecyclerView.setLayoutManager(manager);
               mRecyclerView.setHasFixedSize(true);
               mMovieAdapter.submitList(movieResults);
               mRecyclerView.setAdapter(mMovieAdapter);
               mProgressBar.setVisibility(View.INVISIBLE);
           }
       });
    }

    @Override
    public void onClickedMovie(MovieResult movie) {
        Intent intent = new Intent(MainPopularActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Constants.PARCEL, movie);
        startActivity(intent);
    }

}
