package com.tradiuus.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.helper.imagepicker.ImageSelectActivity;
import com.tradiuus.models.Image;
import com.tradiuus.models.ImageImpl;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.widgets.CustomDrawableEditText;
import com.tradiuus.widgets.CustomEditText;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import java.util.List;



public class TechnicianSettingActivity extends BaseActivity implements TechnicianImpl.OnSettingsUpdateListener, ImageImpl.OnImageUploadChangedListener {
    private Context context;
    private CustomProgressDialog pgDialog;
    public TechnicianImpl technicianImpl;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    private CustomEditText setting_edt_old_pwd;
    private CustomDrawableEditText setting_edt_new_pwd;
    private CustomEditText setting_edt_conf_pwd;
    private ImageView image_profile_pic;
    private DisplayImageOptions options;

    private String filePath;
    private ImageImpl mImageImpl;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_technician_setting);
        this.context = TechnicianSettingActivity.this;
        technicianImpl = new TechnicianImpl();
        technicianImpl.init(context, this);

        mImageImpl = new ImageImpl();
        mImageImpl.init(context, this);

        permission();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);

        setting_edt_old_pwd = (CustomEditText) findViewById(R.id.setting_edt_old_pwd);
        setting_edt_new_pwd = (CustomDrawableEditText) findViewById(R.id.setting_edt_new_pwd);
        setting_edt_conf_pwd = (CustomEditText) findViewById(R.id.setting_edt_conf_pwd);

        image_profile_pic = (ImageView) findViewById(R.id.image_profile_pic);
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.setting_btn_submit)).setOnClickListener(this);
        ((TextView) findViewById(R.id.edit_btn)).setOnClickListener(this);
        contractor_dashboard_back.setOnClickListener(this);
        setting_edt_new_pwd.setDrawableClickListener(new CustomDrawableEditText.DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        Utility.uiThreadAlert(context, "Password", "Password must have a minimum of 8 characters, can only contain alphanumeric character and at least one uppercase");
                        break;
                    default:
                        break;
                }
            }
        });
        try {
            ((CustomTextView) findViewById(R.id.btn_HELP)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        openHelpDialog(context, technicianImpl.mTechnician.getFaqs(), technicianImpl.mTechnician.getVideos());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.setting_btn_submit:
                submitEvent();
                break;
            case R.id.edit_btn:
                uploadImage(1201);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            switch (requestCode) {
                case 1201:
                    ImageLoader.getInstance().displayImage("file://" + filePath, image_profile_pic, options);

                    uploadImageNetwork();
                    break;
            }
        }

    }

    @Override
    protected void initLoadCall() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.technician_avt)
                .showImageOnFail(R.drawable.technician_avt)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        try {
            ImageLoader.getInstance().displayImage(technicianImpl.mTechnician.getProfile_image(), image_profile_pic, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPasswordUpdateSuccess(String msg) {

    }

    @Override
    public void onShowPgDialog() {
        pgDialog = new CustomProgressDialog(context);
        pgDialog.prepareAndShowDialog();
    }

    @Override
    public void onHidePgDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pgDialog.dismissDialog();
            }
        });
    }

    @Override
    public void showAlert(String message) {
        Utility.uiThreadAlert(context, message);
    }

    @Override
    public void onImageUploadSuccess(Image image, String message) {

    }

    @Override
    public void onUserPicUploadSuccess(String imageUrl, String message) {
        try {
            technicianImpl.mTechnician.setProfile_image(imageUrl);
            UserPreference.saveTechnician(context, technicianImpl.mTechnician);
            ImageLoader.getInstance().displayImage(technicianImpl.mTechnician.getProfile_image(), image_profile_pic, options);
            showAlert(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void permission() {
        Dexter.withActivity(TechnicianSettingActivity.this)
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
        Intent intent = new Intent(TechnicianSettingActivity.this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
        startActivityForResult(intent, codeFor);
    }

    private void submitEvent() {
        String edtOldPwd = setting_edt_old_pwd.getText().toString();
        String edtNewPwd = setting_edt_new_pwd.getText().toString();
        String edtConfPwd = setting_edt_conf_pwd.getText().toString();

        if (TextUtils.isEmpty(edtOldPwd) || TextUtils.isEmpty(edtNewPwd) || TextUtils.isEmpty(edtConfPwd)) {
            Utility.uiThreadAlert(context, "Please check all the fields before submit");
            return;
        }

        if (!edtNewPwd.equalsIgnoreCase(edtConfPwd)) {
            Utility.uiThreadAlert(context, "Password miss matched");
            return;
        }
        if (!Utility.isValidPassword(edtNewPwd)) {
            Utility.uiThreadAlert(context, "Password must have a minimum of 8 characters, can only contain alphanumeric character and at least one uppercase");
            return;
        }
        technicianImpl.updatePassword(edtOldPwd, edtNewPwd);
    }

    public void uploadImageNetwork() {
        mImageImpl.compressAndUploadTechnicianImage(technicianImpl.mTechnician.getTechnician_id(), filePath);
        /*mImageImpl.uploadUserPic(technicianImpl.mTechnician.getTechnician_id(), filePath);*/
    }
}