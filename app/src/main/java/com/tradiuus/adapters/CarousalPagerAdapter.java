package com.tradiuus.adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tradiuus.R;
import com.tradiuus.models.Image;

import java.util.ArrayList;
import java.util.Random;

public class CarousalPagerAdapter extends PagerAdapter {

    private Context context;
    private String image_url;
    private ArrayList<Image> carousalItems;
    private LayoutInflater layoutInflater;
    private DisplayImageOptions options;

    public CarousalPagerAdapter(Context mContext, ArrayList<Image> carousalItems, String image_url) {
        this.context = mContext;
        this.image_url = image_url;
        this.carousalItems = carousalItems;
        this.layoutInflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pager_loading_bg)
                .showImageOnFail(R.drawable.pager_loading_bg)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    @Override
    public int getCount() {
        return (carousalItems == null || carousalItems.size() == 0) ? 0 : carousalItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup view, int position, @NonNull Object object) {
        view.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.inflate_pager_item, collection, false);
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        final ProgressBar pg_loading = (ProgressBar) layout.findViewById(R.id.pg_loading);
        ImageLoader.getInstance().displayImage(image_url + carousalItems.get(position).getImage(), image, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                pg_loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                pg_loading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                pg_loading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                pg_loading.setVisibility(View.GONE);
            }
        });
        collection.addView(layout);
        return layout;
    }
}