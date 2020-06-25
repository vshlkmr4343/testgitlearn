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
import com.tradiuus.adapters.EstimateServiceAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Trade;
import com.tradiuus.network.postparams.ServiceParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContractorMyServicesEstimateFragment extends Fragment implements View.OnClickListener {
    private Context context;
    public Contractor mContractor;
    private Trade mTradeSignIn;
    public ListView list_estimate;
    private EstimateServiceAdapter estimateServiceAdapter;
    private boolean isFromTrade = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_myservice_estimate, null);
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
                if (estimateServiceAdapter.items != null) {
                    boolean priceAndTimeNotSet = false;
                    List<ServiceParam> listEstimateParam = new ArrayList<>();
                    for (ServiceParam serviceParam : estimateServiceAdapter.items) {
                        if (serviceParam.isSelected == 1) {
                            listEstimateParam.add(serviceParam);
                            try {
                                if (Integer.valueOf(serviceParam.getPrice_max()) <= 0
                                        || Integer.valueOf(serviceParam.getPrice_min()) <= 0
                                        || Integer.valueOf(serviceParam.getJob_time()) <= 0) {
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
                    if (isFromTrade) {
                        ((ContractorMyServicesActivity) getActivity()).updateEstimate(listEstimateParam);
                    } else {
                        ((ContractorMyServicesActivity) getActivity()).updateTradeOnly(((ContractorMyServicesActivity) getActivity()).services, listEstimateParam);
                    }
                }
                break;
        }
    }

    private void initView(View rootView) {
        list_estimate = (ListView) rootView.findViewById(R.id.list_emergency);
        ((TextView) rootView.findViewById(R.id.myService_trade_btn_next)).setOnClickListener(this);
    }

    private void initViewLoad() {
        mContractor = ((ContractorMyServicesActivity) getActivity()).contractorImpl.getmContractor();
        mTradeSignIn = ((ContractorMyServicesActivity) getActivity()).mTradeSignIn;
        Trade selectedTrade = ((ContractorMyServicesActivity) getActivity()).selectedTrade;

        List<ServiceParam> services = mTradeSignIn.getEstimate_services();
        if (selectedTrade == null) {
            loadEstimateServices(services);
        } else if (selectedTrade.getTrade_id().equalsIgnoreCase(mTradeSignIn.getTrade_id())) {
            loadEstimateServices(services);
        } else {
            services = new ArrayList<>();
            for (Trade mTradeService : selectedTrade.getServices()) {
                if (mTradeService.getService_type().equalsIgnoreCase("2")) {
                    services.add(new ServiceParam(mTradeService.getService_id(), "0", "0", "0", mTradeService.getService_name()));
                }
            }
            for (ServiceParam serviceParam : services) {
                serviceParam.isSelected = 1;
            }
            estimateServiceAdapter = new EstimateServiceAdapter(context, services);
            list_estimate.setAdapter(estimateServiceAdapter);
        }
    }

    private void loadEstimateServices(List<ServiceParam> services) {
        ConfigData mConfigData = ((ContractorMyServicesActivity) getActivity()).mConfigData;
        List<ServiceParam> configServices = new ArrayList<>();
        for (Trade mTrade : mConfigData.getTrades()) {
            if (mTrade.getTrade_id().equalsIgnoreCase(mTradeSignIn.getTrade_id())) {
                for (Trade td : mTrade.getServices()) {
                    if (td.getService_type().equalsIgnoreCase("2")) {
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
        estimateServiceAdapter = new EstimateServiceAdapter(context, configServices);
        list_estimate.setAdapter(estimateServiceAdapter);
    }
}
