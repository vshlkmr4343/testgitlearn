package com.tradiuus.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.CustomerAboutTradiuusFragment;
import com.tradiuus.fragments.CustomerHelpFragment;
import com.tradiuus.fragments.CustomerHistoryFragment;
import com.tradiuus.fragments.CustomerPromotionFragment;
import com.tradiuus.fragments.CustomerSettingsFragment;
import com.tradiuus.fragments.TechnicianLuxuryBuildingListFragment;
import com.tradiuus.helper.LocationHandler;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.AddressInfo;
import com.tradiuus.models.Customer;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Job;
import com.tradiuus.models.MessageEvent;
import com.tradiuus.models.Technician;
import com.tradiuus.models.Trade;
import com.tradiuus.models.Value;
import com.tradiuus.network.FindTechnicians;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tradiuus.notification.MyFirebaseMessagingService.FCM_PARAM_MESSAGE;
import static com.tradiuus.notification.MyFirebaseMessagingService.FCM_PARAM_TITLE;


public class CustomerDashboardActivity extends BaseActivity implements OnMapReadyCallback, CustomerImpl.OnCustomerDashboardListener {
    private final static String TAG = CustomerDashboardActivity.class.getSimpleName();
    private Context context;
    private CustomProgressDialog pgDialog;
    private DrawerLayout drawer;
    private ImageView action_menu;
    private TextView customer_name;
    private TextView customer_add;
    private TextView customer_phone;
    private LinearLayout more_trade_dialog;
    private ListView moreTradeList;

    private ImageView img_plumber;
    private ImageView img_electrician;
    private ImageView img_heat;
    private ImageView img_flood;
    private ImageView img_lock;
    private ImageView img_more;

    private CardView historyLayout;
    private TextView history_count;

    public Customer customer;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LatLng currentLatLang;

    public CustomerImpl customerImpl;
    private FrameLayout fragmentContainer;
    private Marker userMarker;
    private Circle userCircle;
    private int mapInfoW = 350;
    private DisplayImageOptions options;

    private enum ReqType {
        PLUMBER, ELECTRICIAN, HEAT, FLOOD, LOCK, MORE
    }

