package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorMyServicesActivity;
import com.tradiuus.adapters.EmergencyServiceAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Trade;
import com.tradiuus.network.postparams.ServiceParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContractorMyServicesEmergencyFragment extends Fragment implements View.OnClickListener {
    private Context context;
    public Contractor mContractor;
    private Trade mTradeSignIn;
    public ListView list_emergency;
    private EmergencyServiceAdapter emergencyServiceAdapter;
    private boolean isFromTrade = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_myservice_emergency, null);
        this.context = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isFromTrade = bundle.getBoolean("isFromTrade", false);
        }
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
            case R.id.myService_trade_btn_next:
                ((ContractorMyServicesActivity) getActivity()).services = emergencyServiceAdapter.items;
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.EstimateService, false);
                break;
            case R.id.myService_trade_btn_update:
                if (emergencyServiceAdapter.items != null) {
                    boolean priceAndTimeNotSet = false;
                    List<ServiceParam> listServiceParam = new ArrayList<>();
                    for (ServiceParam serviceParam : emergencyServiceAdapter.items) {
                        if (serviceParam.isSelected == 1) {
                            listServiceParam.add(serviceParam);
                            try {
                                if (0 >= Integer.valueOf(serviceParam.getPrice_max())
                                        || 0 >= Integer.valueOf(serviceParam.getPrice_min())
                                        || 0 >= Integer.valueOf(serviceParam.getJob_time())) {
                                    priceAndTimeNotSet = true;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (priceAndTimeNotSet) {
                        Utility.uiThreadAlert(context, "Warning", "Please enter Price Range and Job Time", "Ok", new Utility.OnDialogButtonClick() {
                            @Override
                            public void onOkayButtonClick() {

                            }
                        });
                        return;
                    }
                    ((ContractorMyServicesActivity) getActivity()).updateEmergency(listServiceParam);
                }
                break;
        }
    }

    private void initView(View rootView) {
        list_emergency = (ListView) rootView.findViewById(R.id.list_emergency);
        TextView trade_btn_next = (TextView) rootView.findViewById(R.id.myService_trade_btn_next);
        TextView trade_btn_update = (TextView) rootView.findViewById(R.id.myService_trade_btn_update);
        if (isFromTrade) {
            trade_btn_next.setVisibility(View.GONE);
            trade_btn_update.setVisibility(View.VISIBLE);
            trade_btn_update.setOnClickListener(this);
        } else {
            trade_btn_next.setVisibility(View.VISIBLE);
            trade_btn_update.setVisibility(View.GONE);
            trade_btn_next.setOnClickListener(this);
        }
    }

    private void initViewLoad() {
        mContractor = ((ContractorMyServicesActivity) getActivity()).contractorImpl.getmContractor();
        mTradeSignIn = ((ContractorMyServicesActivity) getActivity()).mTradeSignIn;
        Trade selectedTrade = ((ContractorMyServicesActivity) getActivity()).selectedTrade;

        List<ServiceParam> services = mTradeSignIn.getEmergency_services();
        if (selectedTrade == null) {
            loadEmergencyServices(services);
        } else if (selectedTrade.getTrade_id().equalsIgnoreCase(mTradeSignIn.getTrade_id())) {
            loadEmergencyServices(services);
        } else {
            services = new ArrayList<>();
            for (Trade mTradeService : selectedTrade.getServices()) {
                if (mTradeService.getService_type().equalsIgnoreCase("1")) {
                    services.add(new ServiceParam(mTradeService.getService_id(), "0", "0", "0", mTradeService.getService_name()));
                }
            }
            for (ServiceParam serviceParam : services) {
                serviceParam.isSelected = 1;
            }
            emergencyServiceAdapter = new EmergencyServiceAdapter(context, services);
            list_emergency.setAdapter(emergencyServiceAdapter);
        }
    }

    private void loadEmergencyServices(List<ServiceParam> services) {
        ConfigData mConfigData = ((ContractorMyServicesActivity) getActivity()).mConfigData;
        List<ServiceParam> configServices = new ArrayList<>();
        for (Trade mTrade : mConfigData.getTrades()) {
            if (mTrade.getTrade_id().equalsIgnoreCase(mTradeSignIn.getTrade_id())) {
                for (Trade td : mTrade.getServices()) {
                    if (td.getService_type().equalsIgnoreCase("1")) {
                        configServices.add(new ServiceParam(td.getService_id(), "0", "0", "0", td.getService_name()));
                    }
                }
                break;
            }
        }

        HashMap<Integer, ServiceParam> mapObject = new HashMap<>();
        for (int i = 0; i < configServices.size(); i++) {
            for (int j = 0; j < services.size(); j++) {
                if (configServices.get(i).getService_id().equalsIgnoreCase(services.get(j).getService_id())) {
                    mapObject.put(i, services.get(j));
                }
            }
        }
        for (Integer key : mapObject.keySet()) {
            mapObject.get(key).isSelected = 1;
            configServices.set(key, mapObject.get(key));
        }
        emergencyServiceAdapter = new EmergencyServiceAdapter(context, configServices);
        list_emergency.setAdapter(emergencyServiceAdapter);
    }
}
