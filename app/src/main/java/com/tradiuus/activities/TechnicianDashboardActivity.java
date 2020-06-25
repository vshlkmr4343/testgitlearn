package com.tradiuus.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

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
import com.tradiuus.TradiuusApp;
import com.tradiuus.helper.LocationHandler;
import com.tradiuus.helper.StackActivities;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.MessageEvent;
import com.tradiuus.models.Technician;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.network.GPSTracker;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class TechnicianDashboardActivity extends BaseActivity implements TechnicianImpl.OnTechnicianDataChangedListener {
    private final static String TAG = TechnicianDashboardActivity.class.getSimpleName();
    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView userName, companyName;
    public TechnicianImpl technicianImpl;
    public ImageView image_profile_pic;
    private DisplayImageOptions options;
    private Technician mTechnician;
    private SwitchCompat work_status;
    private boolean isActive = false;
    private boolean isNotificationReceived = false;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_technician_dashboard);
        EventBus.getDefault().register(this);
        StackActivities.registerActivity(this);
        this.context = TechnicianDashboardActivity.this;

        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
        technicianImpl = new TechnicianImpl();
        technicianImpl.init(context, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeDataLoad();
        try {
            if (isNotificationReceived && technicianImpl != null) {
                technicianImpl.getServicesData();
            }
            isActive = true;
            isNotificationReceived = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        try {
            StackActivities.removeTop();
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        try {
            if (technicianImpl != null && StackActivities.getActivity().equals(context)) {
                if (isActive) {
                    technicianImpl.getServicesData();
                } else {
                    isNotificationReceived = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUIComponent() {
        companyName = (CustomTextView) findViewById(R.id.contractor_company_name);
        userName = (CustomTextView) findViewById(R.id.contractor_dsb_username);
        image_profile_pic = (ImageView) findViewById(R.id.image_profile_pic);
        work_status = (SwitchCompat) findViewById(R.id.contractor_dsb_work_status);

    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.contractor_dashboard_logut)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_emergency)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_estimate)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_luxury)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_settings)).setOnClickListener(this);
        image_profile_pic.setOnClickListener(this);
    }

    @Override
    public void onDataFetchDone() {

    }


    @Override
    public void onShowPgDialog() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog = new CustomProgressDialog(context);
                    pgDialog.prepareAndShowDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_logut:
                logoutDialog(new OnLogoutListener() {
                    @Override
                    public void onProceed() {
                        technicianImpl.logoutUser(mTechnician.getUser_id());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.contractor_dash_btn_emergency:
                startActivity(new Intent(context, TechnicianEmergencyRequestActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_estimate:
                startActivity(new Intent(context, TechnicianEstimateRequestActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_luxury:
                startActivity(new Intent(context, TechnicianLuxuryBuildingsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_settings:
                startActivity(new Intent(context, TechnicianSettingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.image_profile_pic:
                startActivity(new Intent(context, TechnicianSettingActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            technicianImpl.getServicesData();
            companyName.setText(technicianImpl.mTechnician.getCompany_name());
            userName.setText(technicianImpl.mTechnician.getTechnician_name());
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
            ImageLoader.getInstance().displayImage(technicianImpl.mTechnician.getProfile_image(), image_profile_pic, options);
            changeWorkingStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        askPermissionForLocation();
    }

    @Override
    public void updateWorkingStatus(String msg) {
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
        Utility.uiThreadAlert(context, getString(R.string.app_name), msg);
    }

    @Override
    public void failToUpdateWorkingStatus(String message, String status) {
        switch (status) {
            case "1":
                work_status.setChecked(false);
                break;
            case "3":
                work_status.setChecked(true);
                break;
        }
        Utility.uiThreadAlert(context, message);
    }


    @Override
    public void logoutSuccess(String msg) {
        try {
            stopService(new Intent(context, GPSTracker.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserPreference.saveTechnician(context, null);
        startActivity(new Intent(context, LoginActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void changeWorkingStatus() {
        try {
            if (mTechnician != null && !TextUtils.isEmpty(mTechnician.getStatus())) {
                switch (mTechnician.getStatus()) {
                    case "1":
                        work_status.setChecked(true);
                        break;
                    case "3":
                        work_status.setChecked(false);
                        break;
                    default:
                        work_status.setChecked(true);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        work_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    technicianImpl.updateWorkingStatus(mTechnician.getTechnician_id(), mTechnician.getUser_type(), "1");
                } else {
                    technicianImpl.updateWorkingStatus(mTechnician.getTechnician_id(), mTechnician.getUser_type(), "3");
                }
            }
        });
    }

    private void askPermissionForLocation() {
        try {
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            Log.i(TAG, report.toString());
                            if (report.getDeniedPermissionResponses().size() > 0) {
                                Utility.uiThreadAlert(context, getString(R.string.msg_permission), new Utility.OnDialogMultiButtonClick() {
                                    @Override
                                    public void onOkayButtonClick() {
                                        askPermissionForLocation();
                                    }

                                    @Override
                                    public void onCancelButtonClick() {
                                        onBackPressed();
                                    }
                                });
                            } else {
                                if (!LocationHandler.getInstance(context).isLocationServiceAvailable()) {
                                    LocationHandler.getInstance(context).createLocationServiceError(context);
                                }
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        ContextCompat.startForegroundService(context, new Intent(context, GPSTracker.class));
                                    } else {
                                        startService(new Intent(context, GPSTracker.class));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onResumeDataLoad() {
        try {
            mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
            companyName.setText(mTechnician.getCompany_name());
            userName.setText(mTechnician.getTechnician_name());
            ImageLoader.getInstance().displayImage(mTechnician.getProfile_image(), image_profile_pic, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void startReceiver() {
        try {
            if (pushReceiver == null) {
                pushReceiver = new PushReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(getPackageName() + MyFirebaseMessagingService.FCM_NOTIFICATION_RECEIVER_TECH_1);
                registerReceiver(pushReceiver, intentFilter);
            } else {
                stopReceiver();
                startReceiver();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopReceiver() {
        if (pushReceiver != null) {
            unregisterReceiver(pushReceiver);
            pushReceiver = null;
        }
    }

    public class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            technicianImpl.getServicesData();
        }
    }*/
}