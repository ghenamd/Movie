package com.example.android.myapplication.ui;

import android.os.Bundle;
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
import android.widget.ToggleButton;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.model.MovieVideo;
import com.example.android.myapplication.model.MovieVideoResult;
import com.example.android.myapplication.network.RestManager;
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

public class MovieDetailsActivity extends AppCompatActivity {
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
    private boolean isClicked = false;
    @BindView(R.id.recycler_trailers)
    RecyclerView mRecyclerView;
    ExpandableTextView mExpandableTextView;
    private TrailerAdapter mAdapter;
    private RestManager mManager;
    private MovieResult movieResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mRatingBar = findViewById(R.id.ratingBar);
        mExpandableTextView = findViewById(R.id.expand_text_view);
        movieResult = getIntent().getParcelableExtra(Constants.PARCEL);
        Log.v(TAG, String.valueOf(movieResult.getId()));
        setSupportActionBar(toolbar);
        setFavouriteButton();
        populateUi();
        mAdapter = new TrailerAdapter(new ArrayList<MovieVideoResult>());
        setMovieVideoResultClient();

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
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

    private void populateUi() {

        title.setText(movieResult.getOriginalTitle());
        Picasso.with(this).load(Constants.IMAGE_BASE_URL + movieResult.getPosterPath())
                .fit().into(imageThumbnail);
        date.setText(movieResult.getReleaseDate());
        mExpandableTextView.setText(movieResult.getOverview());
        setRatingBar(movieResult.getVoteAverage());
    }

    private void setFavouriteButton() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButton.isChecked()) {
                    mButton.setBackgroundResource(R.drawable.star_yellow);
                } else {
                    mButton.setBackgroundResource(R.drawable.star_white);
                }
            }
        });
    }
}
