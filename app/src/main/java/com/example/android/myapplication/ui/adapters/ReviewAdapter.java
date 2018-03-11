package com.example.android.myapplication.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.MovieReviewResult;

import java.util.List;

/**
 * Created by Ghena on 07/03/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private List<MovieReviewResult> mReviewResults;

    public ReviewAdapter(List<MovieReviewResult> reviewResults) {
        mReviewResults = reviewResults;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        MovieReviewResult result = mReviewResults.get(position);
        String review = result.getContent();
        String author = result.getAuthor();
        holder.review.setText(review);
        holder.author.setText(": " + author);
    }

    @Override
    public int getItemCount() {
        return mReviewResults.size();
    }
    public void addMovieReviews(List<MovieReviewResult> movieReviewResults){
        mReviewResults = movieReviewResults;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView review;
        TextView author;
        public ViewHolder(View itemView) {
            super(itemView);
            review = itemView.findViewById(R.id.review);
            author = itemView.findViewById(R.id.author);
        }
    }
    public List<MovieReviewResult> getReviewResults(){
        return mReviewResults;
    }
}
