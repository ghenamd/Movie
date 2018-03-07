package com.example.android.myapplication.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.myapplication.R;
import com.example.android.myapplication.data.InsertMovieDetails;
import com.example.android.myapplication.data.MovieContract;
import com.example.android.myapplication.data.MovieDbHelper;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.model.MovieReview;
import com.example.android.myapplication.model.MovieReviewResult;
import com.example.android.myapplication.model.MovieVideo;
import com.example.android.myapplication.model.MovieVideoResult;
import com.example.android.myapplication.network.RestManager;
import com.example.android.myapplication.ui.adapters.ReviewAdapter;
import com.example.android.myapplication.ui.adapters.TrailerAdapter;
import com.example.android.myapplication.utils.Constants;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.OnTrailerClick {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.image_thumbnail)
    ImageView imageThumbnail;
    @BindView(R.id.releaseDate)
    TextView date;
    @BindView(R.id.image_button)
    ToggleButton mButton;
    private RatingBar mRatingBar;
    private static final String TAG = "MovieDetailsActivity";
    @BindView(R.id.recycler_trailers)
    RecyclerView mRecyclerView;
    @BindView(R.id.recycler_review)
    RecyclerView mRecyclerViewReviews;
    ExpandableTextView mExpandableTextView;
    private TrailerAdapter mAdapter;
    private RestManager mManager;
    private MovieResult movieResult;
    private MovieDbHelper mMovieDbHelper;
    private ReviewAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mRatingBar = findViewById(R.id.ratingBar);
        mExpandableTextView = findViewById(R.id.expand_text_view);
        movieResult = getIntent().getParcelableExtra(Constants.PARCEL);
        mMovieDbHelper = new MovieDbHelper(this);
        isFavouriteMovie(movieResult.getId());
        setSupportActionBar(toolbar);
        setFavouriteButton();
        populateUi();

        mAdapter = new TrailerAdapter(new ArrayList<MovieVideoResult>(),this);
        setMovieVideoResultClient();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mReviewAdapter = new ReviewAdapter(new ArrayList<MovieReviewResult>());
        setMovieReview();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReviews.setLayoutManager(layoutManager);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    //Method to set the average rating for a specific Movie
    private void setRatingBar(double d) {
        double half = d / 2;
        if (half > 0) {
            mRatingBar.setRating(Float.parseFloat(String.valueOf(half)));
        }
    }

    //Retrofit method to get fetch the Trailers for the movies
    private void setMovieVideoResultClient() {
        mManager = new RestManager();
        Call<MovieVideo> movieVideoCall = mManager.getMovieClient()
                .getMovieTrailer(movieResult.getId(), Constants.API_KEY);

        movieVideoCall.enqueue(new Callback<MovieVideo>() {
            @Override
            public void onResponse(Call<MovieVideo> call, Response<MovieVideo> response) {
                if (response.isSuccessful()) {
                    List<MovieVideoResult> videoResult = response.body().getResults();
                    mAdapter.addTrailers(videoResult);
                } else {
                    int sc = response.code();
                    Log.v(TAG, String.valueOf(sc));
                }
            }

            @Override
            public void onFailure(Call<MovieVideo> call, Throwable t) {

            }
        });
    }

    private void setMovieReview() {
        mManager = new RestManager();
        Call<MovieReview> movieReviewCall = mManager.getMovieClient().
                getMovieReview(movieResult.getId(), Constants.API_KEY);
        movieReviewCall.enqueue(new Callback<MovieReview>() {
            @Override
            public void onResponse(Call<MovieReview> call, Response<MovieReview> response) {
                List<MovieReviewResult> movieReviewResults = response.body().getResults();
                mReviewAdapter.addMovieReviews(movieReviewResults);
                Log.v(TAG, movieReviewResults.toString());
//
            }

            @Override
            public void onFailure(Call<MovieReview> call, Throwable t) {

            }
        });

    }

    private void populateUi() {

        title.setText(movieResult.getOriginalTitle());
        Picasso.with(this).load(Constants.IMAGE_BASE_URL + movieResult.getPosterPath())
                .fit().into(imageThumbnail);
        date.setText(movieResult.getReleaseDate());
        mExpandableTextView.setText(movieResult.getOverview());
        setRatingBar(movieResult.getVoteAverage());
    }

    // Set the ClickListener on the ToggleButton
    // responsible for adding or deleting a movie from the Favourites list
    private void setFavouriteButton() {
        mButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int idOfTheMovie = movieResult.getId();

                if (isFavouriteMovie(idOfTheMovie)) {
                    mButton.setBackgroundResource(R.drawable.star_white);
                    deleteFromFavourite(idOfTheMovie);
                } else if (!isFavouriteMovie(movieResult.getId())) {
                    mButton.setBackgroundResource(R.drawable.star_yellow);
                    addToFavourite();
                }
            }
        });
    }

    //Add Movies to the local database
    private void addToFavourite() {
        ContentValues values = InsertMovieDetails.insertMovieValues(movieResult);
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

    }

    //Delete Movie  from the local database
    private void deleteFromFavourite(int id) {
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        db.delete(MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry.COLUMN_ID + "=" + id,
                null);
    }

    //Method to check if the Movie to be added to Favourites is in Favourite database
    public boolean isFavouriteMovie(int id) {

        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        String[] projection = {MovieContract.MovieEntry.COLUMN_ID};
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                MovieContract.MovieEntry.COLUMN_ID + "=?",
                selectionArgs,
                null,
                null,
                null);
        if (cursor.moveToNext() && cursor.getCount() > 0) {
            cursor.close();
            mButton.setBackgroundResource(R.drawable.star_yellow);
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    @Override
    public void onClick(MovieVideoResult videoResult) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.YOUTUBE_URL + videoResult.getKey()));
        PackageManager packageManager = getPackageManager();
        if (intent.resolveActivity(packageManager) !=null){
            startActivity(intent);
        }
        else {
            Toast.makeText(this,"No apps available to play video",Toast.LENGTH_SHORT);
        }

    }
}
