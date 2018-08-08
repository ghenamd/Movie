package com.example.android.myapplication.datasource.topmovie;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.example.android.myapplication.datasource.popularmovie.MovieDataSource;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.network.RestManager;

public class MovieTopDataSourceFactory extends MovieDataSource.Factory<Integer, MovieResult> {

    private MutableLiveData<MovieTopDataSource> mDataSourceMutableLiveData = new MutableLiveData<>();

    private  MovieTopDataSource dataSource;

    @Override
    public DataSource<Integer, MovieResult> create() {
        dataSource = new MovieTopDataSource(RestManager.getMovieClient());
        mDataSourceMutableLiveData.postValue(dataSource);
        return dataSource;
    }
}
