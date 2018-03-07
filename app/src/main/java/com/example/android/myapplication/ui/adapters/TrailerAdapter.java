package com.example.android.myapplication.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieVideoResult;
import com.example.android.myapplication.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ghena on 28/02/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<MovieVideoResult> mVideoResults;
    private OnTrailerClick mOnTrailerClick;

    public TrailerAdapter(List<MovieVideoResult> videoResults, OnTrailerClick trailerClicked) {
        mVideoResults = videoResults;
        mOnTrailerClick = trailerClicked;
    }


    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.trailer_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, int position) {
        MovieVideoResult movieVideoResult = mVideoResults.get(position);
        String key = movieVideoResult.getKey();
        String thumbnailUrl = Constants.YOUTUBE_VIDEO_THUMBNAIL + key + Constants.YOUTUBE_THUMBNAIL_MEDIUM_QUALITY;
        Picasso.with(holder.itemView.getContext())
                .load(thumbnailUrl)
                .placeholder(R.color.colorWhite)
                .resize(300, 250)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mVideoResults.size();
    }

    public void addTrailers(List<MovieVideoResult> results) {
        mVideoResults = results;
        notifyDataSetChanged();
    }
    public interface OnTrailerClick{
        void onClick(MovieVideoResult videoResult);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.trailer_thumbnail);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieVideoResult movieVideoResult = mVideoResults.get(position);
            mOnTrailerClick.onClick(movieVideoResult);

        }
    }
}
