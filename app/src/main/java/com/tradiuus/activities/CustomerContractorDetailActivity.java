package com.tradiuus.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
import com.tradiuus.models.Contractor;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Job;
import com.tradiuus.models.Question;
import com.tradiuus.models.Service;
import com.tradiuus.models.Technician;
import com.tradiuus.network.postparams.ServiceParam;
import com.tradiuus.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;


public class CustomerContractorDetailActivity extends BaseActivity implements OnMapReadyCallback, CustomerImpl.OnCustomerGetTradeQuestionnaireListener {

    private Context context;
    private CustomProgressDialog pgDialog;

    private TextView contractor_name;
    private TextView technician_job;
    private TextView technician_time;
    private TextView technician_cost;
    private TextView contractor_insurance;
    private TextView contractor_license;
    private TextView contractor_workers;
    private TextView contractor_trucks;
    private TextView contractor_website;
    private TextView contractor_time;
    private TextView contractor_zip;
    private TextView contractor_availability;
    private TextView contractor_bcg_check;
    private TextView contractor_ratting_count;
    private TextView contractor_services;
    private TextView emergency_ratting;

    private LinearLayout emergency_btn;
    private ImageView emergency_btn_icon;
    private TextView emergency_btn_title;
    private LinearLayout estimate_btn;
    private ImageView estimate_btn_icon;
    private TextView estimate_btn_title;
    private ListView workList;

    public CustomerImpl customerImpl;
    public Technician mTechnician;
    private boolean selectedState = true;

