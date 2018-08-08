package com.example.android.myapplication.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.network.RestManager;

public class MovieDataSourceFactory extends MovieDataSource.Factory<Integer, MovieResult> {

    private MutableLiveData<MovieDataSource> mDataSourceMutableLiveData = new MutableLiveData<>();
    private  MovieDataSource dataSource;


    public MovieDataSourceFactory() {

    }

    @Override
    public DataSource<Integer, MovieResult> create() {
        dataSource = new MovieDataSource(RestManager.getMovieClient());
        mDataSourceMutableLiveData.postValue(dataSource);
        return dataSource;
    }


}
