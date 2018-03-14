package com.example.android.myapplication.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
    FloatingActionButton mButton;
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
    private final static String TRAILER_STATE = "trailer_state";
    private final static String REVIEW_STATE = "review_state";
    RecyclerView.LayoutManager reviewManager, trailerManager;

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

        mAdapter = new TrailerAdapter(new ArrayList<MovieVideoResult>(), this);
        setMovieVideoResultClient();
        trailerManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(trailerManager);
        mRecyclerView.setAdapter(mAdapter);

        mReviewAdapter = new ReviewAdapter(new ArrayList<MovieReviewResult>());
        setMovieReview();
        reviewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReviews.setLayoutManager(reviewManager);
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
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idOfTheMovie = movieResult.getId();

                if (isFavouriteMovie(idOfTheMovie)) {
                    mButton.setImageResource(R.drawable.star_white);
                    deleteFromFavourite(idOfTheMovie);
                } else if (!isFavouriteMovie(movieResult.getId())) {
                    mButton.setImageResource(R.drawable.star_yellow);
                    addToFavourite();
                }
            }
        });
    }

    //Add Movies to the local database
    private void addToFavourite() {
        ContentValues values = InsertMovieDetails.insertMovieValues(movieResult);
        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

    }

    //Delete Movie  from the local database
    private void deleteFromFavourite(int id) {
        getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_ID + "=" + id,
                null);
    }

    //Method to check if the Movie to be added to Favourites is in Favourite database
    public boolean isFavouriteMovie(int id) {

        ContentResolver resolver = getContentResolver();
        String[] projection = {MovieContract.MovieEntry.COLUMN_ID};
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                MovieContract.MovieEntry.COLUMN_ID + "=?",
                selectionArgs,
                null,
                null);
        if (cursor.moveToNext() && cursor.getCount() > 0) {
            cursor.close();
            mButton.setImageResource(R.drawable.star_yellow);
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
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<MovieVideoResult> movie = mAdapter.getVideoResults();
        List<MovieReviewResult> reviewResults = mReviewAdapter.getReviewResults();
        if (movie != null && !movie.isEmpty()) {
            outState.putParcelableArrayList(TRAILER_STATE, (ArrayList<? extends Parcelable>) movie);
        }
        if (reviewResults != null && !reviewResults.isEmpty()) {
            outState.putParcelableArrayList(REVIEW_STATE, (ArrayList<? extends Parcelable>) reviewResults);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRAILER_STATE)) {
                List<MovieVideoResult> movieResultList = savedInstanceState.getParcelableArrayList(TRAILER_STATE);
                mAdapter.addTrailers(movieResultList);

            } else if (savedInstanceState.containsKey(REVIEW_STATE)) {
                List<MovieReviewResult> review = savedInstanceState.getParcelableArrayList(REVIEW_STATE);
                mReviewAdapter.addMovieReviews(review);
            }
        }
    }

}
