package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.CustomerEmergencyReqDetailActivity;
import com.tradiuus.activities.CustomerEstimateReqDetailActivity;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Image;
import com.tradiuus.models.Reason;

import java.util.ArrayList;
import java.util.List;

public class CustomerReqCancelFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ListView list_option;
    private Reason currentReason;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inflate_req_cancel_dialog, null);
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
            case R.id.confirm:
                try {
                    if (currentReason != null) {
                        try {
                            ((CustomerEmergencyReqDetailActivity) getActivity()).submitReason(currentReason);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            ((CustomerEstimateReqDetailActivity) getActivity()).submitReason(currentReason);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.reconsider:
                try {
                    if (currentReason != null) {
                        try {
                            ((CustomerEmergencyReqDetailActivity) getActivity()).reConsider(currentReason);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            ((CustomerEstimateReqDetailActivity) getActivity()).reConsider(currentReason);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_cross:
                try {
                    ((CustomerEmergencyReqDetailActivity) getActivity()).onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ((CustomerEstimateReqDetailActivity) getActivity()).onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initView(View rootView) {
        list_option = (ListView) rootView.findViewById(R.id.list_option);

        ((TextView) rootView.findViewById(R.id.confirm)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.reconsider)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.action_cross)).setOnClickListener(this);
    }

    public void initViewLoad() {
        ConfigData mConfigData = ((TradiuusApp) getActivity().getApplicationContext()).mConfigData;
        ArrayList<Reason> reasons = new ArrayList<Reason>();
        try {
            for (Reason mReason : mConfigData.getReasonList()) {
                reasons.add(new Reason(mReason.getReason_id(), mReason.getReason(), mReason.getDisplay_order(), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReasonAdapter mReasonAdapter = new ReasonAdapter(context, reasons);
        list_option.setAdapter(mReasonAdapter);
    }

    public class ReasonAdapter extends BaseAdapter {

        private List<Reason> items;
        private Context context;
        private LayoutInflater inflater;
        private int lastSelectedIndex = -1;

        public ReasonAdapter(Context context, List<Reason> items) {
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
                convertView = inflater.inflate(R.layout.inflate_trade_row, null);
                viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.trade_row_radio);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.trade_row_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Reason mReason = items.get(position);
            viewHolder.checkBox.setSelected(mReason.isSelected);
            viewHolder.txtName.setText(mReason.getReason());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastSelectedIndex >= 0) {
                        items.get(lastSelectedIndex).isSelected = false;
                    }
                    currentReason = items.get(position);
                    items.get(position).isSelected = (items.get(position).isSelected) ? false : true;
                    lastSelectedIndex = position;
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            public TextView txtName;
            public ImageView checkBox;
        }

    }
}
