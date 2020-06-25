package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.os.HandlerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.TechnicianEmergencyRequestActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Job;
import com.tradiuus.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class TechnicianEmergencyReqListFragment extends Fragment implements View.OnClickListener {
    public static TechnicianEmergencyReqListFragment fragment;
    private Context context;
    public Technician mTechnician;
    public ListView req_list;
    private TextView no_items;
    private EmergencyReqAdapter mEmergencyReqAdapter;
    private ArrayList<Job> emergencyJobList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_technician_emergency_req_list, null);
        this.context = getActivity();
        initView(rootView);
        initViewLoad();
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
        }
    }

    private void initView(View rootView) {
        req_list = (ListView) rootView.findViewById(R.id.req_list);
        no_items = (TextView) rootView.findViewById(R.id.no_items);
    }

    private void initViewLoad() {
        emergencyJobList = ((TradiuusApp) context.getApplicationContext()).emergencyJobList;
        mTechnician = ((TechnicianEmergencyRequestActivity) getActivity()).mTechnician;
        mEmergencyReqAdapter = new EmergencyReqAdapter(context, emergencyJobList);
        req_list.setAdapter(mEmergencyReqAdapter);
        if (emergencyJobList == null || emergencyJobList.size() == 0) {
            no_items.setVisibility(View.VISIBLE);
        }
    }

    public void refreshUI() {
        try {
            emergencyJobList = ((TradiuusApp) context.getApplicationContext()).emergencyJobList;
            mTechnician = ((TechnicianEmergencyRequestActivity) getActivity()).mTechnician;
            mEmergencyReqAdapter.refresh(emergencyJobList);
            if (emergencyJobList == null || emergencyJobList.size() == 0) {
                no_items.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class EmergencyReqAdapter extends BaseAdapter {

        private List<Job> items;
        private Context context;
        private LayoutInflater inflater;
        private DisplayImageOptions options;

        public EmergencyReqAdapter(Context context, List<Job> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.options = new DisplayImageOptions.Builder()
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

        private void refresh(List<Job> items) {
            try {
                if (items != null) {
                    this.items = new ArrayList<>();
                    this.items.addAll(items);
                } else {
                    this.items = new ArrayList<>();
                }
                notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        req_list.smoothScrollToPosition(0);
                    }
                },200);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                convertView = inflater.inflate(R.layout.inflate_emergency_req_list_row, null);
                viewHolder.service_logo = (ImageView) convertView.findViewById(R.id.service_logo);
                viewHolder.status_icon = (ImageView) convertView.findViewById(R.id.status_icon);
                viewHolder.linear_technician = (LinearLayout) convertView.findViewById(R.id.linear_technician);
                viewHolder.service_name = (TextView) convertView.findViewById(R.id.service_name);
                viewHolder.service_job_id = (TextView) convertView.findViewById(R.id.service_job_id);
                viewHolder.service_posted_by = (TextView) convertView.findViewById(R.id.service_posted_by);
                viewHolder.service_booked = (TextView) convertView.findViewById(R.id.service_booked);
                viewHolder.service_work_status = (TextView) convertView.findViewById(R.id.service_work_status);
                viewHolder.service_mark_as_completed = (TextView) convertView.findViewById(R.id.service_mark_as_completed);
                viewHolder.service_ph_no = (TextView) convertView.findViewById(R.id.service_ph_no);
                viewHolder.status_card = (LinearLayout) convertView.findViewById(R.id.status_card);
                viewHolder.service_cost = (TextView) convertView.findViewById(R.id.service_cost);
                viewHolder.service_rating_icon = (ImageView) convertView.findViewById(R.id.service_rating_icon);
                viewHolder.service_rating = (TextView) convertView.findViewById(R.id.service_rating);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                Job jobDetail = items.get(position);

                viewHolder.linear_technician.setVisibility(View.GONE);

                viewHolder.service_name.setText(jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());
                viewHolder.service_job_id.setText("Job Id: " + jobDetail.getJob_name());
                viewHolder.service_posted_by.setText("Posted By: " + jobDetail.getCustomer_details().getFirst_name() + " " + jobDetail.getCustomer_details().getLast_name());
                viewHolder.service_ph_no.setText(jobDetail.getCustomer_details().getMobile_number());
                viewHolder.service_booked.setText(Utility.getDateTime(jobDetail.getCreated_on()));
                viewHolder.service_work_status.setText(Utility.getStatusMd2(jobDetail.getJob_status(), jobDetail.getUpdated_on()));

                viewHolder.status_card.setBackgroundColor(Color.parseColor(Utility.getStatusColor(jobDetail.getJob_status())));
                viewHolder.service_mark_as_completed.setText(Utility.getStatusCard(jobDetail.getJob_status()));
                viewHolder.status_icon.setImageDrawable(getResources().getDrawable(Utility.getStatusIcon(jobDetail.getJob_status())));
                Log.d("Technician","service_cost:"+jobDetail.getJob_cost());
                viewHolder.service_cost.setText(jobDetail.getJob_cost());
                viewHolder.service_rating_icon.setImageDrawable(getResources().getDrawable(Utility.getRatingIcon(jobDetail.getJob_rating())));
                viewHolder.service_rating.setText(jobDetail.getJob_rating());

            } catch (Exception e) {
                e.printStackTrace();
            }
            viewHolder.status_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (items.get(position).getJob_status().equalsIgnoreCase("1")) {
                        Utility.uiThreadAlert(context, "Confirmation", "Do you want to mark as completed ? ", "Yes", "No", new Utility.OnDialogMultiButtonClick() {
                            @Override
                            public void onOkayButtonClick() {
                                try {
                                    ((TechnicianEmergencyRequestActivity) getActivity()).completeJob(items.get(position), position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelButtonClick() {

                            }
                        });
                    }
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((TradiuusApp) context.getApplicationContext()).jobDetail = items.get(position);
                        ((TechnicianEmergencyRequestActivity) getActivity()).selectedIndex = position;
                        ((TechnicianEmergencyRequestActivity) getActivity()).replaceFragment(new TechnicianEmergencyReqListDetailsFragment());
                        ((TechnicianEmergencyRequestActivity) getActivity()).HomeShowHide(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }


        private class ViewHolder {
            public ImageView service_logo;
            public ImageView status_icon;
            public LinearLayout linear_technician;
            public TextView service_name;
            public TextView service_job_id;
            public TextView service_posted_by;
            public TextView service_booked;
            public TextView service_work_status;
            public TextView service_mark_as_completed;
            public TextView service_ph_no;
            public LinearLayout status_card;
            public TextView service_cost;
            public ImageView service_rating_icon;
            public TextView service_rating;

        }
    }

    public void updateEmergencyJob() {
        try {
            emergencyJobList = ((TradiuusApp) context.getApplicationContext()).emergencyJobList;
            mEmergencyReqAdapter.refresh(emergencyJobList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
