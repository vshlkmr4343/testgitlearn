package com.tradiuus.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.adapters.CarousalPagerAdapter;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Image;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HelpActivity extends BaseActivity {

    private Context context;
    private ViewPager viewpager;
    private CarousalPagerAdapter carousalPagerAdapter;
    private CircleIndicator indicator;
    public ConfigData mConfigData;

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_help);
        this.context = HelpActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.contractor_dashboard_logut)).setVisibility(View.GONE);
        ((CustomTextView) findViewById(R.id.contractor_dashboard_back)).setVisibility(View.GONE);
        ((CustomTextView) findViewById(R.id.splash_goto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initLoadCall() {
        try {
            carousalPagerAdapter = new CarousalPagerAdapter(context, new ArrayList<Image>(mConfigData.getImages()), mConfigData.getImage_url());
            viewpager.setAdapter(carousalPagerAdapter);
            indicator.setViewPager(viewpager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void viewClick(View view) {
    }


}
