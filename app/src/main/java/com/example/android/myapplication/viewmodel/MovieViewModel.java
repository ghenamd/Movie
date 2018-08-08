package com.example.android.myapplication.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.example.android.myapplication.datasource.MovieDataSourceFactory;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.network.LoadState;
import com.example.android.myapplication.utils.AppExecutor;


public class MovieViewModel extends AndroidViewModel {

    private static final int PAGE_SIZE = 2;

    private LoadState networkState;

    public MovieViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<PagedList<MovieResult>> getMovieResultLiveData() {

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(true)
                .setPageSize(PAGE_SIZE)
                .build();

        MovieDataSourceFactory dataSourceFactory = new MovieDataSourceFactory();
        LiveData<PagedList<MovieResult>> mPagedListLiveData = new LivePagedListBuilder<>(dataSourceFactory, config)
                .setFetchExecutor(AppExecutor.networkIO())
                .build();

        return mPagedListLiveData;
    }

}
