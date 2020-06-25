package com.tradiuus.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.TechnicianLuxuryBuildingListFragment;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Job;
import com.tradiuus.models.Technician;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;


public class TechnicianLuxuryBuildingsActivity extends BaseActivity implements TechnicianImpl.OnTechnicianLuxuryDataChangedListener {

    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView title_luxury;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    private ImageView logo;
    private ImageView cross;
    public TechnicianImpl technicianImpl;
    public ArrayList<Job> jobList;
    private Technician mTechnician;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_technician_luxury_building);
        this.context = TechnicianLuxuryBuildingsActivity.this;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
        technicianImpl = new TechnicianImpl();
        technicianImpl.init(context, this);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            title_luxury.setText("LUXURY BUILDING");
            cross.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);
            contractor_dashboard_back.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }
    }

    @Override
    protected void initUIComponent() {
        title_luxury = (CustomTextView) findViewById(R.id.title_luxury);
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);
        title_luxury.setText("LUXURY BUILDING");

        logo = (ImageView) findViewById(R.id.logo);
        cross = (ImageView) findViewById(R.id.action_cross);
    }

    @Override
    protected void initUIListener() {
        contractor_dashboard_back.setOnClickListener(this);
        contractor_dashboard_logut.setOnClickListener(this);
        cross.setOnClickListener(this);
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
            case R.id.action_cross:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        replaceFragment(new TechnicianLuxuryBuildingListFragment());
        technicianImpl.getLuxuryData();
    }

    public void replaceFragment(Fragment fragment) {
        try {
            String backStateName = fragment.getClass().getName();
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.add(R.id.fragment_container, fragment, backStateName);
                ft.addToBackStack(backStateName);
                ft.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Job> map = new HashMap<>();

    @Override
    public void onLuxuryDataFetchDone(ArrayList<Job> jobArray, HashMap<String, Job> map) {
        try {
            this.jobList = jobArray;
            this.map = (map != null) ? map : new HashMap<String, Job>();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(0).getName());
                    ((TechnicianLuxuryBuildingListFragment) fragment).updateDataValue(jobList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShowPgDialog() {
        pgDialog = new CustomProgressDialog(context);
        pgDialog.prepareAndShowDialog();
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

    public void changeTitle() {
        contractor_dashboard_back.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.GONE);
        cross.setVisibility(View.VISIBLE);
        title_luxury.setText("ADD LUXURY BUILDING");
    }

    @Override
    public void showAlert(String message) {
        Utility.uiThreadAlert(context, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(0).getName());
                    ((TechnicianLuxuryBuildingListFragment) fragment).noData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLuxuryDataAdded(String message) {
        Utility.uiThreadAlert(context, getString(R.string.app_name), message, "Ok", new Utility.OnDialogButtonClick() {

            @Override
            public void onOkayButtonClick() {
                onBackPressed();
                technicianImpl.getLuxuryData();
            }
        });
    }

    public void addJobs(ArrayList<String> ids) {
        technicianImpl.addGeoJob(mTechnician.getTechnician_id(), mTechnician.getUser_type(), ids);
    }


}