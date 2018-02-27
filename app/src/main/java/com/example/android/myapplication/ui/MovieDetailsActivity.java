package com.example.android.myapplication.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.utils.Constants;
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
    @BindView(R.id.overview)
    TextView overview;
    private RatingBar mRatingBar;
    private static final String TAG = "MovieDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        mRatingBar = findViewById(R.id.ratingBar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        populateUi();
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
    private void setRatingBar(double d){
        double half = d/2;
        if(half>0){mRatingBar.setRating(Float.parseFloat(String.valueOf(half)));}

    }

    private void populateUi(){
        MovieResult movieResult = getIntent().getParcelableExtra(Constants.PARCEL);
        title.setText(movieResult.getOriginalTitle());
        Picasso.with(this).load(Constants.IMAGE_BASE_URL + movieResult.getPosterPath())
                .fit().into(imageThumbnail);
        date.setText(movieResult.getReleaseDate());
        overview.setText(movieResult.getOverview());
        setRatingBar(movieResult.getVoteAverage());
    }
}
