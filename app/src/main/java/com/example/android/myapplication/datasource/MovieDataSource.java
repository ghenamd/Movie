package com.example.android.myapplication.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.example.android.myapplication.model.Movie;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.network.LoadState;
import com.example.android.myapplication.network.MovieClient;
import com.example.android.myapplication.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDataSource extends PageKeyedDataSource<Integer, MovieResult> {

    private MovieClient mMovieClient;

    public final MutableLiveData loadState;

    public MovieDataSource(MovieClient movieClient) {
        mMovieClient = movieClient;
        loadState = new MutableLiveData<LoadState>();
    }


    public MutableLiveData getLoadState() {
        return loadState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, MovieResult> callback) {
        Call<Movie> movieCall = mMovieClient.getPopularByPage(Constants.API_KEY, 1);
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response != null) {
                    callback.onResult(response.body().getResults(),1,2);
                    loadState.postValue(LoadState.LOADED);
                }else {
                    loadState.postValue(LoadState.FAILED);
                }

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                loadState.postValue(LoadState.FAILED);
            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, MovieResult> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, MovieResult> callback) {

        Call<Movie> movieCall = mMovieClient.getPopularByPage(Constants.API_KEY, params.key);
        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response != null) {
                    callback.onResult(response.body().getResults(),params.key + 1);
                    loadState.postValue(LoadState.LOADED);
                }else {
                    loadState.postValue(LoadState.FAILED);
                }

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                loadState.postValue(LoadState.FAILED);
            }
        });
    }
}
