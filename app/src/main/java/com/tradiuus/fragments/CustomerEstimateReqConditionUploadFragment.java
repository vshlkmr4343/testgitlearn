package com.tradiuus.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tradiuus.R;
import com.tradiuus.activities.CustomerEstimateCaseActivity;
import com.tradiuus.helper.imagepicker.ImageSelectActivity;
import com.tradiuus.models.Trade;
import com.tradiuus.widgets.CustomTextView;


public class CustomerEstimateReqConditionUploadFragment extends Fragment implements View.OnClickListener {
    private Context context;

    private ImageView req_type;
    private TextView heading;
    private TextView title;
    private TextView charges_for_emergency;
    private ImageView image_1;
    private ImageView image_2;
    private String image_1_path;
    private String image_2_path;

    private DisplayImageOptions options;
    public Trade selectedTrade;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_emergency_req_condition_upload, null);
        this.context = getActivity();
        selectedTrade = ((CustomerEstimateCaseActivity) getActivity()).selectedTrade;
        initView(rootView);
        initViewLoad();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_1:
                PickImage(1201);
                break;
            case R.id.image_2:
                PickImage(1202);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            switch (requestCode) {
                case 1201:
                    ((CustomerEstimateCaseActivity) getActivity()).image_1_path = image_1_path = filePath;
                    ImageLoader.getInstance().displayImage("file://" + filePath, image_1, options);
                    break;
                case 1202:
                    ((CustomerEstimateCaseActivity) getActivity()).image_2_path = image_2_path = filePath;
                    ImageLoader.getInstance().displayImage("file://" + filePath, image_2, options);
                    break;
            }
        }

    }

    private void initView(View rootView) {
        req_type = (ImageView) rootView.findViewById(R.id.req_type);
        title = (TextView) rootView.findViewById(R.id.title);
        charges_for_emergency = (TextView) rootView.findViewById(R.id.charges_for_emergency);
        heading = (TextView) rootView.findViewById(R.id.heading);
        image_1 = (ImageView) rootView.findViewById(R.id.image_1);
        image_2 = (ImageView) rootView.findViewById(R.id.image_2);
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        image_1.setOnClickListener(this);
        image_2.setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.cancelRequest)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CustomerEstimateCaseActivity) getActivity()).cancelRequest();
            }
        });
    }

    public void initViewLoad() {
        try {
            charges_for_emergency.setText("Contractor visits and estimates are free of charge not mandatory.");
            heading.setText("Do you want to send pics of your estimate condition? [optional]");
            title.setText("Need A " + selectedTrade.getTrade_name() + " for an Estimate");
            ImageLoader.getInstance().displayImage(selectedTrade.getFeatured_normal_image(), req_type, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    try {
                        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) view).getDrawable()), Color.parseColor("#5fd8e3"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PickImage(int code) {
        Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
        startActivityForResult(intent, code);
    }
}
