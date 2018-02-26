package com.example.android.myapplication.network;

import com.example.android.myapplication.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ghena on 24/02/2018.
 */

public class RestManager {

    private MovieClient mClient;

    public MovieClient getMovieClient(){
        if(mClient == null){

            Retrofit retrofit= new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mClient = retrofit.create(MovieClient.class);
        }
        return mClient;

    }
}
