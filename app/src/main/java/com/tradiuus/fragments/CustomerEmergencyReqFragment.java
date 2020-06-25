package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorMyServicesActivity;
import com.tradiuus.activities.CustomerEmergencyCaseActivity;
import com.tradiuus.adapters.TradeServiceAdapter;
import com.tradiuus.models.Trade;

import java.util.ArrayList;
import java.util.List;

public class CustomerEmergencyReqFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ListView list_select_type;
    private ImageView req_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_emergency_req, null);
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
        }
    }

    private void initView(View rootView) {
        req_type = (ImageView) rootView.findViewById(R.id.req_type);
        list_select_type = (ListView) rootView.findViewById(R.id.list_of_emergencies);
    }

    public void initViewLoad() {
        try {
            List<Trade> emergencyservices = new ArrayList<>();
            List<Trade> services = ((CustomerEmergencyCaseActivity) getActivity()).selectedTrade.getServices();
            for (Trade mTrade : services) {
                if (mTrade.getService_type().equalsIgnoreCase("1")) {
                    emergencyservices.add(mTrade);
                }
            }
            TradeServiceAdapter mTradeAdapter = new TradeServiceAdapter(context, emergencyservices, new TradeServiceAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Trade trade) {
                    //((CustomerEmergencyCaseActivity) getActivity()).selectedEmergencyServices = trade;
                }
            });
            list_select_type.setAdapter(mTradeAdapter);
            DrawableCompat.setTint(DrawableCompat.wrap(req_type.getDrawable()), Color.parseColor("#5fd8e3"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
