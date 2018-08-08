package com.example.android.myapplication.network;

import com.example.android.myapplication.model.Movie;
import com.example.android.myapplication.model.MovieReview;
import com.example.android.myapplication.model.MovieVideo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ghena on 24/02/2018.
 */

public interface MovieClient {


    @GET("popular?")
    Call<Movie> getPopularByPage(@Query("api_key") String api, @Query("page") int pageNumber);

    @GET("top_rated?")
    Call<Movie> getTopRatedMovies(@Query("api_key")String api, @Query("page") int pageNumber);

       @GET("{movie_id}/videos?")
    Call<MovieVideo> getMovieTrailer(@Path("movie_id") int in, @Query("api_key")String s);

    @GET("{movie_id}/reviews?")
    Call<MovieReview> getMovieReview(@Path("movie_id")int in,@Query("api_key")String s);
}
