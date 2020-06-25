package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorSignupActivity;
import com.tradiuus.helper.Utility;

public class ContractorEstimatePriceFragment extends Fragment implements View.OnClickListener {

    private Context context;
    public static ContractorEstimatePriceFragment fragment;
    public ImageView fragment_condition;
    private EditText estimate_price_max;
    private EditText estimate_price_min;
    private EditText estimate_job_time_max;
    private EditText estimate_job_time_min;
    private boolean isChecked = true;

    public static ContractorEstimatePriceFragment newInstance(int position, Context context) {
        if (fragment == null)
            fragment = new ContractorEstimatePriceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_estimate_price, null);
        this.context = getActivity();
        initView(rootView);
        initLoad();
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
            case R.id.frag_estimate_btn_proceed:
                nextButtonEvent();
                break;
            case R.id.frag_estimate_btn_back:
                ((ContractorSignupActivity) getActivity()).backEvent();
                break;
        }
    }

    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.frag_estimate_btn_proceed)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.frag_estimate_btn_back)).setOnClickListener(this);

        estimate_price_max = (EditText) rootView.findViewById(R.id.estimate_price_max);
        estimate_price_min = (EditText) rootView.findViewById(R.id.estimate_price_min);
        estimate_job_time_max = (EditText) rootView.findViewById(R.id.estimate_job_time_max);
        estimate_job_time_min = (EditText) rootView.findViewById(R.id.estimate_job_time_min);
        fragment_condition = (ImageView) rootView.findViewById(R.id.fragment_condition);
    }

    private void initLoad() {
        fragment_condition.setSelected(true);
        fragment_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                fragment_condition.setSelected(isChecked);
            }
        });
    }

    private void nextButtonEvent() {
        String priceMax = estimate_price_max.getText().toString();
        String priceMin = estimate_price_min.getText().toString();
        String timeMax = estimate_job_time_max.getText().toString();
        String timeMin = estimate_job_time_min.getText().toString();
        if (TextUtils.isEmpty(priceMax)
                || TextUtils.isEmpty(priceMin)
                || TextUtils.isEmpty(timeMax)
                || TextUtils.isEmpty(timeMin)) {
            Utility.uiThreadAlert(context, "Please check all the field values before hit the next button");
            return;
        }
        if (!isChecked) {
            Utility.uiThreadAlert(context, "Please agree the terms and condition");
            return;
        }
        int totalTime = Integer.parseInt(timeMax) * 60 + Integer.parseInt(timeMin);
        ((ContractorSignupActivity) getActivity()).submitInitial(priceMax, priceMin, String.valueOf(totalTime));
    }
}