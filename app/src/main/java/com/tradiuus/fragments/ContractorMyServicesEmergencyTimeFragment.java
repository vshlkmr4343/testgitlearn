package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorMyServicesActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ContractorMyServicesEmergencyTimeFragment extends Fragment implements View.OnClickListener {
    private Context context;
    public Contractor mContractor;
    private CustomTextView emergency_text, emergency_edit_time;
    private int timeInterval = 30;
    private String setTime = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_myservice_emergency_time, null);
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
            case R.id.estimate_btn_update:
                try {
                    if (TextUtils.isEmpty(setTime)) {
                        return;
                    }
                    JSONObject memberData = new JSONObject();
                    memberData.put("emergency_auto_response_time", setTime);
                    ((ContractorMyServicesActivity) getActivity()).updateTime(memberData.toString(), "emergencyAutoResponse");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.estimate_btn_posetive:
                calculateTime(true);
                break;

            case R.id.estimate_btn_negetive:
                calculateTime(false);
                break;
        }
    }

    private void initView(View rootView) {
        emergency_text = (CustomTextView) rootView.findViewById(R.id.estimate_text);
        emergency_edit_time = (CustomTextView) rootView.findViewById(R.id.estimate_edit_time);
        ((CustomTextView) rootView.findViewById(R.id.estimate_btn_update)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.estimate_btn_negetive)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.estimate_btn_posetive)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.timeInfo)).setVisibility(View.GONE);
        ((LinearLayout) rootView.findViewById(R.id.parent)).setOnClickListener(this);
    }

    private void initViewLoad() {
        mContractor = ((ContractorMyServicesActivity) getActivity()).contractorImpl.getmContractor();
        updateTime(Integer.valueOf(mContractor.getEmergency_auto_response_time()));
    }

    private void calculateTime(boolean plus) {
        try {
            String time = new String(setTime);
            int timeIn = Integer.valueOf(time);
            if (plus) {
                timeIn = timeIn + timeInterval;
            } else {
                if (timeIn == timeInterval || timeIn < timeInterval) {
                    return;
                }
                timeIn = timeIn - timeInterval;
            }
            updateTime(timeIn);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateTime(int time) {
        try {
            setTime = String.valueOf(time);
            String showTime = Utility.convertTimeToHr(time);
            emergency_edit_time.setText(showTime);
            emergency_text.setText("Hi, I'll be available in " + showTime + " to resolve your problem. Thanks and I'll see you soon.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
