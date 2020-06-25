package com.tradiuus.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.TechnicianLuxuryBuildingsActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Image;
import com.tradiuus.models.Job;
import com.tradiuus.models.LuxuryData;
import com.tradiuus.models.Technician;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TechnicianLuxuryBuildingAddFragment extends Fragment implements View.OnClickListener {
    private Context context;
    public Technician mTechnician;

    public ListView emergencyList;
    public ListView estimatedList;

    private LuxuryBuildingsAddAdapter mLuxuryBuildingEmergency;
    private LuxuryBuildingsAddAdapter mLuxuryBuildingsEstimate;

    private ArrayList<LuxuryData> emergencyLuxuryDataList = new ArrayList<>();
    private ArrayList<LuxuryData> estimateLuxuryDataList = new ArrayList<>();

    private LinearLayout emergency_btn;
    private ImageView emergency_btn_icon;
    private TextView emergency_btn_title;
    private LinearLayout estimate_btn;
    private ImageView estimate_btn_icon;
    private TextView estimate_btn_title;
    public TextView no_data;

    private boolean selectedState = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_technician_luxury_build_add, null);
        this.context = getActivity();
        initView(rootView);
        initViewLoad();
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
            case R.id.btn_Add: {
                try {
                    ArrayList<String> ids = new ArrayList<>();
                    for (LuxuryData mLuxuryData : emergencyLuxuryDataList) {
                        if (mLuxuryData.select) {
                            ids.add(mLuxuryData.getId());
                        }

                    }
                    for (LuxuryData mLuxuryData : estimateLuxuryDataList) {
                        if (mLuxuryData.select) {
                            ids.add(mLuxuryData.getId());
                        }
                    }
                    if (ids.size() == 0) {
                        Utility.uiThreadAlert(context, "Warning", "Please select Job before Done");
                        return;
                    }
                    ((TechnicianLuxuryBuildingsActivity) getActivity()).addJobs(ids);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            break;
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
        no_data = (TextView) rootView.findViewById(R.id.no_data);

        ((LinearLayout) rootView.findViewById(R.id.emergency_btn)).setOnClickListener(this);
        ((LinearLayout) rootView.findViewById(R.id.estimate_btn)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.btn_Add)).setOnClickListener(this);

    }

    private void initViewLoad() {
        try {
            mTechnician = ((TechnicianLuxuryBuildingsActivity) getActivity()).technicianImpl.mTechnician;

            ArrayList<Job> emergencyJobListAll = ((TradiuusApp) context.getApplicationContext()).emergencyJobList;
            ArrayList<Job> estimateJobListAll = ((TradiuusApp) context.getApplicationContext()).estimateJobList;
            HashMap<String, Job> map = ((TechnicianLuxuryBuildingsActivity) getActivity()).map;

            for (Job job : emergencyJobListAll) {
                if (job.getJob_status().equalsIgnoreCase("5") && !map.containsKey(job.getJob_id())) {
                    emergencyLuxuryDataList.add(new LuxuryData(job.getCustomer_details().getArea(), job.getJob_trade().getTrade_name(), job.getJob_name(), job.getJob_id(), job.getJob_rating(), false));
                }
            }
            for (Job job : estimateJobListAll) {
                if (job.getJob_status().equalsIgnoreCase("5") && !map.containsKey(job.getJob_id())) {
                    estimateLuxuryDataList.add(new LuxuryData(job.getCustomer_details().getArea(), job.getJob_trade().getTrade_name(), job.getJob_name(), job.getJob_id(), job.getJob_rating(), false));
                }
            }

            mLuxuryBuildingEmergency = new LuxuryBuildingsAddAdapter(context, emergencyLuxuryDataList);
            emergencyList.setAdapter(mLuxuryBuildingEmergency);

            mLuxuryBuildingsEstimate = new LuxuryBuildingsAddAdapter(context, estimateLuxuryDataList);
            estimatedList.setAdapter(mLuxuryBuildingsEstimate);

            selectTab(true);

            /*if (emergencyLuxuryDataList.size() == 0 && estimateLuxuryDataList.size() == 0) {
                Utility.uiThreadAlert(context, getActivity().getString(R.string.app_name), "No more Jobs available for addition");
            }*/
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

            no_data.setVisibility(((emergencyLuxuryDataList == null || emergencyLuxuryDataList.size() == 0)
                    || (estimateLuxuryDataList == null || estimateLuxuryDataList.size() == 0)) ? View.VISIBLE : View.GONE);

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private class LuxuryBuildingsAddAdapter extends BaseAdapter {

        private List<LuxuryData> items;
        private Context context;
        private LayoutInflater inflater;

        public LuxuryBuildingsAddAdapter(Context context, List<LuxuryData> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
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
                convertView = inflater.inflate(R.layout.inflate_luxury_add_list_row, null);
                viewHolder.service_name = (TextView) convertView.findViewById(R.id.service_name);
                viewHolder.service_job_desc = (TextView) convertView.findViewById(R.id.service_job_desc);
                viewHolder.service_job_id = (TextView) convertView.findViewById(R.id.service_job_id);
                viewHolder.rating = (LinearLayout) convertView.findViewById(R.id.rating);
                viewHolder.check_item = (ImageView) convertView.findViewById(R.id.check_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            LuxuryData mLuxuryData = items.get(position);

            viewHolder.service_name.setText(mLuxuryData.getLocation());
            viewHolder.service_job_desc.setText("Job: " + mLuxuryData.getDesc());
            viewHolder.service_job_id.setText("Job Id: " + mLuxuryData.getId());
            viewHolder.check_item.setSelected(mLuxuryData.isSelect());

            viewHolder.check_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.get(position).select = (items.get(position).select) ? false : true;
                    notifyDataSetChanged();
                }
            });

           /* convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
            return convertView;
        }


        private class ViewHolder {
            public TextView service_name;
            public TextView service_job_desc;
            public TextView service_job_id;
            public LinearLayout rating;
            public ImageView check_item;
        }

    }
}
