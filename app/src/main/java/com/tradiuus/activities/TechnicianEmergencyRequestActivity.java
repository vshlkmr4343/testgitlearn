package com.tradiuus.activities;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.TechnicianEmergencyReqListDetailsFragment;
import com.tradiuus.fragments.TechnicianEmergencyReqListFragment;
import com.tradiuus.helper.StackActivities;
import com.tradiuus.models.Job;
import com.tradiuus.models.MessageEvent;
import com.tradiuus.models.Technician;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class TechnicianEmergencyRequestActivity extends BaseActivity implements TechnicianImpl.OnTechnicianJobCompletedListener {

    private Context context;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    public Technician mTechnician;
    public TechnicianImpl technicianImpl;
    private CustomProgressDialog pgDialog;
    public int selectedIndex = -1;
    private boolean isActive = false;
    private boolean isNotificationReceived = false;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_emergency_req);
        EventBus.getDefault().register(this);
        StackActivities.registerActivity(this);
        this.context = TechnicianEmergencyRequestActivity.this;

        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
        technicianImpl = new TechnicianImpl();
        technicianImpl.init(context, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isNotificationReceived && technicianImpl != null) {
                technicianImpl.getServicesData();
            }
            isActive = true;
            isNotificationReceived = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        try {
            if (technicianImpl != null && StackActivities.getActivity().equals(context)) {
                if (isActive) {
                    technicianImpl.getServicesData();
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
            HomeShowHide(false);
            super.onBackPressed();
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
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        replaceFragment(new TechnicianEmergencyReqListFragment());
    }

    public void replaceFragment(Fragment fragment) {
        try {
            String backStateName = fragment.getClass().getName();
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.fragment_container, fragment, backStateName);
                ft.addToBackStack(backStateName);
                ft.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void HomeShowHide(boolean shouldShow) {
        contractor_dashboard_logut.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
    }

    public void completeJob(Job job, int index) {
        technicianImpl.completeJob(job.getCustomer_details().getCustomer_id(), mTechnician.getTechnician_id(), job.getJob_id(), "1", index);
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

    }

    @Override
    public void onDataSetRefreshed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().getFragments().size() > 0 && getSupportFragmentManager().getFragments().get(0) instanceof TechnicianEmergencyReqListFragment) {
                    ((TechnicianEmergencyReqListFragment) getSupportFragmentManager().getFragments().get(0)).refreshUI();
                }
            }
        });
    }

    @Override
    public void onJobCompletedSuccess(final String job_id, final String status, final String status_value, final int index) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());
                        ((TradiuusApp) context.getApplicationContext()).emergencyJobList.get(index).setJob_status(status);
                        if (fragment instanceof TechnicianEmergencyReqListDetailsFragment) {
                            onBackPressed();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(0).getName());
                                        ((TechnicianEmergencyReqListFragment) fragment).updateEmergencyJob();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);
                        } else {
                            ((TechnicianEmergencyReqListFragment) fragment).updateEmergencyJob();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void startReceiver() {
        try {
            if (pushReceiver == null) {
                pushReceiver = new PushReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(getPackageName() + MyFirebaseMessagingService.FCM_NOTIFICATION_RECEIVER_TECH_2);
                registerReceiver(pushReceiver, intentFilter);
            } else {
                stopReceiver();
                startReceiver();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopReceiver() {
        if (pushReceiver != null) {
            unregisterReceiver(pushReceiver);
            pushReceiver = null;
        }
    }

    public class PushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            technicianImpl.getServicesData();
        }
    }*/
}