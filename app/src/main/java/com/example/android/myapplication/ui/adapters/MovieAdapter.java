package com.example.android.myapplication.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.utils.Constants;
import com.example.android.myapplication.utils.LoadCachedImages;

import java.util.List;

/**
 * Created by Ghena on 24/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ThumbnailsViewHolder> {
    private List<MovieResult> mResultList;
    private OnItemClicked mClicked;

    public MovieAdapter(List<MovieResult> resultList, OnItemClicked movieClicked) {
        mResultList = resultList;
        mClicked = movieClicked;
    }

    @Override
    public ThumbnailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
        return new ThumbnailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ThumbnailsViewHolder holder, int position) {
        MovieResult currentMovie = mResultList.get(position);
        String imagePath = Constants.IMAGE_BASE_URL + currentMovie.getPosterPath();
        //Using this method to load cached images from memory to keep a smooth scroll
        LoadCachedImages.loadImageFromMemory(holder.itemView.getContext(), imagePath, holder.mImage);

    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public void addMovieResult(List<MovieResult> list) {
        mResultList = list;
        notifyDataSetChanged();
    }

    public void clear() {
        mResultList.clear();
        this.notifyDataSetChanged();
    }

    public interface OnItemClicked {
        void onClickedMovie(MovieResult movie);
    }

    class ThumbnailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImage;

        public ThumbnailsViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.thumbnails);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieResult movieResult = mResultList.get(position);
            mClicked.onClickedMovie(movieResult);
        }
    }
    public List<MovieResult> getMovies(){
        return mResultList;

    }

}
