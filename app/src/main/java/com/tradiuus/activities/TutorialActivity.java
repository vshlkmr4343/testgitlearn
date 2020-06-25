package com.tradiuus.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.adapters.CarousalPagerAdapter;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Image;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends BaseActivity {

    private Context context;
    private ViewPager viewpager;
    private CarousalPagerAdapter carousalPagerAdapter;
    private CircleIndicator indicator;
    public ConfigData mConfigData;
    private ArrayList<Image> pagerItems;
    public int indexCurrentP = 0;

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_tutorial);
        this.context = TutorialActivity.this;
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
    protected void viewClick(View v) {
        switch (v.getId()) {
            case R.id.pager_back:
                try {
                    if (indexCurrentP == 0 || indexCurrentP < 0) {
                        return;
                    }
                    indexCurrentP--;
                    viewpager.setCurrentItem(indexCurrentP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.pager_next:
                try {
                    if (indexCurrentP == (pagerItems.size() - 1)) {
                        return;
                    }
                    indexCurrentP++;
                    viewpager.setCurrentItem(indexCurrentP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.contractor_dashboard_logut)).setVisibility(View.GONE);
        ((CustomTextView) findViewById(R.id.contractor_dashboard_back)).setVisibility(View.VISIBLE);
        ((CustomTextView) findViewById(R.id.contractor_dashboard_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((ImageView) findViewById(R.id.pager_back)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.pager_next)).setOnClickListener(this);
    }

    @Override
    protected void initLoadCall() {
        try {
            pagerItems = new ArrayList<Image>(mConfigData.getImages());
            carousalPagerAdapter = new CarousalPagerAdapter(context, pagerItems, mConfigData.getImage_url());
            viewpager.setAdapter(carousalPagerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
