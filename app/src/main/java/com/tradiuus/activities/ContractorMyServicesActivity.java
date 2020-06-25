package com.tradiuus.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.google.gson.Gson;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.ContractorMyServicesEmergencyFragment;
import com.tradiuus.fragments.ContractorMyServicesEmergencyTimeFragment;
import com.tradiuus.fragments.ContractorMyServicesEstimateFragment;
import com.tradiuus.fragments.ContractorMyServicesEstimateTimeFragment;
import com.tradiuus.fragments.ContractorMyServicesFragment;
import com.tradiuus.fragments.ContractorMyServicesTradeFragment;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.ContractorImpl;
import com.tradiuus.models.Trade;
import com.tradiuus.network.postparams.ServiceParam;
import com.tradiuus.network.postparams.UpdatePostParam;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;
import java.util.List;


public class ContractorMyServicesActivity extends BaseActivity implements ContractorImpl.OnMyServiceDataChangedListener {


    private Context context;
    private CustomProgressDialog pgDialog;
    private FragmentManager manager = getSupportFragmentManager();
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    public ContractorImpl contractorImpl;
    public Trade mTradeSignIn;
    public ConfigData mConfigData;
    public Trade selectedTrade;

    public List<ServiceParam> services;


    public static enum FragTag {
        Main, Trade, EmergencyService, EstimateService, EmergencyTime, EstimateTime
    }

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_myservices);
        this.context = ContractorMyServicesActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        contractorImpl = new ContractorImpl();
        contractorImpl.init(context, this);
        mTradeSignIn = contractorImpl.getmContractor().getTrade().get(0);
    }

    @Override
    public void onBackPressed() {
        if (manager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            contractor_dashboard_logut.setVisibility(View.INVISIBLE);
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
        loadFragment(FragTag.Main, false);
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

    @Override
    public void showAlert(String message) {
        Utility.uiThreadAlert(context, message);
    }

    @Override
    public void onUpdateServiceData(final boolean callForAll, String message) {
        try {
            Utility.uiThreadAlert(context, message, new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {
                    if (callForAll) {
                        for (int i = 0; i < 3; i++) {
                            onBackPressed();
                        }
                    } else {
                        onBackPressed();
                    }
                    updateServiceData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateTime(final String message) {
        Utility.uiThreadAlert(context, getString(R.string.app_name), message, "Ok", new Utility.OnDialogButtonClick() {
            @Override
            public void onOkayButtonClick() {
                onBackPressed();
                updateServiceData();
            }
        });
    }

    /**
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        try {
            String backStateName = fragment.getClass().getName();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                //ft.replace(R.id.fragment_container, fragment);
                ft.add(R.id.fragment_container, fragment, backStateName);
                ft.addToBackStack(backStateName);
                ft.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param tag
     * @param isDirectCall
     */
    public void loadFragment(FragTag tag, boolean isDirectCall) {
        switch (tag) {
            case Main:
                replaceFragment(new ContractorMyServicesFragment());
                break;
            case Trade:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorMyServicesTradeFragment());
                break;
            case EmergencyService:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                if (isDirectCall) {
                    selectedTrade = null;
                }
                ContractorMyServicesEmergencyFragment contractorMyServicesEmergencyFragment = new ContractorMyServicesEmergencyFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromTrade", isDirectCall);
                contractorMyServicesEmergencyFragment.setArguments(bundle);
                replaceFragment(contractorMyServicesEmergencyFragment);
                break;
            case EstimateService:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                if (isDirectCall) {
                    selectedTrade = null;
                }
                ContractorMyServicesEstimateFragment contractorMyServicesEstimateFragment = new ContractorMyServicesEstimateFragment();
                Bundle mBundle1 = new Bundle();
                mBundle1.putBoolean("isFromTrade", isDirectCall);
                contractorMyServicesEstimateFragment.setArguments(mBundle1);
                replaceFragment(contractorMyServicesEstimateFragment);
                break;

            case EmergencyTime:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorMyServicesEmergencyTimeFragment());
                break;

            case EstimateTime:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorMyServicesEstimateTimeFragment());
                break;
        }
    }

    public void updateTradeOnly(List<ServiceParam> listEmergencyParam, List<ServiceParam> listEstimateParam) {
        String metaData = "";
        try {
            UpdatePostParam updatePostParam = new UpdatePostParam();
            if (selectedTrade != null) {
                updatePostParam.setTrade_id(selectedTrade.getTrade_id());
            } else {
                updatePostParam.setTrade_id(mTradeSignIn.getTrade_id());
            }
            ArrayList<ServiceParam> emergency_services = new ArrayList<ServiceParam>();
            ArrayList<ServiceParam> estimate_services = new ArrayList<ServiceParam>();
            for (ServiceParam mServiceParam : listEmergencyParam) {
                if (mServiceParam.isSelected == 1) {
                    emergency_services.add(mServiceParam);
                }
            }
            for (ServiceParam mServiceParam : listEstimateParam) {
                if (mServiceParam.isSelected == 1) {
                    estimate_services.add(mServiceParam);
                }
            }
            if (emergency_services.size() == 0 || estimate_services.size() == 0) {
                Utility.uiThreadAlert(context, "Please select some item before update");
                return;
            }
            updatePostParam.setEmergency_services(emergency_services);
            updatePostParam.setEstimate_services(estimate_services);

            String postBody = new Gson().toJson(updatePostParam);
            contractorImpl.updateContractorData(postBody, "tradeServices", true, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEmergency(List<ServiceParam> listParam) {
        String metaData = "";
        try {
            UpdatePostParam updatePostParam = new UpdatePostParam();
            if (selectedTrade != null) {
                updatePostParam.setTrade_id(selectedTrade.getTrade_id());
            } else {
                updatePostParam.setTrade_id(mTradeSignIn.getTrade_id());
            }
            ArrayList<ServiceParam> emergency_services = new ArrayList<ServiceParam>();
            for (ServiceParam mServiceParam : listParam) {
                if (mServiceParam.isSelected == 1) {
                    emergency_services.add(mServiceParam);
                }
            }
            if (emergency_services.size() == 0) {
                Utility.uiThreadAlert(context, "Please select some item before update");
                return;
            }
            updatePostParam.setEmergency_services(emergency_services);
            updatePostParam.setEstimate_services(mTradeSignIn.getEstimate_services());

            String postBody = new Gson().toJson(updatePostParam);
            contractorImpl.updateContractorData(postBody, "tradeServices", false, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEstimate(List<ServiceParam> listParam) {
        String metaData = "";
        try {
            UpdatePostParam updatePostParam = new UpdatePostParam();
            if (selectedTrade != null) {
                updatePostParam.setTrade_id(selectedTrade.getTrade_id());
            } else {
                updatePostParam.setTrade_id(mTradeSignIn.getTrade_id());
            }
            ArrayList<ServiceParam> estimate_services = new ArrayList<ServiceParam>();
            for (ServiceParam mServiceParam : listParam) {
                if (mServiceParam.isSelected == 1) {
                    estimate_services.add(mServiceParam);
                }
            }
            if (estimate_services.size() == 0) {
                Utility.uiThreadAlert(context, "Please select some item before update");
                return;
            }
            updatePostParam.setEmergency_services(mTradeSignIn.getEmergency_services());
            updatePostParam.setEstimate_services(estimate_services);
            String postBody = new Gson().toJson(updatePostParam);
            contractorImpl.updateContractorData(postBody, "tradeServices", false, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTime(String metaData, final String update) {
        contractorImpl.updateServiceTime(metaData, update);
    }

    private void updateServiceData() {
        try {
            contractorImpl = new ContractorImpl();
            contractorImpl.init(context, this);
            mTradeSignIn = contractorImpl.getmContractor().getTrade().get(0);

            Fragment fragment = manager.findFragmentByTag(manager.getBackStackEntryAt(0).getName());
            ((ContractorMyServicesFragment) fragment).initViewLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}