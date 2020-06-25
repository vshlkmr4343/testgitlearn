package com.tradiuus.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.tradiuus.activities.CustomerDashboardActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.AddressInfo;
import com.tradiuus.models.Customer;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Job;
import com.tradiuus.models.Technician;
import com.tradiuus.widgets.CustomImageViewerDialog;
import com.tradiuus.widgets.CustomProgressDialog;

import java.util.ArrayList;

public class CustomerHistoryFragment extends Fragment implements View.OnClickListener, CustomerImpl.OnCustomerDashboardListener {
    private Context context;
    private ListView emergencyList;
    private ListView estimatedList;

    private EmergencyJobAdapter mEmergencyJobAdapter;
    private EstimatedJobAdapter mEstimatedJobAdapter;
    private CustomProgressDialog pgDialog;
    private LinearLayout emergency_btn;
    private ImageView emergency_btn_icon;
    private TextView emergency_btn_title;
    private LinearLayout estimate_btn;
    private ImageView estimate_btn_icon;
    private TextView estimate_btn_title;

    public ArrayList<Job> emergencyJobs = new ArrayList<>();
    public ArrayList<Job> estimateJobs = new ArrayList<>();

    private boolean selectedState = true;
    public Customer customer;
    public CustomerImpl customerImpl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_history, null);
        this.context = getActivity();
        customer = ((TradiuusApp) context.getApplicationContext()).customer;
        customerImpl = new CustomerImpl();
        customerImpl.init(context, this);
        initView(rootView);
        initViewLoad();
        loadingHistory();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    public void onShowPgDialog() {
        try {
            pgDialog = new CustomProgressDialog(context);
            pgDialog.prepareAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onHidePgDialog() {
        try {
            ((CustomerDashboardActivity) getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog.dismissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView(View rootView) {

        emergencyList = (ListView) rootView.findViewById(R.id.emergencyList);
        estimatedList = (ListView) rootView.findViewById(R.id.estimatedList);

        emergency_btn = (LinearLayout) rootView.findViewById(R.id.emergency_btn);
        emergency_btn_icon = (ImageView) rootView.findViewById(R.id.emergency_btn_icon);
        emergency_btn_title = (TextView) rootView.findViewById(R.id.emergency_btn_title);

        estimate_btn = (LinearLayout) rootView.findViewById(R.id.estimate_btn);
        estimate_btn_icon = (ImageView) rootView.findViewById(R.id.estimate_btn_icon);
        estimate_btn_title = (TextView) rootView.findViewById(R.id.estimate_btn_title);

        ((LinearLayout) rootView.findViewById(R.id.emergency_btn)).setOnClickListener(this);
        ((LinearLayout) rootView.findViewById(R.id.estimate_btn)).setOnClickListener(this);
    }

    public void initViewLoad() {
        try {
            /*emergencyJobs = ((CustomerDashboardActivity) getActivity()).emergencyOnGoingJobsAll;
            estimateJobs = ((CustomerDashboardActivity) getActivity()).estimateOnGoingJobsAll;*/

            mEmergencyJobAdapter = new EmergencyJobAdapter(context, emergencyJobs);
            emergencyList.setAdapter(mEmergencyJobAdapter);

            mEstimatedJobAdapter = new EstimatedJobAdapter(context, estimateJobs);
            estimatedList.setAdapter(mEstimatedJobAdapter);

            selectTab(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadingHistory() {
        try {
            onShowPgDialog();
            customerImpl.getCustomerJobHistoryService(customer.getCustomer_id(), customer.getUser_type());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void jobHistory(final ArrayList<Job> emergencyJobs, final ArrayList<Job> estimateJobs) {
        try {
            ((CustomerDashboardActivity) getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEmergencyJobAdapter.refresh(emergencyJobs);
                    mEstimatedJobAdapter.refresh(estimateJobs);
                }
            });
            onHidePgDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectTab(boolean isEmergencySelected) { //38C7B5  red F86A54
        try {
            if (isEmergencySelected) {
                emergency_btn.setBackgroundColor(Color.parseColor("#F86A54"));
                emergency_btn_icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                emergency_btn_title.setTextColor(Color.WHITE);

                estimate_btn.setBackground(getResources().getDrawable(R.drawable.estimate_rec));
                estimate_btn_icon.setColorFilter(Color.parseColor("#38C7B5"), PorterDuff.Mode.SRC_ATOP);
                estimate_btn_title.setTextColor(Color.parseColor("#38C7B5"));

                emergencyList.setVisibility(View.VISIBLE);
                estimatedList.setVisibility(View.GONE);

            } else {
                emergency_btn.setBackground(getResources().getDrawable(R.drawable.emergency_rec));
                emergency_btn_icon.setColorFilter(Color.parseColor("#F86A54"), PorterDuff.Mode.SRC_ATOP);
                emergency_btn_title.setTextColor(Color.parseColor("#F86A54"));

                estimate_btn.setBackgroundColor(Color.parseColor("#38C7B5"));
                estimate_btn_icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                estimate_btn_title.setTextColor(Color.WHITE);

                emergencyList.setVisibility(View.GONE);
                estimatedList.setVisibility(View.VISIBLE);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
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
                    .build();
        }

        public void refresh(ArrayList<Job> jobs) {
            if (jobs != null && jobs.size() > 0) {
                this.jobs = new ArrayList<>();
                this.jobs.addAll(jobs);
                notifyDataSetChanged();
            }
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
                convertView = inflater.inflate(R.layout.inflate_customer_history_list_row, null);
                holder.service_logo = (ImageView) convertView.findViewById(R.id.service_logo);
                holder.service_name = (TextView) convertView.findViewById(R.id.service_name);
                holder.service_contractor_name = (TextView) convertView.findViewById(R.id.service_contractor_name);
                holder.service_job_id = (TextView) convertView.findViewById(R.id.service_job_id);
                holder.service_posted_by = (TextView) convertView.findViewById(R.id.service_posted_by);
                holder.service_booked = (TextView) convertView.findViewById(R.id.service_booked);
                holder.service_work_status = (TextView) convertView.findViewById(R.id.service_work_status);
                holder.service_mark_as_completed = (TextView) convertView.findViewById(R.id.service_mark_as_completed);
                holder.service_ph_no = (TextView) convertView.findViewById(R.id.service_ph_no);

                holder.status_icon = (ImageView) convertView.findViewById(R.id.status_icon);
                holder.status_card = (LinearLayout) convertView.findViewById(R.id.status_card);

                holder.service_cost = (TextView) convertView.findViewById(R.id.service_cost);
                holder.service_rating_icon = (ImageView) convertView.findViewById(R.id.service_rating_icon);
                holder.service_rating = (TextView) convertView.findViewById(R.id.service_rating);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Job jobDetail = jobs.get(position);

            try {
                holder.service_contractor_name.setText(jobDetail.getTechnician().get(0).getContractor_details().getContact_person());
                holder.service_name.setText(jobDetail.getTechnician().get(0).getContractor_details().getCompany_name());
                holder.service_job_id.setText("Job Id: " + jobDetail.getJob_name());
                holder.service_posted_by.setText(jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());
                holder.service_ph_no.setText(jobDetail.getTechnician().get(0).getTechnician_mobile());
                holder.service_booked.setText(Utility.getDateTime(jobDetail.getCreated_on()));
                holder.service_work_status.setText(Utility.getStatusMd2(jobDetail.getJob_status(), jobDetail.getUpdated_on()));

                holder.status_card.setBackgroundColor(Color.parseColor(Utility.getStatusColor(jobDetail.getJob_status())));
                holder.service_mark_as_completed.setText(Utility.getStatus(jobDetail.getJob_status()));
                holder.status_icon.setImageDrawable(getResources().getDrawable(Utility.getStatusIcon(jobDetail.getJob_status())));

                holder.service_cost.setText("$" + jobDetail.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min()
                        + " - $" + jobDetail.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max());
                holder.service_rating_icon.setImageDrawable(getResources().getDrawable(Utility.getRatingIcon(jobDetail.getJob_rating())));
                holder.service_rating.setText(jobDetail.getJob_rating());
                ImageLoader.getInstance().displayImage(jobDetail.getTechnician().get(0).getTechnician_profile(), holder.service_logo, options);
                holder.service_logo.setOnClickListener(new View.OnClickListener() {
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
            public TextView service_name, service_contractor_name;
            public TextView service_job_id;
            public TextView service_posted_by;
            public TextView service_booked;
            public TextView service_work_status;
            public TextView service_mark_as_completed;
            public TextView service_ph_no;
            public ImageView status_icon;
            public LinearLayout status_card;

            private TextView service_cost;
            private ImageView service_rating_icon, service_logo;
            private TextView service_rating;
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
                    .build();
        }

        public void refresh(ArrayList<Job> jobs) {
            if (jobs != null && jobs.size() > 0) {
                this.jobs = new ArrayList<>();
                this.jobs.addAll(jobs);
                notifyDataSetChanged();
            }
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
                convertView = inflater.inflate(R.layout.inflate_customer_history_list_row, null);
                holder.service_logo = (ImageView) convertView.findViewById(R.id.service_logo);
                holder.service_name = (TextView) convertView.findViewById(R.id.service_name);
                holder.service_contractor_name = (TextView) convertView.findViewById(R.id.service_contractor_name);
                holder.service_job_id = (TextView) convertView.findViewById(R.id.service_job_id);
                holder.service_posted_by = (TextView) convertView.findViewById(R.id.service_posted_by);
                holder.service_booked = (TextView) convertView.findViewById(R.id.service_booked);
                holder.service_work_status = (TextView) convertView.findViewById(R.id.service_work_status);
                holder.service_mark_as_completed = (TextView) convertView.findViewById(R.id.service_mark_as_completed);
                holder.service_ph_no = (TextView) convertView.findViewById(R.id.service_ph_no);

                holder.status_icon = (ImageView) convertView.findViewById(R.id.status_icon);
                holder.status_card = (LinearLayout) convertView.findViewById(R.id.status_card);

                holder.service_cost = (TextView) convertView.findViewById(R.id.service_cost);
                holder.service_rating_icon = (ImageView) convertView.findViewById(R.id.service_rating_icon);
                holder.service_rating = (TextView) convertView.findViewById(R.id.service_rating);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Job jobDetail = jobs.get(position);

            try {
                holder.service_contractor_name.setText(jobDetail.getTechnician().get(0).getContractor_details().getContact_person());
                holder.service_name.setText(jobDetail.getTechnician().get(0).getContractor_details().getCompany_name());
                holder.service_job_id.setText("Job Id: " + jobDetail.getJob_name());
                holder.service_posted_by.setText(jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());
                holder.service_ph_no.setText(jobDetail.getTechnician().get(0).getTechnician_mobile());
                holder.service_booked.setText(Utility.getDateTime(jobDetail.getCreated_on()));
                holder.service_work_status.setText(Utility.getStatusMd2(jobDetail.getJob_status(), jobDetail.getUpdated_on()));

                holder.status_card.setBackgroundColor(Color.parseColor(Utility.getStatusColor(jobDetail.getJob_status())));
                holder.service_mark_as_completed.setText(Utility.getStatus(jobDetail.getJob_status()));
                holder.status_icon.setImageDrawable(getResources().getDrawable(Utility.getStatusIcon(jobDetail.getJob_status())));

                holder.service_cost.setText("$" + jobDetail.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_min()
                        + " - $" + jobDetail.getTechnician().get(0).getContractor_details().getTrade().get(0).getEmergency_services().get(0).getPrice_max() + "$");
                holder.service_rating_icon.setImageDrawable(getResources().getDrawable(Utility.getRatingIcon(jobDetail.getJob_rating())));
                holder.service_rating.setText(jobDetail.getJob_rating());
                ImageLoader.getInstance().displayImage(jobDetail.getTechnician().get(0).getTechnician_profile(), holder.service_logo, options);
                holder.service_logo.setOnClickListener(new View.OnClickListener() {
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
            public TextView service_name, service_contractor_name;
            public TextView service_job_id;
            public TextView service_posted_by;
            public TextView service_booked;
            public TextView service_work_status;
            public TextView service_mark_as_completed;
            public TextView service_ph_no;
            public ImageView status_icon;
            public LinearLayout status_card;

            private TextView service_cost;
            private ImageView service_rating_icon, service_logo;
            private TextView service_rating;
        }
    }


    @Override
    public void foundTechnicians(ArrayList<Technician> technicians) {
    }


    @Override
    public void removeAllTech(String message) {
    }

    @Override
    public void locationUpdate(AddressInfo mAddressInfo, String message) {
    }

    @Override
    public void onPasswordUpdateSuccess(String message) {
    }


    @Override
    public void logoutSuccess(String message) {
    }

    @Override
    public void showAlert(String message) {
        onHidePgDialog();
    }
}
