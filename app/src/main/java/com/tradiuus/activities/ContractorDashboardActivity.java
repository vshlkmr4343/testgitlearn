package com.tradiuus.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tradiuus.R;
import com.tradiuus.helper.StackActivities;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.ContractorImpl;
import com.tradiuus.models.MessageEvent;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class ContractorDashboardActivity extends BaseActivity implements ContractorImpl.OnDashboardDataChangedListener {

    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView username;
    private ContractorImpl contractorImpl;
    private LinearLayout layout_imp;
    private SwitchCompat work_status;
    private boolean isActive = false;
    private boolean isNotificationReceived = false;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_dashboard_contractor);
        EventBus.getDefault().register(this);
        StackActivities.registerActivity(this);
        this.context = ContractorDashboardActivity.this;

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
        super.onBackPressed();
    }

    @Override
    protected void initUIComponent() {
        username = (CustomTextView) findViewById(R.id.contractor_dsb_username);
        layout_imp = (LinearLayout) findViewById(R.id.layout_imp);
        work_status = (SwitchCompat) findViewById(R.id.contractor_dsb_work_status);
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.contractor_dashboard_logut)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_emergency)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_estimate)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_luxury)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_myservices)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_myprofile)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.contractor_dash_btn_settings)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.dashboard_tab)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.logout_tab)).setOnClickListener(this);
        layout_imp.setOnClickListener(this);
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
    public void logoutSuccess(String message) {
        UserPreference.saveContractor(context, null);
        startActivity(new Intent(context, LoginActivity.class));
        overridePendingTransition(0, 0);
        finish();
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

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_logut:
                logoutDialog(new OnLogoutListener() {
                    @Override
                    public void onProceed() {
                        try {
                            contractorImpl.logoutUser(contractorImpl.mContractor.getUser_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.contractor_dash_btn_emergency:
                startActivity(new Intent(context, ContractorEmergencyRequestActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_estimate:
                startActivity(new Intent(context, ContractorEstimateRequestActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_luxury:
                startActivity(new Intent(context, ContractorLuxuryBuildingsActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_myservices:
                startActivity(new Intent(context, ContractorMyServicesActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_myprofile:
                startActivity(new Intent(context, ContractorProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.contractor_dash_btn_settings:
                startActivity(new Intent(context, ContractorSettingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.dashboard_tab:
                onDashboardEvent();
                break;
            case R.id.logout_tab:
                logoutDialog(new OnLogoutListener() {
                    @Override
                    public void onProceed() {
                        try {
                            contractorImpl.logoutUser(contractorImpl.mContractor.getUser_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            if (contractorImpl.getmContractor().isShownImp()) {
                layout_imp.setVisibility(View.GONE);
                username.setText(contractorImpl.getmContractor().getCompany_name());
                contractorImpl.getServices();
            } else {
                layout_imp.setVisibility(View.VISIBLE);
            }
            changeWorkingStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataFetchDone() {

    }

    @Override
    public void updateWorkingStatus(String msg) {
        Utility.uiThreadAlert(context, getString(R.string.app_name), msg);
    }

    @Override
    public void failToUpdateWorkingStatus(String message, String status) {
        switch (status) {
            case "1":
                work_status.setChecked(false);
                break;
            case "3":
                work_status.setChecked(true);
                break;
        }
        Utility.uiThreadAlert(context, message);
    }

    private void changeWorkingStatus() {
        try {
            if (contractorImpl.mContractor != null && contractorImpl.mContractor.getStatus() > -1) {
                switch (contractorImpl.mContractor.getStatus()) {
                    case 1:
                        work_status.setChecked(true);
                        break;
                    case 3:
                        work_status.setChecked(false);
                        break;
                    default:
                        work_status.setChecked(true);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        work_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        contractorImpl.updateWorkingStatus(contractorImpl.mContractor.getContractor_id(), contractorImpl.mContractor.getUser_type() + "", "1");
                    } else {
                        contractorImpl.updateWorkingStatus(contractorImpl.mContractor.getContractor_id(), contractorImpl.mContractor.getUser_type() + "", "3");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onDashboardEvent() {
        try {
            Contractor mContractor = contractorImpl.getmContractor();
            mContractor.setShownImp(true);
            UserPreference.saveContractor(context, mContractor);
            contractorImpl.setmContractor(mContractor);
            layout_imp.setVisibility(View.GONE);
            username.setText(contractorImpl.getmContractor().getCompany_name());
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && (bundle.containsKey("new_user") || bundle.containsKey("loginUser"))) {
                showCongratulationsMsg();
            } else {
                contractorImpl.getServices();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCongratulationsMsg() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Congratulations!!!");
        builder1.setMessage("Your contractor profile has been successfully created on Tradiuus. " +
                "You have also been added as a technician and your username/password for the technician " +
                "account is the same as your contractor login information. Once your contractor account is " +
                "approved by the admin, you will be able to login to your technician profile and complete it," +
                "after which Tradiuus will start sending qualified lead to you. You can also add more technicians" +
                " under your My Profile->Update Technicians's Details section.");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        contractorImpl.getServices();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}