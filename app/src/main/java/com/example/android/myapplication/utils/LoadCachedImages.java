package com.example.android.myapplication.utils;

import android.content.Context;
import android.widget.ImageView;

import com.example.android.myapplication.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Ghena on 27/02/2018.
 */

public class LoadCachedImages {
    //Picasso caches  images in th phone's memory and allowes a smooth scrolling
    public static void loadImageFromMemory(final Context context, final String imagePath, final ImageView image) {
        Picasso
                .with(context)
                .load(imagePath)
                .fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso
                                .with(context)
                                .load(imagePath)
                                .placeholder(R.drawable.no_image_icon)
                                .into(image);
                    }

                    @Override
                    public void onError() {

                    }
                });


    }
}
