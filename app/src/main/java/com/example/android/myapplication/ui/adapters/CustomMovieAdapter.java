package com.example.android.myapplication.ui.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieResult;
import com.example.android.myapplication.utils.Constants;
import com.example.android.myapplication.utils.LoadCachedImages;

public class CustomMovieAdapter extends PagedListAdapter<MovieResult, CustomMovieAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClicked mOnItemClicked;

    public CustomMovieAdapter( Context context,OnItemClicked onItemClicked) {
        super(DIFF_CALLBACK);
        mContext = context;
        mOnItemClicked = onItemClicked;
    }

    static final DiffUtil.ItemCallback<MovieResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<MovieResult>() {

        @Override
        public boolean areItemsTheSame(@NonNull MovieResult oldItem, @NonNull MovieResult newItem) {
            return oldItem.title == newItem.title;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MovieResult oldItem, @NonNull MovieResult newItem) {
            return oldItem.equals(newItem);
        }
    };



    @NonNull
    @Override
    public CustomMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomMovieAdapter.ViewHolder holder, int position) {
        MovieResult currentMovie = getItem(position);
        String imagePath = Constants.IMAGE_BASE_URL + currentMovie.getPosterPath();
        //Using this method to load cached images from memory to keep a smooth scroll
        LoadCachedImages.loadImageFromMemory(holder.itemView.getContext(), imagePath, holder.mImage);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.thumbnails);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieResult movieResult = getItem(position);
            mOnItemClicked.onClickedMovie(movieResult);

        }
    }
    public interface OnItemClicked {
        void onClickedMovie(MovieResult movie);
    }
}
