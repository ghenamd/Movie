package com.example.android.myapplication.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.utils.Constants;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mRatingBar = findViewById(R.id.ratingBar);
        mExpandableTextView = findViewById(R.id.expand_text_view);
        setSupportActionBar(toolbar);
        setFavouriteButton();
        populateUi();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(manager);
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

    private void populateUi() {
        MovieResult movieResult = getIntent().getParcelableExtra(Constants.PARCEL);
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
                    mButton.setBackgroundResource(R.drawable.favourite_red);
                } else {
                    mButton.setBackgroundResource(R.drawable.favourite_white);
                }

            }
        });


    }
}