    public List<Trade> trades;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_dashboard);
        this.context = CustomerDashboardActivity.this;
        customer = ((TradiuusApp) context.getApplicationContext()).customer;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        trades = new ArrayList<>(((TradiuusApp) context.getApplicationContext()).mConfigData.getTrades());
        customerImpl = new CustomerImpl();
        customerImpl.init(context, this);
        currentLatLang = new LatLng(Double.valueOf(customer.getLat()), Double.valueOf(customer.getLng()));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (getSupportFragmentManager().getFragments().size() > 2) {
            backToHome();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                replaceFragment(new CustomerHistoryFragment());
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            if (mServiceBound) {
                unbindService(mServiceConnection);
                mServiceBound = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        action_menu = (ImageView) findViewById(R.id.action_menu);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        more_trade_dialog = (LinearLayout) findViewById(R.id.more_trade_dialog);
        moreTradeList = (ListView) findViewById(R.id.list_trade);

        customer_name = (TextView) findViewById(R.id.customer_dashboard_name);
        customer_add = (TextView) findViewById(R.id.customer_dashboard_add);
        customer_phone = (TextView) findViewById(R.id.customer_dashboard_phone);

        img_plumber = (ImageView) findViewById(R.id.img_plumber);
        img_electrician = (ImageView) findViewById(R.id.img_electrician);
        img_heat = (ImageView) findViewById(R.id.img_heat);
        img_flood = (ImageView) findViewById(R.id.img_flood);
        img_lock = (ImageView) findViewById(R.id.img_lock);
        img_more = (ImageView) findViewById(R.id.img_more);

        historyLayout = (CardView) findViewById(R.id.historyLayout);
        history_count = (TextView) findViewById(R.id.history_count);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            mapInfoW = (int) (getResources().getDisplayMetrics().widthPixels * 0.75);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUIListener() {
        action_menu.setOnClickListener(this);
        ((TextView) findViewById(R.id.customer_dashboard_btn_logout)).setOnClickListener(this);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        ((ImageView) findViewById(R.id.img_plumber)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img_electrician)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img_heat)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img_flood)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img_lock)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img_more)).setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ((CustomTextView) findViewById(R.id.navHome)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.navAboutTradiuus)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.navSettings)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.navPromotion)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.navHelp)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.navHistory)).setOnClickListener(this);

        ((ImageView) findViewById(R.id.action_cross)).setOnClickListener(this);
        try {
            ((LinearLayout) findViewById(R.id.upperView)).setOnClickListener(this);
            ((LinearLayout) findViewById(R.id.bottom_view)).setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initLoadCall() {
        customer_name.setText(customer.getFirst_name() + " " + customer.getLast_name());
        customer_add.setText(customer.getFull_address());
        customer_phone.setText(customer.getMobile_number());
        try {
            List<Trade> moreTrades = new ArrayList<>();
            for (int i = 5; i < trades.size(); i++) {
                moreTrades.add(trades.get(i));
            }
            moreTradeList.setAdapter(new MoreTradeAdapter(context, moreTrades));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intent intent = new Intent(getApplicationContext(), FindTechnicians.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Its a notification Call*/
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey(FCM_PARAM_TITLE)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        replaceFragment(new CustomerHistoryFragment());
                    }
                }, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShowPgDialog() {
        try {
            pgDialog = new CustomProgressDialog(context);
            pgDialog.prepareAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHidePgDialog() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog.dismissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showAlert(String message) {
        Utility.uiThreadAlert(context, context.getString(R.string.app_name), message);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.action_menu:
                drawer.openDrawer(Gravity.RIGHT);
                break;
            case R.id.customer_dashboard_btn_logout:
                closeDrawer();
                logoutDialog(new OnLogoutListener() {
                    @Override
                    public void onProceed() {
                        try {
                            customerImpl.logoutUser(customer.getUser_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.img_plumber:
                handleDoubleTap(false);
                changeItemColor(ReqType.PLUMBER);
                showRequestDialog(ReqType.PLUMBER, -1);
                /*try {
                    JSONObject obj = new JSONObject(Utility.loadJSONFromAsset(context));
                    ((TradiuusApp) getApplication()).job  = new Gson().fromJson(obj.getJSONObject("job_info").toString(), Job.class);
                    startActivity(new Intent(context, CustomerEmergencyReqDetailActivity.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.img_electrician:
                handleDoubleTap(false);
                changeItemColor(ReqType.ELECTRICIAN);
                showRequestDialog(ReqType.ELECTRICIAN, -1);
                break;
            case R.id.img_heat:
                handleDoubleTap(false);
                changeItemColor(ReqType.HEAT);
                showRequestDialog(ReqType.HEAT, -1);
                break;
            case R.id.img_flood:
                handleDoubleTap(false);
                changeItemColor(ReqType.FLOOD);
                showRequestDialog(ReqType.FLOOD, -1);
                break;
            case R.id.img_lock:
                handleDoubleTap(false);
                changeItemColor(ReqType.LOCK);
                showRequestDialog(ReqType.LOCK, -1);
                break;
            case R.id.img_more:
                handleDoubleTap(false);
                loadMoreMenu();
                break;
            case R.id.action_cross:
                loadMoreMenu();
                break;
            case R.id.navHome:
                backToHome();
                break;
            case R.id.navAboutTradiuus:
                replaceFragment(new CustomerAboutTradiuusFragment());
                break;
            case R.id.navSettings:
                replaceFragment(new CustomerSettingsFragment());
                break;
            case R.id.navPromotion:
                replaceFragment(new CustomerPromotionFragment());
                break;
            case R.id.navHelp:
                replaceFragment(new CustomerHelpFragment());
                break;
            case R.id.navHistory:
                replaceFragment(new CustomerHistoryFragment());
                break;
        }
    }

    private void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    public void replaceFragment(Fragment fragment) {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragmentContainer, fragment);
            ft.commitAllowingStateLoss();
            if (fragmentContainer.getVisibility() == View.GONE) {
                fragmentContainer.setVisibility(View.VISIBLE);
                fragmentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            closeDrawer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backToHome() {
        try {
            for (int i = 2; i < getSupportFragmentManager().getFragments().size(); i++) {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().getFragments().get(i)).commit();
            }
            fragmentContainer.removeAllViews();
            fragmentContainer.setVisibility(View.GONE);
            closeDrawer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMoreMenu() {
        if (more_trade_dialog.getVisibility() == View.VISIBLE) {
            handleDoubleTap(true);
            more_trade_dialog.setVisibility(View.GONE);
        } else {
            handleDoubleTap(false);
            more_trade_dialog.setVisibility(View.VISIBLE);
        }
    }

    private void showRequestDialog(final ReqType reqtype, final int index) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.inflate_dialog_customer_request);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ((ImageView) dialog.findViewById(R.id.img_cross)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.emergency_req)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerEmergencyCaseActivity.class);
                intent.putExtra("emType", (reqtype == ReqType.MORE) ? index + 1 : reqtype.ordinal());
                startActivity(intent);
                ((CustomerDashboardActivity) context).overridePendingTransition(0, 0);
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.estimate_req)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerEstimateCaseActivity.class);
                intent.putExtra("emType", (reqtype == ReqType.MORE) ? index + 1 : reqtype.ordinal());
                startActivity(intent);
                ((CustomerDashboardActivity) context).overridePendingTransition(0, 0);
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handleDoubleTap(true);
                changeItemColor(null);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    public class MoreTradeAdapter extends BaseAdapter {

        private List<Trade> items;
        private Context context;
        private LayoutInflater inflater;
        private DisplayImageOptions options;

        public MoreTradeAdapter(Context context, List<Trade> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            options = new DisplayImageOptions.Builder()
                    //.showImageForEmptyUri(R.drawable.add_image)
                    //.showImageOnFail(R.drawable.add_image)
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
            return (items == null) ? 0 : items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.inflate_more_trade_row, null);
                viewHolder.trade_icon = (ImageView) convertView.findViewById(R.id.trade_icon);
                viewHolder.trade_row_title = (TextView) convertView.findViewById(R.id.trade_row_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Trade mTrade = items.get(position);
            viewHolder.trade_row_title.setText(mTrade.getTrade_name());
            ImageLoader.getInstance().displayImage(mTrade.getFeatured_normal_image(), viewHolder.trade_icon, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    try {
                        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) view).getDrawable()), Color.parseColor("#01579b"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreMenu();
                    showRequestDialog(ReqType.MORE, (4 + position));
                }
            });
            return convertView;
        }


        private class ViewHolder {
            public TextView trade_row_title;
            public ImageView trade_icon;
        }

    }


    /*****************************************************************************************/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            mMap.setInfoWindowAdapter(mInfoWindowAdapter);

            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("newReg")) {
                newRegDialog();
            } else {
                askPermissionForLocation();
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTag() != null && mapTechnician.containsKey(String.valueOf(marker.getTag()))) {
                        showTechDetails(mapTechnician.get(String.valueOf(marker.getTag())));
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askPermissionForLocation() {
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
                            /*mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());*/
                            mMap.setMyLocationEnabled(false);
                            locateCamera(currentLatLang, customer.getFull_address());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Log.i(TAG, token.toString());
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }


    /*private LocationCallback mLocationCallback = new LocationCallback() {
        private boolean firstTimeLoad = true;

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            try {
                for (Location location : locationResult.getLocations()) {
                    currentLatLang = new LatLng(location.getLatitude(), location.getLongitude());
                    if (firstTimeLoad) {
                        locateCamera(currentLatLang);
                        firstTimeLoad = false;
                    }
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };*/
    float zoom[] = new float[]{
            14.0f,//0
            13.80f,//1
            12.65f,//2
            12.2f,//3
            11.8f,//4
            11.5f,//5
            11.30f,//6
            11.0f,//7
            10.85f,//8
            10.70f,//9
            10.50f};//0

    private void locateCamera(LatLng latLng, String title) {
        try {
            if (userCircle != null) {
                userCircle.remove();
            }
            userCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius((Integer.valueOf(customer.getMax_distance()) * 1609.34))
                    .strokeColor(0x803D5AFE)
                    .fillColor(0x223D5AFE));
            if (userMarker != null) {
                userMarker.remove();
            }
            userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.menu_mapannotation_icon)));
            userMarker.showInfoWindow();
            float zoomLv = zoom[Integer.valueOf(customer.getMax_distance())];
            Log.i("Zoom Level Animate:", zoomLv + "");
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(zoomLv)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
             /*if (userCircle != null) {
                userCircle.remove();
            }
            userCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(Integer.valueOf(customer.getMax_distance()) * 1609.34) // Converting Miles into Meters...
                    .strokeColor(Color.RED)
                    .strokeWidth(5));
            userCircle.isVisible();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);*/

            /*double iMeter = Integer.valueOf(customer.getMax_distance()) * 1609.34;
            if (userCircle != null) {
                userCircle.remove();
            }
            userCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(iMeter) // Converting Miles into Meters...
                    .strokeColor(0x803D5AFE)
                    .fillColor(0x223D5AFE)
                    .strokeWidth(5));
            userCircle.isVisible();
            float currentZoomLevel = getZoomLevel(userCircle);
            float animateZomm = currentZoomLevel + 5;

            Log.e("Zoom Level:", currentZoomLevel + "");
            Log.e("Zoom Level Animate:", animateZomm + "");

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, animateZomm));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel), 500, null);
            Log.e("Circle Lat Long:", latLng.latitude + ", " + latLng.latitude);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public float getZoomLevel(Circle circle) {
        float zoomLevel = 0;
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel + .5f;
    }*/

    /**************************************** Syn call to find technician near you***************************************/

    private FindTechnicians mBoundService;
    private boolean mServiceBound = false;
    private String msg = "";

    public ArrayList<Job> emergencyOnGoingJobs = new ArrayList<>();
    public ArrayList<Job> estimateOnGoingJobs = new ArrayList<>();
    public ArrayList<Job> emergencyOnGoingJobsAll = new ArrayList<>();
    public ArrayList<Job> estimateOnGoingJobsAll = new ArrayList<>();
    private HashMap<String, Technician> mapTechnician = new HashMap<>();
    private HashMap<String, Marker> mapMarker = new HashMap<>();

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FindTechnicians.MyBinder myBinder = (FindTechnicians.MyBinder) service;
            mBoundService = myBinder.getService();
            mBoundService.setCustomerSyncListener(new FindTechnicians.CustomerSyncListener() {
                @Override
                public void foundTechnicians(final ArrayList<Technician> technicians) {
                    try {
                        Log.i("Sync working 1", " -> " + System.currentTimeMillis());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addMarkerInMap(technicians);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void jobHistory(final ArrayList<Job> emergencyJobs, final ArrayList<Job> estimateJobs) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Sync working 2", " -> " + System.currentTimeMillis());
                            try {
                                Log.i("History", emergencyJobs.size() + " " + estimateJobs.size());
                                emergencyOnGoingJobsAll = emergencyJobs;
                                estimateOnGoingJobsAll = estimateJobs;

                                int currentJobCount = 0;
                                emergencyOnGoingJobs = new ArrayList<>();
                                estimateOnGoingJobs = new ArrayList<>();

                                if (emergencyJobs != null && emergencyJobs.size() > 0) {
                                    for (Job mJob : emergencyJobs) {
                                        if (mJob.getJob_status().equalsIgnoreCase("1")) {
                                            emergencyOnGoingJobs.add(mJob);
                                            currentJobCount++;
                                        }
                                    }
                                }
                                if (estimateJobs != null && estimateJobs.size() > 0) {
                                    for (Job mJob : estimateJobs) {
                                        if (mJob.getJob_status().equalsIgnoreCase("1")) {
                                            estimateOnGoingJobs.add(mJob);
                                            currentJobCount++;
                                        }
                                    }
                                }
                                if (currentJobCount > 0) {
                                    historyLayout.setVisibility(View.VISIBLE);
                                    history_count.setText(currentJobCount + " Ongoing Job" + ((currentJobCount == 1) ? "" : "(s)"));
                                    historyLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ((TradiuusApp) getApplication()).emergencyJobList = emergencyOnGoingJobs;
                                            ((TradiuusApp) getApplication()).estimateJobList = estimateOnGoingJobs;
                                            startActivity(new Intent(context, CustomerJobHistoryActivity.class));
                                            overridePendingTransition(0, 0);
                                        }
                                    });

                                } else {
                                    historyLayout.setVisibility(View.GONE);
                                }

                    /*try {
                        if (getSupportFragmentManager().getFragments().size() > 2) {
                            Fragment fragment = getSupportFragmentManager().getFragments().get(2);
                            if (fragment instanceof CustomerHistoryFragment) {
                                CustomerHistoryFragment mCustomerHistoryFragment = ((CustomerHistoryFragment) fragment);
                                mCustomerHistoryFragment.jobHistory(emergencyOnGoingJobsAll, estimateOnGoingJobsAll);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void removeAllTech(final String message) {
                    Log.i("Sync working 3", " -> " + System.currentTimeMillis());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*if (!msg.equalsIgnoreCase(message) && !drawer.isDrawerOpen(GravityCompat.END)) {
                                msg = message;
                                Utility.uiThreadAlert(context, context.getString(R.string.app_name), message);
                            }*/
                            try {
                                if (mapMarker.size() > 0) {
                                    for (String key : mapMarker.keySet()) {
                                        try {
                                            mapMarker.get(key).remove();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                mapMarker.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            mServiceBound = true;
        }
    };

    @Override
    public void foundTechnicians(final ArrayList<Technician> technicians) {

    }

    @Override
    public void removeAllTech(String message) {

    }

    @Override
    public void jobHistory(final ArrayList<Job> emergencyJobs, final ArrayList<Job> estimateJobs) {

    }


    private void addMarkerInMap(ArrayList<Technician> technicians) {
        try {
            if (mapMarker.size() > 0) {
                /**** found newly added technicians *****/
                ArrayList<Technician> addedTechnicians = new ArrayList<Technician>();
                for (Technician mTechnician : technicians) {
                    if (!mapMarker.containsKey(mTechnician.getTechnician_id())) {
                        addedTechnicians.add(mTechnician);
                    }
                }
                /**** found need to remove technicians *****/
                ArrayList<String> removeTechnicians = new ArrayList<String>();
                for (String key : mapMarker.keySet()) {
                    boolean found = false;
                    for (Technician mTechnician : technicians) {
                        if (key.equalsIgnoreCase(mTechnician.getTechnician_id())) {
                            found = true;
                            try {
                                Log.i("Location ->" + mTechnician.getTechnician_id(), "Lat:" + mTechnician.getTechnician_latitude() + " Lng:" + mTechnician.getTechnician_longitude());
                                mapMarker.get(key).setPosition(new LatLng(Double.valueOf(mTechnician.getTechnician_latitude()), Double.valueOf(mTechnician.getTechnician_longitude())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                    if (!found) {
                        removeTechnicians.add(key);

                    }
                }
                //First delete
                for (String key : removeTechnicians) {
                    try {
                        mapMarker.get(key).remove();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Now add your new one
                for (Technician mTechnician : addedTechnicians) {
                    loadMarker(mTechnician);
                }
            } else {
                for (Technician mTechnician : technicians) {
                    loadMarker(mTechnician);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void addMarkerInMap(ArrayList<Technician> technicians) {
        try {
            if (mapMarker.size() > 0) {
                for (String key : mapMarker.keySet()) {
                    try {
                        mapMarker.get(key).remove();
                        mapTechnician.remove(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (Technician mTechnician : technicians) {
                loadMarker(mTechnician);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void loadMarker(final Technician mTechnician) {
        Thread thread = new Thread(new Runnable() {
            String OnMapImageUrl = "";
            Bitmap bmp = null;

            @Override
            public void run() {
                try {

                    for (Trade trade : mConfigData.getTrades()) {
                        if (mTechnician.getTrade_id().equalsIgnoreCase(trade.getTrade_id())) {
                            OnMapImageUrl = trade.getOn_map_image();
                            break;
                        }
                    }
                    bmp = BitmapFactory.decodeStream(new URL(OnMapImageUrl).openConnection().getInputStream());
                    bmp = Bitmap.createScaledBitmap(bmp, 72, 72, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapTechnician.put(mTechnician.getTechnician_id(), mTechnician);
                        LatLng markerP = new LatLng(Double.valueOf(mTechnician.getTechnician_latitude()), Double.valueOf(mTechnician.getTechnician_longitude()));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(markerP)
                                .icon(TextUtils.isEmpty(OnMapImageUrl) ? BitmapDescriptorFactory.fromResource(R.drawable.menu_mapannotation_icon) : BitmapDescriptorFactory.fromBitmap(bmp)));
                        marker.setTag(mTechnician.getTechnician_id());
                        mapMarker.put(mTechnician.getTechnician_id(), marker);
                    }
                });
            }
        });
        thread.start();
    }

    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            try {
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);
                info.setLayoutParams(new LinearLayout.LayoutParams(mapInfoW, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.NORMAL);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    };


    private void showTechDetails(final Technician technician) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.inflate_technician_dialog);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView contractor_name = (TextView) dialog.findViewById(R.id.contractor_name);
            TextView contractor_trade = (TextView) dialog.findViewById(R.id.contractor_trade);

            TextView contractor_technician_name = (TextView) dialog.findViewById(R.id.contractor_technician_name);
            TextView technician_job = (TextView) dialog.findViewById(R.id.technician_job);
            TextView technician_time = (TextView) dialog.findViewById(R.id.technician_time);
            TextView technician_cost = (TextView) dialog.findViewById(R.id.technician_cost);

            ((TextView) dialog.findViewById(R.id.view_detail)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TradiuusApp) context.getApplicationContext()).mTechnician = technician;
                    ((TradiuusApp) context.getApplicationContext()).currentLatLan = currentLatLang;
                    startActivity(new Intent(context, CustomerContractorDetailActivity.class));
                    overridePendingTransition(0, 0);
                    dialog.dismiss();
                }
            });
            ((TextView) dialog.findViewById(R.id.schedule)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        for (int i = 0; i < trades.size(); i++) {
                            if (technician.getTrade_id().equalsIgnoreCase(trades.get(i).getTrade_id())) {
                                ((TradiuusApp) context.getApplicationContext()).mTechnician = technician;
                                Intent intent = new Intent(context, (technician.getService_type().equalsIgnoreCase("1")) ? CustomerEmergencyCaseActivity.class : CustomerEstimateCaseActivity.class);
                                intent.putExtra("emType", i);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            ((ImageView) dialog.findViewById(R.id.call_icon)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri number = Uri.parse("tel:" + technician.getTechnician_mobile());
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ((ImageView) dialog.findViewById(R.id.cross)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ((ImageView) dialog.findViewById(R.id.video)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String url = technician.getContractor_details().getVideo_url();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setDataAndType(Uri.parse(url), "video/*");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                LinearLayout rattingView = (LinearLayout) dialog.findViewById(R.id.ratting);
                int ratting = Integer.valueOf(technician.getContractor_details().getOverall_rating());
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
                ImageLoader.getInstance().displayImage(technician.getTechnician_profile(), (ImageView) dialog.findViewById(R.id.profile_image), options);
                ImageLoader.getInstance().displayImage(technician.getContractor_details().getImages().getCompany_logo(), (ImageView) dialog.findViewById(R.id.technician_img), options);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            dialog.show();

            contractor_name.setText(technician.getContractor_details().getCompany_name());
            contractor_trade.setText(technician.getContractor_details().getTrade().get(0).getTrade_name());
            contractor_technician_name.setText(technician.getTechnician_name());
            technician_job.setText(technician.getContractor_details().getGeo_jobs().size() + "");
            technician_time.setText(technician.getContractor_details().getEmergency_auto_response_time() + "mins");
            technician_cost.setText("$" + technician.getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min() + " - $"
                    + technician.getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeItemColor(ReqType mReqType) {
        img_plumber.setImageDrawable(getResources().getDrawable(R.drawable.icon_plum));
        img_electrician.setImageDrawable(getResources().getDrawable(R.drawable.icon_elec));
        img_heat.setImageDrawable(getResources().getDrawable(R.drawable.icon_heat));
        img_flood.setImageDrawable(getResources().getDrawable(R.drawable.icon_reme));
        img_lock.setImageDrawable(getResources().getDrawable(R.drawable.icon_lock));
        if (mReqType != null) {
            switch (mReqType) {
                case PLUMBER:
                    img_plumber.setColorFilter(Color.parseColor("#5fd8e3"));
                    break;
                case ELECTRICIAN:
                    img_electrician.setColorFilter(Color.parseColor("#5fd8e3"));
                    break;
                case HEAT:
                    img_heat.setColorFilter(Color.parseColor("#5fd8e3"));
                    break;
                case FLOOD:
                    img_flood.setColorFilter(Color.parseColor("#5fd8e3"));
                    break;
                case LOCK:
                    img_lock.setColorFilter(Color.parseColor("#5fd8e3"));
                    break;
            }
        } else {
            img_plumber.setColorFilter(Color.WHITE);
            img_electrician.setColorFilter(Color.WHITE);
            img_heat.setColorFilter(Color.WHITE);
            img_flood.setColorFilter(Color.WHITE);
            img_lock.setColorFilter(Color.WHITE);
        }
    }

    public void updateUserLocation(String address, String city, String zipcode, Value liveIn, int maxDistance) {
        try {
            customerImpl.changeLocation(customer.getCustomer_id(), customer.getUser_id(), address,
                    city, zipcode, liveIn.getOption_id(), maxDistance + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String oldPwd, String newPwd) {
        try {
            customerImpl.updatePassword(customer.getUser_id(), oldPwd, newPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPasswordUpdateSuccess(String message) {
        try {
            Utility.uiThreadAlert(context, "Password", message, "OK", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void locationUpdate(AddressInfo mAddressInfo, String message) {
        try {
            this.customer = ((TradiuusApp) context.getApplicationContext()).customer;
            Utility.uiThreadAlert(context, context.getString(R.string.app_name), message, "OK", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {
                    onBackPressed();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                currentLatLang = new LatLng(Double.valueOf(customer.getLat()), Double.valueOf(customer.getLng()));
                                locateCamera(currentLatLang, customer.getFull_address());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logoutSuccess(String message) {
        UserPreference.saveCustomer(context, null);
        startActivity(new Intent(context, LoginActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void handleDoubleTap(boolean disableButtons) {
        img_plumber.setEnabled(disableButtons);
        img_electrician.setEnabled(disableButtons);
        img_heat.setEnabled(disableButtons);
        img_flood.setEnabled(disableButtons);
        img_lock.setEnabled(disableButtons);
        img_more.setEnabled(disableButtons);
    }

    public void newRegDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Thank You");
        builder1.setMessage("for Registration into Tradiuus");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        askPermissionForLocation();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}