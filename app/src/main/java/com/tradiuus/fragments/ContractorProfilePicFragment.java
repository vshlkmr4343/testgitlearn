package com.tradiuus.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.activities.ContractorSignupActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.helper.imagepicker.ImageSelectActivity;


public class ContractorProfilePicFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private ImageView user_pic;
    private DisplayImageOptions options;
    public static ContractorProfilePicFragment fragment;
    String filePath = "";

    public static ContractorProfilePicFragment newInstance(int position, Context context) {
        if (fragment == null)
            fragment = new ContractorProfilePicFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_pic, null);
        this.context = getActivity();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        fragment = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_profile_pic_btn_skip:
                ((ContractorSignupActivity) getActivity()).nextEvent(ContractorSignupActivity.FragTag.CardDetail);
                break;
            case R.id.user_pic:
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 20012);
                break;
            case R.id.frag_profile_pic_btn_proceed:
                if (TextUtils.isEmpty(filePath)) {
                    Utility.uiThreadAlert(context, "Please select an images before hit the proceed button");
                    return;
                }
                ((ContractorSignupActivity) getActivity()).uploadPicNetwork(filePath);
                break;
        }
    }

    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.frag_profile_pic_btn_proceed)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.frag_profile_pic_btn_skip)).setOnClickListener(this);
        user_pic = (ImageView) rootView.findViewById(R.id.user_pic);
        user_pic.setOnClickListener(this);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.add_image)
                .showImageOnFail(R.drawable.add_image)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20012 && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            ImageLoader.getInstance().displayImage("file://" + filePath, user_pic, options);
        }
    }

}