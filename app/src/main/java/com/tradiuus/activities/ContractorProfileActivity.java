package com.tradiuus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.models.Contractor;
import com.tradiuus.widgets.CustomTextView;


public class ContractorProfileActivity extends BaseActivity {

    private static final int PASS_CODE = 101;
    private Context context;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    public Contractor mContractor;


    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_profile);
        this.context = ContractorProfileActivity.this;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PASS_CODE) {
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void initUIListener() {
        contractor_dashboard_back.setOnClickListener(this);
        ((TextView) findViewById(R.id.setting_btn_compnay)).setOnClickListener(this);
        ((TextView) findViewById(R.id.setting_btn_logo)).setOnClickListener(this);
        ((TextView) findViewById(R.id.setting_btn_tech)).setOnClickListener(this);
        ((TextView) findViewById(R.id.setting_btn_area)).setOnClickListener(this);
        ((TextView) findViewById(R.id.setting_btn_credit)).setOnClickListener(this);
        ((TextView) findViewById(R.id.setting_btn_video)).setOnClickListener(this);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.setting_btn_compnay:
                loadFragment(ContractorProfileUpdateActivity.FragTag.COMPANY);
                break;
            case R.id.setting_btn_logo:
                loadFragment(ContractorProfileUpdateActivity.FragTag.LOGO);
                break;
            case R.id.setting_btn_tech:
                loadFragment(ContractorProfileUpdateActivity.FragTag.TECHNICIAN);
                break;
            case R.id.setting_btn_area:
                loadFragment(ContractorProfileUpdateActivity.FragTag.AREA);
                break;
            case R.id.setting_btn_credit:
                loadFragment(ContractorProfileUpdateActivity.FragTag.CARD);
                break;
            case R.id.setting_btn_video:
                loadFragment(ContractorProfileUpdateActivity.FragTag.VIDEO);
                break;
        }
    }

    @Override
    protected void initLoadCall() {

    }

    private void loadFragment(ContractorProfileUpdateActivity.FragTag fragTag) {
        Intent intent = new Intent(context, ContractorProfileUpdateActivity.class);
        intent.putExtra("frag_tag", fragTag.ordinal());
        startActivityForResult(intent, PASS_CODE);
    }
}