    private LatLng currentLatLang;
    private GoogleMap mMap;
    private DisplayImageOptions options;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_customer_contractor_detail);
        this.context = CustomerContractorDetailActivity.this;
        customerImpl = new CustomerImpl();
        customerImpl.init(context, this);
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
        currentLatLang = ((TradiuusApp) context.getApplicationContext()).currentLatLan;
    }

    @Override
    public void onBackPressed() {
        ((TradiuusApp) context.getApplicationContext()).mTechnician = null;
        super.onBackPressed();
    }

    @Override
    protected void initUIComponent() {
        try {
            contractor_name = (TextView) findViewById(R.id.contractor_name);
            technician_job = (TextView) findViewById(R.id.technician_job);
            technician_time = (TextView) findViewById(R.id.technician_time);
            technician_cost = (TextView) findViewById(R.id.technician_cost);

            contractor_insurance = (TextView) findViewById(R.id.contractor_insurance);
            contractor_license = (TextView) findViewById(R.id.contractor_license);
            contractor_workers = (TextView) findViewById(R.id.contractor_workers);
            contractor_trucks = (TextView) findViewById(R.id.contractor_trucks);

            contractor_website = (TextView) findViewById(R.id.contractor_website);
            contractor_time = (TextView) findViewById(R.id.contractor_time);
            contractor_zip = (TextView) findViewById(R.id.contractor_zip);
            contractor_availability = (TextView) findViewById(R.id.contractor_availability);
            contractor_bcg_check = (TextView) findViewById(R.id.contractor_bcg_check);
            contractor_ratting_count = (TextView) findViewById(R.id.contractor_ratting_count);
            contractor_services = (TextView) findViewById(R.id.contractor_services);
            emergency_ratting = (TextView) findViewById(R.id.emergency_ratting);

            emergency_btn = (LinearLayout) findViewById(R.id.emergency_btn);
            emergency_btn_icon = (ImageView) findViewById(R.id.emergency_btn_icon);
            emergency_btn_title = (TextView) findViewById(R.id.emergency_btn_title);

            estimate_btn = (LinearLayout) findViewById(R.id.estimate_btn);
            estimate_btn_icon = (ImageView) findViewById(R.id.estimate_btn_icon);
            estimate_btn_title = (TextView) findViewById(R.id.estimate_btn_title);

            workList = (ListView) findViewById(R.id.workList);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

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
        try {
            ((ImageView) findViewById(R.id.cross_button)).setOnClickListener(this);
            ((LinearLayout) findViewById(R.id.emergency_btn)).setOnClickListener(this);
            ((LinearLayout) findViewById(R.id.estimate_btn)).setOnClickListener(this);
            ((LinearLayout) findViewById(R.id.workListButton)).setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.cross_button:
                onBackPressed();
                break;
            case R.id.emergency_btn:
                if (!selectedState) {
                    setEmergencyServices(true);
                    selectTab(true);
                    selectedState = true;
                }
                break;
            case R.id.estimate_btn:
                if (selectedState) {
                    setEmergencyServices(false);
                    selectTab(false);
                    selectedState = false;
                }
                break;
            case R.id.workListButton:
                workList.setVisibility((workList.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            Contractor mContractor = mTechnician.getContractor_details();
            contractor_name.setText(mContractor.getCompany_name());
            technician_job.setText(mContractor.getGeo_jobs().size() + "");
            technician_time.setText(mContractor.getEmergency_auto_response_time() + "mins");
            technician_cost.setText("$" + mContractor.getTrade().get(0).getEmergency_services().get(0).getPrice_min() + " - $"
                    + mContractor.getTrade().get(0).getEmergency_services().get(0).getPrice_max());
            contractor_insurance.setText(TextUtils.isEmpty(mContractor.getGl_insurance()) ? "No" : "Yes");
            contractor_license.setText(TextUtils.isEmpty(mContractor.getLicense()) ? "No" : "Yes");
            contractor_workers.setText(TextUtils.isEmpty(mContractor.getWc_insurance()) ? "No" : "Yes");
            contractor_trucks.setText(mContractor.getAvailable_trucks());

            contractor_website.setText(mContractor.getWebsite_url());
            contractor_time.setText(mContractor.getBusiness_start_time() + " - " + mContractor.getBusiness_end_time());
            contractor_zip.setText(mContractor.getZip_codes().get(0) + "");
            contractor_availability.setText(mContractor.getTotal_technicians() + "");
            contractor_bcg_check.setText(TextUtils.isEmpty(mContractor.getBackground_check()) ? "No" : "Yes");
            contractor_ratting_count.setText(mContractor.getOverall_rating() + "");
            setEmergencyServices(true);
            selectTab(selectedState);


            workList.setAdapter(new GeoJobAdapter(context, new ArrayList<Job>(mContractor.getGeo_jobs())));


            for (Job mJob : mContractor.getGeo_jobs()) {
                LatLng markerP = new LatLng(Double.valueOf(mJob.getPlace_details().getLat()), Double.valueOf(mJob.getPlace_details().getLat()));
               /* Marker marker = mMap.addMarker(new MarkerOptions().position(markerP)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.menu_mapannotation_icon)));
                marker.setTag(mTechnician.getTechnician_id());*/
            }

            LinearLayout rattingView = (LinearLayout) findViewById(R.id.rattings);
            int ratting = Integer.valueOf(mTechnician.getContractor_details().getOverall_rating());
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
            ImageLoader.getInstance().displayImage(mTechnician.getContractor_details().getImages().getCompany_logo(),(ImageView) findViewById(R.id.technician_img)  , options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTradeQuestionnaire(Question question) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setAllGesturesEnabled(true);

            LatLng markerP = new LatLng(Double.valueOf(currentLatLang.latitude), Double.valueOf(currentLatLang.longitude));
            mMap.addMarker(new MarkerOptions().position(markerP).icon(BitmapDescriptorFactory.fromResource(R.drawable.menu_mapannotation_icon)));

            locateCamera(markerP);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void locateCamera(LatLng latLng) {
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(10)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectTab(boolean isEmergencySelected) { //38C7B5  red F86A54
        if (isEmergencySelected) {
            emergency_btn.setBackgroundColor(Color.parseColor("#F86A54"));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) emergency_btn_icon).getDrawable()), Color.WHITE);
            emergency_btn_title.setTextColor(Color.WHITE);

            estimate_btn.setBackground(getResources().getDrawable(R.drawable.estimate_rec));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) estimate_btn_icon).getDrawable()), Color.parseColor("#38C7B5"));
            estimate_btn_title.setTextColor(Color.parseColor("#38C7B5"));

        } else {
            emergency_btn.setBackground(getResources().getDrawable(R.drawable.emergency_rec));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) emergency_btn_icon).getDrawable()), Color.parseColor("#F86A54"));
            emergency_btn_title.setTextColor(Color.parseColor("#F86A54"));

            estimate_btn.setBackgroundColor(Color.parseColor("#38C7B5"));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) estimate_btn_icon).getDrawable()), Color.WHITE);
            estimate_btn_title.setTextColor(Color.WHITE);
        }
    }

    private void setEmergencyServices(boolean servicesFor) {
        try {
            String services = "";
            Contractor mContractor = mTechnician.getContractor_details();
            ArrayList<ServiceParam> emergency_services = mContractor.getTrade().get(0).getEmergency_services();
            ArrayList<ServiceParam> estimate_services = mContractor.getTrade().get(0).getEstimate_services();
            for (ServiceParam serviceParam : (servicesFor) ? emergency_services : estimate_services) {
                services = services + serviceParam.getService_name() + ", ";
            }
            contractor_services.setText(services);
            emergency_ratting.setText((servicesFor) ? mContractor.getTrade().get(0).getEmergency_rating() + ""
                    : mContractor.getTrade().get(0).getEstimate_rating() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GeoJobAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<Job> jobs = new ArrayList<>();

        public GeoJobAdapter(Context context, ArrayList<Job> jobs) {
            this.jobs = jobs;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return (jobs != null && jobs.size() > 0) ? jobs.size() : 0;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            try {
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.inflate_emergency_layout, null);
                    holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
                    holder.location = (TextView) convertView.findViewById(R.id.location);
                    holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Job mJob = jobs.get(position);

                holder.layout.setBackgroundColor((mJob.getService_type().equalsIgnoreCase("1")) ? Color.parseColor("#F86A54") : Color.parseColor("#38C7B5"));
                holder.location.setText(mJob.getPlace_details().getAddress());
                holder.icon.setImageDrawable((mJob.getService_type().equalsIgnoreCase("1")) ? getResources().getDrawable(R.drawable.emergencyheader) : getResources().getDrawable(R.drawable.estimateheader_2));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView location;
            LinearLayout layout;
        }
    }

}