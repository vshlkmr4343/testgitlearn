package com.tradiuus.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.adapters.CarousalPagerAdapter;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.models.Image;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Technician;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class SplashActivity extends BaseActivity {
    private Context context;
    private ViewPager viewpager;
    private CircleIndicator indicator;
    private CarousalPagerAdapter carousalPagerAdapter;

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_splash);
        this.context = SplashActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
    }

    @Override
    protected void onDestroy() {
        RegisterActivities.removeTop();
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.splash_goto)).setOnClickListener(this);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
    }

    @Override
    protected void initLoadCall() {
        if (UserPreference.getCustomer(context) != null) {
            Intent intentCustomer = new Intent(context, CustomerDashboardActivity.class);
            if (getIntent().getExtras() != null) {
                intentCustomer.putExtras(getIntent().getExtras());
            }
            startActivity(intentCustomer);
            overridePendingTransition(0, 0);
            finish();
        } else if (UserPreference.getContractor(context) != null) {
            Contractor mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
            if (mContractor != null && mContractor.isCardSave) {
                Intent intentContractor = new Intent(context, ContractorDashboardActivity.class);
                if (getIntent().getExtras() != null) {
                    intentContractor.putExtras(getIntent().getExtras());
                }
                startActivity(intentContractor);
                overridePendingTransition(0, 0);
                finish();
            } else {
                startActivity(new Intent(context, ContractorSignupActivity.class));
                overridePendingTransition(0, 0);
            }
        } else if (UserPreference.getTechnician(context) != null) {
            Technician mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
            if (mTechnician != null) {
                Intent intentTechnician = new Intent(context, TechnicianDashboardActivity.class);
                if (getIntent().getExtras() != null) {
                    intentTechnician.putExtras(getIntent().getExtras());
                }
                startActivity(intentTechnician);
                overridePendingTransition(0, 0);
                finish();
            } else {
                startActivity(new Intent(context, ContractorSignupActivity.class));
                overridePendingTransition(0, 0);
            }
        } else {
            loadConfigData();
        }
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.splash_goto:
                startActivity(new Intent(context, LoginActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    protected void loadPagerData(ArrayList<Image> images, String image_url) {
        carousalPagerAdapter = new CarousalPagerAdapter(context, images, image_url);
        viewpager.setAdapter(carousalPagerAdapter);
        indicator.setViewPager(viewpager);
        carousalPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

}
