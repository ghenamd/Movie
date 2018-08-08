package com.example.android.myapplication.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.android.myapplication.datasource.topmovie.MovieTopDataSourceFactory;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.utils.AppExecutor;




public class MovieTopViewModel extends AndroidViewModel {

    private final int PAGE_SIZE = 2;
    public MovieTopViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<PagedList<MovieResult>> getMovieResultLiveData() {

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .setPageSize(PAGE_SIZE)
                .build();

        MovieTopDataSourceFactory dataSourceFactory = new MovieTopDataSourceFactory();

        return new LivePagedListBuilder<>(dataSourceFactory, config)
                .setFetchExecutor(AppExecutor.networkIO())
                .build();
    }
}
