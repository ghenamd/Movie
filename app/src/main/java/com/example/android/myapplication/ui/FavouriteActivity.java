package com.example.android.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.data.MovieContract;
import com.example.android.myapplication.data.MovieDbHelper;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.ui.adapters.MovieAdapter;
import com.example.android.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ghena on 13/03/2018.
 */

public class FavouriteActivity extends AppCompatActivity implements MovieAdapter.OnItemClicked {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.network_error)
    TextView mStatus;
    GridLayoutManager manager;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.favourite));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAdapter = new MovieAdapter(new ArrayList<MovieResult>(), this);
        manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        setMovie();
        mRecyclerView.setAdapter(mAdapter);
        mProgressBar.setVisibility(View.INVISIBLE);
        mStatus.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMovie();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Fetch and Sort Movies by Popularity
        if (id == R.id.action_popularity) {
            Intent intent = new Intent(FavouriteActivity.this,MainPopularActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_top_rated) {
            Intent intent = new Intent(FavouriteActivity.this,TopRatedActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        mAdapter.clear();
        mAdapter.addMovieResult(movieResult);
        cursor.close();
    }
    @Override
    public void onClickedMovie(MovieResult movie) {
        Intent intent = new Intent(FavouriteActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Constants.PARCEL, movie);
        startActivity(intent);
    }

}
