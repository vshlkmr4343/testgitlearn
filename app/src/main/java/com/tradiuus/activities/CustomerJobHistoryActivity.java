package com.tradiuus.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Job;
import com.tradiuus.models.Trade;
import com.tradiuus.widgets.CustomImageViewerDialog;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;


public class CustomerJobHistoryActivity extends BaseActivity {

    public Trade selectedTrade;
    private Context context;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;

    public ArrayList<Job> emergencyJobs = new ArrayList<>();
    public ArrayList<Job> estimateJobs = new ArrayList<>();

    private ListView emergencyList;
    private ListView estimatedList;

    private EmergencyJobAdapter mEmergencyJobAdapter;
    private EstimatedJobAdapter mEstimatedJobAdapter;

    private LinearLayout emergency_btn;
    private ImageView emergency_btn_icon;
    private TextView emergency_btn_title;
    private LinearLayout estimate_btn;
    private ImageView estimate_btn_icon;
    private TextView estimate_btn_title;

    private boolean selectedState = true;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_customer_job_history);
        this.context = CustomerJobHistoryActivity.this;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);
        contractor_dashboard_logut.setText("Next");

        emergencyList = (ListView) findViewById(R.id.emergencyList);
        estimatedList = (ListView) findViewById(R.id.estimatedList);

        emergency_btn = (LinearLayout) findViewById(R.id.emergency_btn);
        emergency_btn_icon = (ImageView) findViewById(R.id.emergency_btn_icon);
        emergency_btn_title = (TextView) findViewById(R.id.emergency_btn_title);

        estimate_btn = (LinearLayout) findViewById(R.id.estimate_btn);
        estimate_btn_icon = (ImageView) findViewById(R.id.estimate_btn_icon);
        estimate_btn_title = (TextView) findViewById(R.id.estimate_btn_title);
    }

    @Override
    protected void initUIListener() {
        contractor_dashboard_back.setOnClickListener(this);
        contractor_dashboard_logut.setOnClickListener(this);

        ((LinearLayout) findViewById(R.id.emergency_btn)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.estimate_btn)).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.contractor_dashboard_logut:
                break;
            case R.id.emergency_btn:
                if (!selectedState) {
                    selectTab(true);
                    selectedState = true;
                }
                break;
            case R.id.estimate_btn:
                if (selectedState) {
                    selectTab(false);
                    selectedState = false;
                }
                break;
        }
    }

    @Override
    protected void initLoadCall() {
    }

    private void loadJobs() {
        emergencyJobs = ((TradiuusApp) getApplication()).emergencyJobList;
        estimateJobs = ((TradiuusApp) getApplication()).estimateJobList;

        mEmergencyJobAdapter = new EmergencyJobAdapter(context, emergencyJobs);
        emergencyList.setAdapter(mEmergencyJobAdapter);

        mEstimatedJobAdapter = new EstimatedJobAdapter(context, estimateJobs);
        estimatedList.setAdapter(mEstimatedJobAdapter);

        selectTab(true);
    }

    private void selectTab(boolean isEmergencySelected) { //38C7B5  red F86A54
        if (isEmergencySelected) {
            emergency_btn.setBackgroundColor(Color.parseColor("#F86A54"));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) emergency_btn_icon).getDrawable()), Color.WHITE);
            emergency_btn_title.setTextColor(Color.WHITE);

            estimate_btn.setBackground(getResources().getDrawable(R.drawable.estimate_rec));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) estimate_btn_icon).getDrawable()), Color.parseColor("#38C7B5"));
            estimate_btn_title.setTextColor(Color.parseColor("#38C7B5"));

            emergencyList.setVisibility(View.VISIBLE);
            estimatedList.setVisibility(View.GONE);

        } else {
            emergency_btn.setBackground(getResources().getDrawable(R.drawable.emergency_rec));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) emergency_btn_icon).getDrawable()), Color.parseColor("#F86A54"));
            emergency_btn_title.setTextColor(Color.parseColor("#F86A54"));

            estimate_btn.setBackgroundColor(Color.parseColor("#38C7B5"));
            DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) estimate_btn_icon).getDrawable()), Color.WHITE);
            estimate_btn_title.setTextColor(Color.WHITE);

            emergencyList.setVisibility(View.GONE);
            estimatedList.setVisibility(View.VISIBLE);
        }
    }

    private class EmergencyJobAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<Job> jobs = new ArrayList<>();
        private DisplayImageOptions options;

        public EmergencyJobAdapter(Context context, ArrayList<Job> jobs) {
            this.jobs = jobs;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.inflate_customer_emg_job_row, null);
                holder.technician_img = (ImageView) convertView.findViewById(R.id.technician_img);
                holder.cancel_section = (LinearLayout) convertView.findViewById(R.id.cancel_section);
                holder.contractor_name = (TextView) convertView.findViewById(R.id.contractor_name);
                holder.contractor_job = (TextView) convertView.findViewById(R.id.technician_job);
                holder.contractor_time = (TextView) convertView.findViewById(R.id.technician_time);
                holder.contractor_cost = (TextView) convertView.findViewById(R.id.technician_cost);
                holder.jobStatus = (TextView) convertView.findViewById(R.id.jobStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Job job = jobs.get(position);

            try {
                holder.jobStatus.setText(Utility.getStatus(job.getJob_status()));
                holder.contractor_name.setText(job.getTechnician().get(0).getContractor_details().getCompany_name());
                holder.contractor_job.setText(job.getTechnician().get(0).getContractor_details().getGeo_jobs().size() + "");
                holder.contractor_time.setText(job.getTechnician().get(0).getContractor_details().getEmergency_auto_response_time() + " mins");
                holder.contractor_cost.setText("$" + job.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min()
                        + " - $" + job.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max());
                ImageLoader.getInstance().displayImage(job.getTechnician().get(0).getTechnician_profile(), holder.technician_img, options);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.cancel_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((TradiuusApp) context.getApplicationContext()).job = jobs.get(position);
                        startActivity(new Intent(context, CustomerEmergencyReqDetailActivity.class));
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                holder.technician_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CustomImageViewerDialog imageViewerDialog = new CustomImageViewerDialog(context);
                            imageViewerDialog.prepareAndShowDialog(jobs.get(position).getTechnician().get(0).getTechnician_profile());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            LinearLayout cancel_section;
            TextView contractor_name;
            TextView contractor_job;
            TextView contractor_time;
            TextView contractor_cost;
            TextView jobStatus;
            ImageView technician_img;
        }
    }

    private class EstimatedJobAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<Job> jobs = new ArrayList<>();
        private DisplayImageOptions options;

        public EstimatedJobAdapter(Context context, ArrayList<Job> jobs) {
            this.jobs = jobs;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.inflate_customer_emg_job_row, null);
                holder.technician_img = (ImageView) convertView.findViewById(R.id.technician_img);
                holder.cancel_section = (LinearLayout) convertView.findViewById(R.id.cancel_section);
                holder.contractor_name = (TextView) convertView.findViewById(R.id.contractor_name);
                holder.contractor_job = (TextView) convertView.findViewById(R.id.technician_job);
                holder.contractor_time = (TextView) convertView.findViewById(R.id.technician_time);
                holder.contractor_cost = (TextView) convertView.findViewById(R.id.technician_cost);
                holder.jobStatus = (TextView) convertView.findViewById(R.id.jobStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Job job = jobs.get(position);

            try {
                holder.jobStatus.setText(Utility.getStatus(job.getJob_status()));
                holder.contractor_name.setText(job.getTechnician().get(0).getContractor_details().getCompany_name());
                holder.contractor_job.setText(job.getTechnician().get(0).getContractor_details().getGeo_jobs().size() + "");
                holder.contractor_time.setText(job.getTechnician().get(0).getContractor_details().getEmergency_auto_response_time() + " mins");
                holder.contractor_cost.setText("$" + job.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min()
                        + " - $" + job.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max());
                ImageLoader.getInstance().displayImage(job.getTechnician().get(0).getTechnician_profile(), holder.technician_img, options);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.cancel_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((TradiuusApp) context.getApplicationContext()).job = jobs.get(position);
                        startActivity(new Intent(context, CustomerEstimateReqDetailActivity.class));
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                holder.technician_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CustomImageViewerDialog imageViewerDialog = new CustomImageViewerDialog(context);
                            imageViewerDialog.prepareAndShowDialog(jobs.get(position).getTechnician().get(0).getTechnician_profile());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            LinearLayout cancel_section;
            TextView contractor_name;
            TextView contractor_job;
            TextView contractor_time;
            TextView contractor_cost;
            TextView jobStatus;
            ImageView technician_img;
        }
    }
}