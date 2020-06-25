package com.tradiuus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Customer;
import com.tradiuus.models.Job;
import com.tradiuus.models.Question;
import com.tradiuus.models.Service;
import com.tradiuus.models.Technician;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.models.Trade;
import com.tradiuus.widgets.CustomImageViewerDialog;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomerFindEmergencyTechnicianActivity extends BaseActivity implements TechnicianImpl.OnTechnicianFundListener, OnMapReadyCallback {

    private Service mService;
    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;

    private ListView listTechnician;
    private MoreTradeAdapter mMoreTradeAdapter;

    private TechnicianImpl mTechnicianImpl;
    public Customer customer;

    private Technician selected = null;
    public ArrayList<Question> qsList = new ArrayList<>();
    public ArrayList<String> imagesList = new ArrayList<>();
    public Trade selectedTrade;
    private GoogleMap mMap;

    private boolean isSelected = false;
    private ImageView technician_map;
    private RelativeLayout mapLayout;
    private Technician currentTechnician;
    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.customer_findemergencytechnician_activity);
        this.context = CustomerFindEmergencyTechnicianActivity.this;
        this.customer = ((TradiuusApp) context.getApplicationContext()).customer;
        this.mService = ((TradiuusApp) context.getApplicationContext()).mServiceParam;
        this.selectedTrade = ((TradiuusApp) getApplicationContext()).mTrade;
        this.qsList = ((TradiuusApp) getApplicationContext()).qsList;
        this.imagesList = ((TradiuusApp) getApplicationContext()).imagesList;
        mTechnicianImpl = new TechnicianImpl();
        currentTechnician = new Technician();
        mTechnicianImpl.init(context, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        RegisterActivities.removeTop();
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        listTechnician = (ListView) findViewById(R.id.listTechnician);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setText("Submit");

        CustomTextView emrg_title = (CustomTextView) findViewById(R.id.emrg_title);
        emrg_title.setTextSize(10);
        emrg_title.setTypeface(Typeface.createFromAsset(context.getAssets(), getString(R.string.cond)), Typeface.NORMAL);
        emrg_title.setText("Review TRADIUUS Pro and select one more Emergency Service");
        ((CustomTextView) findViewById(R.id.charges_for_emergency)).setText("Contractor response time may vary from 30 mins to 2 hr.");

        technician_map = (ImageView) findViewById(R.id.technician_map);
        technician_map.setOnClickListener(this);
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void initUIListener() {
        contractor_dashboard_back.setOnClickListener(this);
        contractor_dashboard_logut.setOnClickListener(this);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.technician_map: {
                try {
                    isSelected = !isSelected;

                    if (isSelected) {
                        technician_map.setImageDrawable(getResources().getDrawable(R.drawable.icon_plum));
                        mapLayout.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mMoreTradeAdapter != null && mMoreTradeAdapter.getList() != null) {
                                        addMarkerInMap(new ArrayList<Technician>(mMoreTradeAdapter.getList()));
                                    }
                                    Log.d("isSelected","isSelected:"+mMoreTradeAdapter.getList().get(0));
                                    Technician mTechnician = mMoreTradeAdapter.getList().get(0);
                                    LatLng markerP = new LatLng(Double.valueOf(mTechnician.getTechnician_latitude()), Double.valueOf(mTechnician.getTechnician_longitude()));
                                    locateCamera(markerP);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 500);
                    } else {
                        technician_map.setImageDrawable(getResources().getDrawable(R.drawable.map_icon_blue));
                        mapLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.contractor_dashboard_logut:
                if (selected == null) {
                    Utility.uiThreadAlert(context, context.getString(R.string.app_name), "Oops, You forgot to select a technician");
                    //onJobAssignedSuccess(null);
                    return;
                }

                try {
                    JSONObject details = new JSONObject();
                    details.put("trade_id", selectedTrade.getTrade_id());
                    details.put("service_type", "1");
                    if (imagesList != null && imagesList.size() > 0) {
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < imagesList.size(); i++) {
                            array.put(i, imagesList.get(i));
                        }
                        details.put("images", array);
                    } else {
                        details.put("images", new JSONArray());
                    }

                    JSONObject serviceObject = new JSONObject();
                    serviceObject.put("service_id", mService.getService_id());
                    JSONArray jsonArray = new JSONArray();
                    try {
                        for (int i = 0; i < qsList.size(); i++) {
                            Question question = qsList.get(i);
                            JSONObject questionObject = new JSONObject();
                            questionObject.put("question_id", question.getQuestion_id());
                            questionObject.put("option_id", question.getOption_id());
                            jsonArray.put(i, questionObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    serviceObject.put("service_question", jsonArray);

                    details.put("service", serviceObject);

                    mTechnicianImpl.assignJob(customer.getUser_id(), customer.getCustomer_id(), selected.getTechnician_id(), String.valueOf(false), selected.getContractor_details().getContractor_id(), details.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            mMoreTradeAdapter = new MoreTradeAdapter(context, new ArrayList<Technician>());
            listTechnician.setAdapter(mMoreTradeAdapter);

            // String user_id, String customer_id, String service_id, String service_type, String technician_id,  String latitude, String longitude, String distance
            mTechnicianImpl.getAvailableTechnician(customer.getUser_id(), customer.getCustomer_id(), mService.getService_id(),
                    mService.getService_type_id(), "", customer.getLat(), customer.getLng(), customer.getMax_distance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTechnicianFundSuccess(final ArrayList<Technician> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMoreTradeAdapter.refreshList(list);
            }
        });
    }

    @Override
    public void onJobAssignedSuccess(final Job job) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("AssignedSuccess -> ", job.getJob_id());
                ((TradiuusApp) getApplication()).job = job;
                startActivity(new Intent(context, CustomerEmergencyReqDetailActivity.class));
                overridePendingTransition(0, 0);
                RegisterActivities.removeAllActivities();
            }
        });
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MoreTradeAdapter extends BaseAdapter {

        private List<Technician> items;
        private Context context;
        private LayoutInflater inflater;
        private int lastIndex = -1;
        private DisplayImageOptions options;

        public MoreTradeAdapter(Context context, List<Technician> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
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
        }

        public void refreshList(List<Technician> newList) {
            if (newList != null && newList.size() > 0) {
                items.clear();
                items.addAll(newList);
                notifyDataSetChanged();
            }
        }

        public List<Technician> getList() {
            return items;
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
                convertView = inflater.inflate(R.layout.inflate_customer_tech_emg_request_row, null);
                viewHolder.technician_call = (ImageView) convertView.findViewById(R.id.technician_call);
                viewHolder.technician_img = (ImageView) convertView.findViewById(R.id.technician_img);
                viewHolder.contractor_name = (TextView) convertView.findViewById(R.id.contractor_name);
                viewHolder.select = (LinearLayout) convertView.findViewById(R.id.estimate_left_select);
                viewHolder.ratting = (LinearLayout) convertView.findViewById(R.id.ratting);
                viewHolder.technician_job = (TextView) convertView.findViewById(R.id.technician_job);
                viewHolder.technician_time = (TextView) convertView.findViewById(R.id.technician_time);
                viewHolder.technician_cost = (TextView) convertView.findViewById(R.id.technician_cost);
                viewHolder.deselect = (LinearLayout) convertView.findViewById(R.id.estimate_left);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.contractor_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDetails(items.get(position));
                }
            });


            if (items.get(position).isExpand) {
                viewHolder.select.setVisibility(View.VISIBLE);
                viewHolder.deselect.setVisibility(View.GONE);
            } else {
                viewHolder.select.setVisibility(View.GONE);
                viewHolder.deselect.setVisibility(View.VISIBLE);
            }

            try {
                Technician mTechnician = items.get(position);
                currentTechnician = mTechnician;
                viewHolder.contractor_name.setText(mTechnician.getContractor_details().getCompany_name());
                viewHolder.technician_job.setText(mTechnician.getContractor_details().getGeo_jobs().size() + "");
                viewHolder.technician_time.setText(mTechnician.getContractor_details().getEmergency_auto_response_time() + " mins");
                viewHolder.technician_cost.setText("$" + mTechnician.getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min() + " - $"
                        + mTechnician.getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max());
                int ratting = Integer.valueOf(mTechnician.getContractor_details().getOverall_rating());
                for (int i = 0; i < 5; i++) {
                    try {
                        if (ratting == 0) {
                            ((ImageView) viewHolder.ratting.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.ratingstargray));
                        } else {
                            ((ImageView) viewHolder.ratting.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.ratingstaryellow_2));
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
                ImageLoader.getInstance().displayImage(mTechnician.getTechnician_profile(), viewHolder.technician_img, options);
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.get(position).isExpand = false;
                    lastIndex = position;
                    selected = null;
                    notifyDataSetChanged();
                }
            });
            viewHolder.deselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastIndex > 0) {
                        items.get(lastIndex).isExpand = false;
                    }
                    items.get(position).isExpand = true;
                    lastIndex = position;
                    selected = items.get(position);
                    notifyDataSetChanged();
                }
            });
            viewHolder.technician_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Log.d("Items:","itemsSize="+currentTechnician.getTechnician_mobile());
                       // Log.d("Tech_Mobile",""+items.get(position).getTechnician_mobile()+"||"+mTech.getEmail());

