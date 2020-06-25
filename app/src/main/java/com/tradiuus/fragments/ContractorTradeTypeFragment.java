package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorSignupActivity;
import com.tradiuus.adapters.TradeAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Trade;

public class ContractorTradeTypeFragment extends Fragment implements View.OnClickListener {

    private Context context;
    public static ContractorTradeTypeFragment fragment;
    public ListView list_select_type;
    public ImageView fragment_trade;

    private boolean isChecked = true;

    public static ContractorTradeTypeFragment newInstance(int position, Context context) {
        if (fragment == null)
            fragment = new ContractorTradeTypeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_type_of_trade, null);
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
            case R.id.frag_trade_btn_proceed:
                nextEvent();
                break;
            case R.id.frag_trade_btn_back:
                ((ContractorSignupActivity) getActivity()).backEvent();
                break;
        }
    }

    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.frag_trade_btn_proceed)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.frag_trade_btn_back)).setOnClickListener(this);
        list_select_type = (ListView) rootView.findViewById(R.id.list_select_type);
        fragment_trade = (ImageView) rootView.findViewById(R.id.fragment_trade);
    }

    private void initLoad() {
        TradeAdapter mTradeAdapter = new TradeAdapter(context, ((ContractorSignupActivity) getActivity()).mConfigData.getTrades(), new TradeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trade trade) {
                ((ContractorSignupActivity) getActivity()).selectedTrade = trade;
            }
        });
        list_select_type.setAdapter(mTradeAdapter);

        fragment_trade.setSelected(true);
        fragment_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                fragment_trade.setSelected(isChecked);
            }
        });
    }

    private void nextEvent() {
        if (((ContractorSignupActivity) getActivity()).selectedTrade == null) {
            Utility.uiThreadAlert(context, "Please select an value before hit the next button.");
            return;
        }
        if (!isChecked) {
            Utility.uiThreadAlert(context, "Warning", "Tradiuus provides both emergency and estimate Services. Please agree to provide both.", "Ok", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {

                }
            });
            return;
        }
        ((ContractorSignupActivity) getActivity()).nextEvent(ContractorSignupActivity.FragTag.EstimatePrice);
    }
}