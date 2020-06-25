package com.tradiuus.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.CustomerReqCancelFragment;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Customer;
import com.tradiuus.models.Job;
import com.tradiuus.models.MessageEvent;
import com.tradiuus.models.Reason;
import com.tradiuus.models.Technician;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;

import static com.tradiuus.notification.MyFirebaseMessagingService.FCM_PARAM_MESSAGE;
import static com.tradiuus.notification.MyFirebaseMessagingService.FCM_PARAM_TITLE;

public class CustomerEmergencyReqDetailActivity extends BaseActivity implements OnMapReadyCallback, TechnicianImpl.OnJobCancelListener {

    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    private LinearLayout cancel_section;

    private TextView message_text;
    private TextView phone;
    private TextView license;
    private TextView time_mage;

    private TextView contractor_name;
    private TextView contractor_job;
    private TextView contractor_time;
    private TextView contractor_cost;
    private ImageView technician_img;

    private LatLng currentLatLang;
    private GoogleMap mMap;
    public Customer customer;
    private Job job;
    private TechnicianImpl mTechnicianImpl;
    private Marker techMarker;
    private DisplayImageOptions options;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_customer_emergency_req_details);
        this.context = CustomerEmergencyReqDetailActivity.this;
        this.customer = ((TradiuusApp) context.getApplicationContext()).customer;
        this.currentLatLang = new LatLng(Double.valueOf(customer.getLat()), Double.valueOf(customer.getLng()));
        this.job = ((TradiuusApp) context.getApplicationContext()).job;

        mTechnicianImpl = new TechnicianImpl();
        mTechnicianImpl.init(context, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTask();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        Utility.uiThreadAlert(context, event.getData().get(FCM_PARAM_TITLE), event.getData().get(FCM_PARAM_MESSAGE), "OK", new Utility.OnDialogButtonClick() {

            @Override
            public void onOkayButtonClick() {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            ((TradiuusApp) getApplicationContext()).mServiceParam = null;
            ((TradiuusApp) getApplicationContext()).qsList = null;
            ((TradiuusApp) getApplicationContext()).mTrade = null;
            ((TradiuusApp) getApplicationContext()).mTechnician = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.INVISIBLE);
        contractor_dashboard_logut.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setText("Home");

        cancel_section = (LinearLayout) findViewById(R.id.cancel_section);

        message_text = (TextView) findViewById(R.id.message_text);
        phone = (TextView) findViewById(R.id.phone);
        license = (TextView) findViewById(R.id.license);
        time_mage = (TextView) findViewById(R.id.time_mage);

        contractor_name = (TextView) findViewById(R.id.contractor_name);
        contractor_job = (TextView) findViewById(R.id.technician_job);
        contractor_time = (TextView) findViewById(R.id.technician_time);
        contractor_cost = (TextView) findViewById(R.id.technician_cost);
        technician_img = (ImageView) findViewById(R.id.technician_img);

        ((ImageView) findViewById(R.id.lc_attachment)).setOnClickListener(this);
        license.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void initUIListener() {
        contractor_dashboard_logut.setOnClickListener(this);
        cancel_section.setOnClickListener(this);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_logut:
                startActivity(new Intent(this,CustomerDashboardActivity.class));
                finish();
                //onBackPressed();
                break;
            case R.id.cancel_section:
                showCancelDialog();
                break;
            case R.id.lc_attachment:
                if (this.job.getTechnician().get(0).getContractor_details() != null
                        && this.job.getTechnician().get(0).getContractor_details().getImages() != null
                        && this.job.getTechnician().get(0).getContractor_details().getImages().getTrade_license_img() != null)
                    showLicenseImage(this.job.getTechnician().get(0).getContractor_details().getImages().getTrade_license_img());
                break;
            case R.id.license:
                if (this.job.getTechnician().get(0).getContractor_details() != null
                        && this.job.getTechnician().get(0).getContractor_details().getImages() != null
                        && this.job.getTechnician().get(0).getContractor_details().getImages().getTrade_license_img() != null)
                    showLicenseImage(this.job.getTechnician().get(0).getContractor_details().getImages().getTrade_license_img());
                break;
        }
    }

    @Override
    protected void initLoadCall() {

        try {
            message_text.setText(this.job.getTechnician().get(0).getTechnician_message());
            phone.setText(this.job.getTechnician().get(0).getContractor_details().getMobile_number());
            license.setText((!TextUtils.isEmpty(this.job.getTechnician().get(0).getContractor_details().getLicense())) ? "Licensed" : "N/A");
            time_mage.setText(this.job.getTechnician().get(0).getContractor_details().getBusiness_end_time());

            contractor_name.setText(this.job.getTechnician().get(0).getContractor_details().getCompany_name());
            contractor_job.setText(this.job.getTechnician().get(0).getContractor_details().getGeo_jobs().size() + "");
            contractor_time.setText(this.job.getTechnician().get(0).getContractor_details().getEmergency_auto_response_time() + " mins");
            contractor_cost.setText("$" + this.job.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min()
                    + " - $" + this.job.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max());
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
            ImageLoader.getInstance().displayImage(this.job.getTechnician().get(0).getTechnician_profile(), technician_img, options);
            LinearLayout rattingView = (LinearLayout) findViewById(R.id.ratting);
            int ratting = Integer.valueOf(this.job.getTechnician().get(0).getContractor_details().getOverall_rating());
            for (int i = 0; i < 5; i++) {
                try {
                    if (ratting == 0) {
                        ((ImageView) rattingView.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.ratingstargray));
                    } else {
                        ((ImageView) rattingView.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.ratingstaryellow_2));
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            LatLng markerP = new LatLng(Double.valueOf(currentLatLang.latitude), Double.valueOf(currentLatLang.longitude));
            mMap.addMarker(new MarkerOptions().position(markerP).icon(BitmapDescriptorFactory.fromResource(R.drawable.menu_mapannotation_icon)));
            locateCamera(markerP);

            LatLng markerT = new LatLng(Double.valueOf(this.job.getTechnician().get(0).getTechnician_latitude()), Double.valueOf(this.job.getTechnician().get(0).getTechnician_longitude()));
            techMarker = mMap.addMarker(new MarkerOptions().position(markerT).icon(BitmapDescriptorFactory.fromResource(R.drawable.tech_marker)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locateCamera(LatLng latLng) {
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(11)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment) {
        try {
            FrameLayout container = findViewById(R.id.fragment_container);
            if (container.getChildCount() > 0) {
                container.removeAllViews();
            }
            String backStateName = fragment.getClass().getName();
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(backStateName);
                ft.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitReason(Reason currentReason) {
        onBackPressed();
        mTechnicianImpl.cancelJob(this.customer.getCustomer_id(), this.job.getJob_id(), currentReason.getReason_id(), this.job.getTechnician().get(0).getTechnician_id());
    }

    public void reConsider(Reason currentReason) {
        onBackPressed();
    }


    public long getDifference(String timeStamp) {
        try {
            Date d = new Date();
            long diff = d.getTime() - Long.valueOf(timeStamp) * 1000;
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            return minutes;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onJobCancelSuccess(Job mJob, String msg) {
        if (mJob != null && mJob.getStatus().equalsIgnoreCase("2")) {
            Utility.uiThreadAlert(context, getString(R.string.app_name), msg, "OK", new Utility.OnDialogButtonClick() {

                @Override
                public void onOkayButtonClick() {
                    try {
                        ArrayList<Job> emergencyJobList = ((TradiuusApp) getApplication()).emergencyJobList;
                        if (emergencyJobList != null && emergencyJobList.size() > 0) {
                            int index = -1;
                            for (int i = 0; i < emergencyJobList.size(); i++) {
                                if (emergencyJobList.get(i).getJob_id().equalsIgnoreCase(job.getJob_id())) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index > -1) {
                                ((TradiuusApp) getApplication()).emergencyJobList.remove(index);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

        } else {
            Utility.uiThreadAlert(context, getString(R.string.app_name), msg);
        }
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

    }

    private void showCancelDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.inflate_dialog_req_cancel);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ((TextView) dialog.findViewById(R.id.btn_proceed)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (getDifference(job.getCreated_on()) > 10) {
                            Utility.uiThreadAlert(context, getString(R.string.app_name), "You can not cancel job request after 10 minutes of job creating!");
                        } else {
                            replaceFragment(new CustomerReqCancelFragment());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            ((TextView) dialog.findViewById(R.id.btn_skip)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();
    private Runnable syncTask = new Runnable() {
        @Override
        public void run() {
            if (mTechnicianImpl != null) {
                mTechnicianImpl.getCurrentJobTechnicianDetails(customer.getCustomer_id(), customer.getUser_type(), true, job.getJob_id(), new TechnicianImpl.OnJobTechnicianDetailListener() {
                    @Override
                    public void onTechnicianFound(final Technician technician) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    techMarker.remove();
                                    LatLng markerT = new LatLng(Double.valueOf(technician.getTechnician_latitude()), Double.valueOf(technician.getTechnician_longitude()));
                                    techMarker = mMap.addMarker(new MarkerOptions().position(markerT).icon(BitmapDescriptorFactory.fromResource(R.drawable.tech_marker)));
                                }
                            });
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        startTask();
                    }

                    @Override
                    public void onFail() {
                        startTask();
                    }
                });
            } else {
                startTask();
            }
            Log.i("syncTask -> ", System.currentTimeMillis() + "");
        }
    };

    private void startTask() {
        try {
            handler.removeCallbacks(syncTask);
            handler.postDelayed(syncTask, 10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTask() {
        try {
            handler.removeCallbacks(syncTask);
        } catch (Exception e) {
            e.printStackTrace(); //menu_mapannotation_icon
        }
    }

    private void showLicenseImage(final String imageURL) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.inflate_dialog_licence);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.black_license)
                    .showImageOnFail(R.drawable.black_license)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();

            ((ImageView) dialog.findViewById(R.id.img_cross)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            ImageView imageLicense = (ImageView) dialog.findViewById(R.id.imageLicense);
            ImageLoader.getInstance().displayImage(imageURL, imageLicense, options);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            dialog.setCancelable(true);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}