package com.tradiuus.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.ActivityWebView;
import com.tradiuus.activities.CustomerDashboardActivity;
import com.tradiuus.activities.TutorialActivity;
import com.tradiuus.adapters.CarousalPagerAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Customer;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Image;
import com.tradiuus.widgets.CustomEditText;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;

public class CustomerHelpFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private CustomEditText userFeedback;
    /*private ViewPager viewpager;
    private CarousalPagerAdapter carousalPagerAdapter;*/
    public ConfigData mConfigData;
    private CustomProgressDialog pgDialog;
    public Customer customer;
    /*private ArrayList<Image> pagerItems;
    public int indexCurrentP = 0;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_help, null);
        this.context = getActivity();
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        customer = ((CustomerDashboardActivity) getActivity()).customer;
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
            case R.id.btn_submit: {
                String feedback = userFeedback.getText().toString();
                if (!TextUtils.isEmpty(feedback)) {
                    sendSuggestion(customer.getCustomer_id(), customer.getUser_id(), feedback);
                }
            }
            break;
            case R.id.btn_tutorial: {
                Intent intent = new Intent(context, TutorialActivity.class);
                ((CustomerDashboardActivity) getActivity()).startActivity(intent);
            }
            break;
            case R.id.btn_faq: {
                Intent intent = new Intent(context, ActivityWebView.class);
                intent.putExtra("URL", customer.getFaqs());
                ((CustomerDashboardActivity) getActivity()).startActivity(intent);
                ((CustomerDashboardActivity) getActivity()).overridePendingTransition(0, 0);
            }
            break;
            case R.id.btn_videos: {
                Intent intent = new Intent(context, ActivityWebView.class);
                intent.putExtra("URL", customer.getVideos());
                ((CustomerDashboardActivity) getActivity()).startActivity(intent);
                ((CustomerDashboardActivity) getActivity()).overridePendingTransition(0, 0);
            }
            break;
            /*case R.id.pager_back:
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
                break;*/
        }
    }

    private void initView(View rootView) {
        /* viewpager = (ViewPager) rootView.findViewById(R.id.viewpager);*/
        userFeedback = (CustomEditText) rootView.findViewById(R.id.userFeedback);
        ((CustomTextView) rootView.findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.btn_tutorial)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.btn_faq)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.btn_videos)).setOnClickListener(this);
        /*((ImageView) rootView.findViewById(R.id.pager_back)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.pager_next)).setOnClickListener(this);*/
    }

    public void initViewLoad() {
        /*try {
            pagerItems = new ArrayList<Image>(mConfigData.getImages());
            carousalPagerAdapter = new CarousalPagerAdapter(context, pagerItems, mConfigData.getImage_url());
            viewpager.setAdapter(carousalPagerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void sendSuggestion(String customer_id, String user_id, String suggestion) {
        ((CustomerDashboardActivity) getActivity()).customerImpl.sendSuggestion(customer_id, user_id, suggestion, new CustomerImpl.OnSuggestionSendListener() {
            @Override
            public void suggestionSend(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userFeedback.setText("");
                        Utility.uiThreadAlert(getActivity(), getActivity().getString(R.string.app_name), message);
                    }
                });
            }

            @Override
            public void onShowPgDialog() {
                onShowPgD();
            }

            @Override
            public void onHidePgDialog() {
                onHidePgD();
            }

            @Override
            public void showAlert(String message) {
                onHidePgDialog();
            }
        });
    }

    public void onShowPgD() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog = new CustomProgressDialog(context);
                    pgDialog.prepareAndShowDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onHidePgD() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog.dismissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
