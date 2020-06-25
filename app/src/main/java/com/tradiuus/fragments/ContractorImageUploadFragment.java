package com.tradiuus.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.activities.ContractorSignupActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.helper.imagepicker.ImageSelectActivity;

import java.util.List;


public class ContractorImageUploadFragment extends Fragment implements View.OnClickListener {

    private Context context;
    public static ContractorImageUploadFragment fragment;
    private ImageView general_insurance;
    private ImageView general_license;
    private ImageView general_company;
    private String insurance_path;
    private String license_path;
    private String company_path;

    private DisplayImageOptions options;

    public static ContractorImageUploadFragment newInstance(int position, Context context) {
        if (fragment == null)
            fragment = new ContractorImageUploadFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_upload, null);
        this.context = getActivity();
        initView(rootView);
        permission();
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
            case R.id.frag_imageupload_btn_proceed:
                if (TextUtils.isEmpty(insurance_path) || TextUtils.isEmpty(license_path)) {
                    Utility.uiThreadAlert(context, "Please provide insurance image and license image");
                    return;
                }
                ((ContractorSignupActivity) getActivity()).uploadImageNetwork(insurance_path, license_path, company_path);
                break;
            case R.id.frag_imageupload_btn_back:
                ((ContractorSignupActivity) getActivity()).backEvent();
                break;
            case R.id.image_general_insurance:
                uploadImage(1201);
                break;
            case R.id.image_general_license:
                uploadImage(1202);
                break;
            case R.id.image_general_company:
                uploadImage(1203);
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
                    insurance_path = filePath;
                    ImageLoader.getInstance().displayImage("file://" + filePath, general_insurance, options);
                    break;
                case 1202:
                    license_path = filePath;
                    ImageLoader.getInstance().displayImage("file://" + filePath, general_license, options);
                    break;
                case 1203:
                    company_path = filePath;
                    ImageLoader.getInstance().displayImage("file://" + filePath, general_company, options);
                    break;
            }
        }

    }

    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.frag_imageupload_btn_proceed)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.frag_imageupload_btn_back)).setOnClickListener(this);

        general_insurance = (ImageView) rootView.findViewById(R.id.image_general_insurance);
        general_license = (ImageView) rootView.findViewById(R.id.image_general_license);
        general_company = (ImageView) rootView.findViewById(R.id.image_general_company);

        general_insurance.setOnClickListener(this);
        general_license.setOnClickListener(this);
        general_company.setOnClickListener(this);

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

    private void permission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Log.i("PERMISSION", report.toString());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Log.i("PERMISSION", token.toString());
                    }
                })
                .check();
    }

    private void uploadImage(int codeFor) {
        Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
        startActivityForResult(intent, codeFor);
    }
}