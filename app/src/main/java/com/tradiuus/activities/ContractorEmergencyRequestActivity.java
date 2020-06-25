package com.tradiuus.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.ContractorEmergencyReqListFragment;
import com.tradiuus.helper.StackActivities;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.ContractorImpl;
import com.tradiuus.models.MessageEvent;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class ContractorEmergencyRequestActivity extends BaseActivity implements ContractorImpl.OnRequestSyncChangedListener {

    private Context context;
    private CustomTextView contractor_dashboard_back;
    public CustomTextView contractor_dashboard_logut;
    public Contractor mContractor;
    private ContractorImpl contractorImpl;
    private boolean isActive = false;
    private boolean isNotificationReceived = false;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_emergency_req);
        EventBus.getDefault().register(this);
        StackActivities.registerActivity(this);
        this.context = ContractorEmergencyRequestActivity.this;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
        contractorImpl = new ContractorImpl();
        contractorImpl.init(context, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isNotificationReceived && contractorImpl != null) {
                contractorImpl.getServices();
            }
            isActive = true;
            isNotificationReceived = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        try {
            StackActivities.removeTop();
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        try {
            if (contractorImpl != null && StackActivities.getActivity().equals(context)) {
                if (isActive) {
                    contractorImpl.getServices();
                } else {
                    isNotificationReceived = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
            contractor_dashboard_logut.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);
        contractor_dashboard_logut.setText("Home");
    }

    @Override
    protected void initUIListener() {
        contractor_dashboard_back.setOnClickListener(this);
        contractor_dashboard_logut.setOnClickListener(this);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.contractor_dashboard_logut:
                overridePendingTransition(0,0);
                finish();
                break;
        }
    }

    @Override
    protected void initLoadCall() {

        replaceFragment(new ContractorEmergencyReqListFragment());
    }

    public void replaceFragment(Fragment fragment) {
        try {
            String backStateName = fragment.getClass().getName();

            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(backStateName);
                ft.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataFetchDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getSupportFragmentManager().getFragments().size() > 0 && getSupportFragmentManager().getFragments().get(0) instanceof ContractorEmergencyReqListFragment) {
                        ((ContractorEmergencyReqListFragment) getSupportFragmentManager().getFragments().get(0)).refreshUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onShowPgDialog() {
        try {
            runOnUiThread(new Runnable() {
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

    @Override
    public void onHidePgDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pgDialog.dismissDialog();
            }
        });
    }

    @Override
    public void showAlert(String message) {
        Utility.uiThreadAlert(context, message);
    }
}