//                        Intent intent = new Intent(Intent.ACTION_DIAL);
//                        intent.setData(Uri.parse("tel:" + items.get(position).getTechnician_mobile()));
//                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            viewHolder.technician_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        CustomImageViewerDialog imageViewerDialog = new CustomImageViewerDialog(context);
                        imageViewerDialog.prepareAndShowDialog(items.get(position).getTechnician_profile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }


        private class ViewHolder {
            public TextView contractor_name;
            public TextView technician_job;
            public TextView technician_time;
            public TextView technician_cost;
            public ImageView technician_call, technician_img;
            public LinearLayout select, ratting;
            public LinearLayout deselect;
        }

    }

    private void loadDetails(Technician mTechnician) {
        try {
            ((TradiuusApp) context.getApplicationContext()).mTechnician = mTechnician;
            startActivity(new Intent(context, CustomerContractorDetailActivity.class));
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Technician> mapTechnician = new HashMap<>();
    private HashMap<String, Marker> mapMarker = new HashMap<>();

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

    private void locateCamera(LatLng latLng) {
        try {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(10.5f)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                        //   Log.d("Trade_Id","Trade_Id"+mConfigData.getTrades());
//                        for (Trade trade : mConfigData.getTrades()) {
//                            if (mTechnician.getTrade_id().equalsIgnoreCase(trade.getTrade_id())) {
//                                OnMapImageUrl = trade.getOn_map_image();
//                                break;
//                            }
//                        }
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
}