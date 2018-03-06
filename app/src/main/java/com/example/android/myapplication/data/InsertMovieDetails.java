package com.example.android.myapplication.data;

import android.content.ContentValues;

import com.example.android.myapplication.model.MovieResult;

/**
 * Created by Ghena on 05/03/2018.
 */

public class InsertMovieDetails {
    public static ContentValues insertMovieValues(MovieResult movieResult){
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieResult.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_ID, movieResult.getId());
        values.put(MovieContract.MovieEntry.COLUMN_PICTURE, movieResult.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieResult.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieResult.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieResult.getReleaseDate());
        return values;

    }
}
