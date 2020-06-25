package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorProfileUpdateActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ContractorProfileCardUpdateFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = ContractorProfileCardUpdateFragment.class.getSimpleName();
    private Context context;
    public Contractor mContractor;

    public EditText frag_card_info_no;
    public EditText card_detail_cvv;
    private TextView exp_date;

    private String exp_month = "";
    private String exp_year = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_detail, null);
        this.context = getActivity();
        this.mContractor = ((ContractorProfileUpdateActivity) getActivity()).contractorImpl.getmContractor();
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
            case R.id.card_detail_btn_save:
                saveEvent();
                break;
            case R.id.card_detail_exp_date:
                createDialogWithoutDateField();
                break;
        }
    }


    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.credit_card_info_title)).setText("Update Credit Card Info");
        ((TextView) rootView.findViewById(R.id.card_detail_btn_save)).setOnClickListener(this);

        frag_card_info_no = (EditText) rootView.findViewById(R.id.frag_card_info_no);
        exp_date = (TextView) rootView.findViewById(R.id.card_detail_exp_date);
        card_detail_cvv = (EditText) rootView.findViewById(R.id.card_detail_cvv);
        exp_date.setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.btn_powered_by_trip)).setText(Html.fromHtml("Powered by <b>Stripe</b>"));
    }

    private void initViewLoad() {
        if (!TextUtils.isEmpty(mContractor.getCardNo())) {
            frag_card_info_no.setText(mContractor.getCardNo());
        } else if (!TextUtils.isEmpty(mContractor.getCredit_card_number())) {
            frag_card_info_no.setText(mContractor.getCredit_card_number());
        }
    }

    private void createDialogWithoutDateField() {
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                exp_month = String.valueOf(selectedMonth + 1); //month coming as 0-11
                exp_year = String.valueOf(selectedYear);
                exp_date.setText(exp_month + "/" + exp_year);
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setMinYear(today.get(Calendar.YEAR))
                .setMaxYear((today.get(Calendar.YEAR) + 20))
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {
                    }
                })
                .build()
                .show();

    }

    private void saveEvent() {
        String card_number = frag_card_info_no.getText().toString().trim(); //"4514570014787801";
        String cvv = card_detail_cvv.getText().toString().trim();  //"816";
        if (TextUtils.isEmpty(card_number)
                || TextUtils.isEmpty(exp_month)
                || TextUtils.isEmpty(exp_year)
                || TextUtils.isEmpty(cvv)) {
            Utility.uiThreadAlert(context, "Please check all the field values before save");
            return;
        }
        JSONObject cardDetailsJson = new JSONObject();
        try {
            JSONObject cardJson = new JSONObject();
            cardJson.put("card_number", card_number);
            cardJson.put("exp_month", exp_month);
            cardJson.put("exp_year", exp_year);
            cardJson.put("cvv", cvv);
            cardDetailsJson.put("card_details", cardJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ContractorProfileUpdateActivity) getActivity()).updateProfileData(cardDetailsJson.toString(), "creditCardInformation", ContractorProfileUpdateActivity.FragTag.CARD);
    }
}
