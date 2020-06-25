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
import com.tradiuus.adapters.TradeAdapter;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Trade;

import java.util.List;

public class ContractorMyServicesTradeFragment extends Fragment implements View.OnClickListener {
    public static ContractorMyServicesTradeFragment fragment;
    private Context context;
    public ListView list_select_type;

    public Contractor mContractor;
    private Trade mTradeSignIn;
    private boolean isTradeChanged = false;

    public static ContractorMyServicesTradeFragment newInstance(int position, Context context) {
        if (fragment == null)
            fragment = new ContractorMyServicesTradeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_myservice_trade, null);
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
            case R.id.myService_trade_btn_next:
                if (!isTradeChanged) {
                    ((ContractorMyServicesActivity) getActivity()).selectedTrade = null;
                }
                ((ContractorMyServicesActivity) getActivity()).loadFragment(ContractorMyServicesActivity.FragTag.EmergencyService, false);
                break;
        }
    }

    private void initView(View rootView) {
        list_select_type = (ListView) rootView.findViewById(R.id.list_select_type);
        ((TextView) rootView.findViewById(R.id.myService_trade_btn_next)).setOnClickListener(this);
    }

    private void initViewLoad() {
        int lastSelectedIndex = -1;
        mContractor = ((ContractorMyServicesActivity) getActivity()).contractorImpl.getmContractor();
        mTradeSignIn = ((ContractorMyServicesActivity) getActivity()).mTradeSignIn;
        List<Trade> trades = ((ContractorMyServicesActivity) getActivity()).mConfigData.getTrades();
        for (int i = 0; i < trades.size(); i++) {
            if (mTradeSignIn.getTrade_id().equalsIgnoreCase(trades.get(i).getTrade_id())) {
                trades.get(i).isSelected = 1;
                lastSelectedIndex = i;
            } else {
                trades.get(i).isSelected = 0;
            }
        }
        TradeAdapter mTradeAdapter = new TradeAdapter(context, trades, lastSelectedIndex, new TradeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trade trade) {
                isTradeChanged = true;
                ((ContractorMyServicesActivity) getActivity()).selectedTrade = trade;
            }
        });
        list_select_type.setAdapter(mTradeAdapter);
    }
}
