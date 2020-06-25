package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorMyServicesActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Trade;
import com.tradiuus.widgets.CustomTextView;

public class ContractorMyServicesFragment extends Fragment implements View.OnClickListener {
    public static ContractorMyServicesFragment fragment;
    private Context context;
    private CustomTextView myservice_trade;
    private CustomTextView myservice_emergancy;
    private CustomTextView myservice_estimate;
    private CustomTextView myservice_response_time, myservice_estimate_response_time;
    public Contractor mContractor;
    private Trade mTradeSignIn;

    public static ContractorMyServicesFragment newInstance(Context context) {
        if (fragment == null)
            fragment = new ContractorMyServicesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_myservice, null);
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
            case R.id.contractor_myservice_btn_update_trade:
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.Trade, true);
                break;
            case R.id.contractor_myservice_btn_update_emergancy:
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.EmergencyService, true);
                break;
            case R.id.contractor_myservice_btn_update_estimate:
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.EstimateService, true);
                break;
            case R.id.contractor_myservice_btn_update_response_time:
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.EmergencyTime, true);
                break;
            case R.id.contractor_myservice_btn_update_estimate_response_time:
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.EstimateTime, true);
                break;
        }
    }

    private void initView(View rootView) {
        myservice_trade = (CustomTextView) rootView.findViewById(R.id.contractor_myservice_trade);
        myservice_emergancy = (CustomTextView) rootView.findViewById(R.id.contractor_myservice_emergancy);
        myservice_estimate = (CustomTextView) rootView.findViewById(R.id.contractor_myservice_estimate);
        myservice_response_time = (CustomTextView) rootView.findViewById(R.id.contractor_myservice_response_time);
        myservice_estimate_response_time = (CustomTextView) rootView.findViewById(R.id.contractor_myservice_estimate_response_time);

        ((CustomTextView) rootView.findViewById(R.id.contractor_myservice_btn_update_trade)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.contractor_myservice_btn_update_emergancy)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.contractor_myservice_btn_update_estimate)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.contractor_myservice_btn_update_response_time)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.contractor_myservice_btn_update_estimate_response_time)).setOnClickListener(this);
    }

    public void initViewLoad() {
        try {
            mContractor = ((ContractorMyServicesActivity) getActivity()).contractorImpl.getmContractor();
            mTradeSignIn = ((ContractorMyServicesActivity) getActivity()).mTradeSignIn;

            if (mTradeSignIn != null) {
                myservice_trade.setText("Selected Trade: " + mTradeSignIn.getTrade_name());
                myservice_emergancy.setText(mTradeSignIn.getEmergencyServices());
                myservice_estimate.setText(mTradeSignIn.getEstimateServices());
            }
            if (!TextUtils.isEmpty(mContractor.getEmergency_auto_response_time())) {
                myservice_response_time.setText("Emergency Response Time : " + Utility.convertTimeToHr_(Integer.valueOf(mContractor.getEmergency_auto_response_time())));
            } else {
                myservice_response_time.setText("Emergency Response Time : 0 min");
            }
            if (!TextUtils.isEmpty(mContractor.getEstimate_auto_response_time())) {
                myservice_estimate_response_time.setText("Estimate Response Time : " + Utility.convertTimeToHr_(Integer.valueOf(mContractor.getEstimate_auto_response_time())));
            } else {
                myservice_estimate_response_time.setText("Estimate Response Time : 0 min");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
