package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Color;
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

import com.tradiuus.R;
import com.tradiuus.activities.ContractorLuxuryBuildingsActivity;
import com.tradiuus.models.Job;
import com.tradiuus.models.Contractor;

import java.util.ArrayList;
import java.util.List;

public class ContractorLuxuryBuildingListFragment extends Fragment implements View.OnClickListener {
    public static ContractorLuxuryBuildingListFragment fragment;
    private Context context;
    public Contractor mContractor;
    public ListView req_list;
    public TextView no_data;
    public LuxuryBuildingsAdapter mEmergencyReqAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_luxury_list, null);
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
            case R.id.btn_Add:
                ((ContractorLuxuryBuildingsActivity) getActivity()).changeTitle();
                ((ContractorLuxuryBuildingsActivity) getActivity()).replaceFragment(new ContractorLuxuryBuildingAddFragment());
                break;

        }
    }

    private void initView(View rootView) {
        req_list = (ListView) rootView.findViewById(R.id.req_list);
        no_data = (TextView) rootView.findViewById(R.id.no_data);
        ((TextView) rootView.findViewById(R.id.btn_Add)).setOnClickListener(this);
    }

    private void initViewLoad() {
        mContractor = ((ContractorLuxuryBuildingsActivity) getActivity()).contractorImpl.getmContractor();
        mEmergencyReqAdapter = new LuxuryBuildingsAdapter(context, new ArrayList<Job>());
        req_list.setAdapter(mEmergencyReqAdapter);
    }

    public void noData() {
        try {
            no_data.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDataValue(ArrayList<Job> jobList) {
        try {
            mEmergencyReqAdapter.updateList(jobList);
            no_data.setVisibility(((jobList == null || jobList.size() == 0)) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LuxuryBuildingsAdapter extends BaseAdapter {

        private List<Job> items;
        private Context context;
        private LayoutInflater inflater;

        public LuxuryBuildingsAdapter(Context context, List<Job> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public void updateList(List<Job> items) {
            try {
                this.items.clear();
                this.items = items;
                notifyDataSetChanged();
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
                convertView = inflater.inflate(R.layout.inflate_luxury_list_row, null);
                viewHolder.service_name = (TextView) convertView.findViewById(R.id.service_name);
                viewHolder.service_estimated = (TextView) convertView.findViewById(R.id.service_estimated);
                viewHolder.service_job_id = (TextView) convertView.findViewById(R.id.service_job_id);
                viewHolder.service_time_line = (TextView) convertView.findViewById(R.id.service_time_line);
                viewHolder.rating_layout = (LinearLayout) convertView.findViewById(R.id.rating_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Job jobDetail = items.get(position);

            try {
                viewHolder.service_name.setText(jobDetail.getPlace_details().getAddress());
                viewHolder.service_job_id.setText("Jon ID: " + jobDetail.getJob_name());
                //viewHolder.service_time_line.setText(jobDetail.getPlace_details().getAddress());
                if (jobDetail.getService_type().equalsIgnoreCase("1")) {
                    viewHolder.service_estimated.setText("Emergency Services: " + jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());
                    viewHolder.service_estimated.setTextColor(Color.parseColor("#F86A54"));
                } else {
                    viewHolder.service_estimated.setText("Estimate Services: " + jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());
                    viewHolder.service_estimated.setTextColor(Color.parseColor("#38C7B5"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                int rating = Integer.valueOf(jobDetail.getJob_rating());
                for (int i = 0; i < 5; i++) {
                    ImageView ratingV = new ImageView(context);
                    ratingV.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
                    ratingV.setImageDrawable(getResources().getDrawable(R.drawable.overall_rating));
                    if (rating > 0 && i < rating) {
                        ratingV.setImageDrawable(context.getResources().getDrawable(R.drawable.ratingstaryellow_2));
                    } else {
                        ratingV.setImageDrawable(context.getResources().getDrawable(R.drawable.ratingstargray));
                    }
                    viewHolder.rating_layout.addView(ratingV);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return convertView;
        }


        private class ViewHolder {
            public TextView service_name;
            public TextView service_estimated;
            public TextView service_job_id;
            public TextView service_time_line;
            public LinearLayout rating_layout;
        }

    }
}
