package com.tradiuus.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.CustomerEstimateReqConditionFragment;
import com.tradiuus.fragments.CustomerEstimateReqConditionUploadFragment;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Customer;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.ImageImpl;
import com.tradiuus.models.Job;
import com.tradiuus.models.Question;
import com.tradiuus.models.Service;
import com.tradiuus.models.Technician;
import com.tradiuus.models.TechnicianImpl;
import com.tradiuus.models.Trade;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomerEstimateCaseActivity extends BaseActivity implements CustomerImpl.OnCustomerGetTradeQuestionnaireListener, TechnicianImpl.OnTechnicianFundListener, ImageImpl.OnProblemImageUploadListener {

    public Trade selectedTrade;
    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    public CustomerImpl customerImpl;
    private TechnicianImpl mTechnicianImpl;
    public List<Service> services = new ArrayList<Service>();
    public Question questionnaire;
    public Service mService;
    public ConfigData mConfigData;
    public Customer customer;
    private Technician mTechnician = null;

    public String image_1_path;
    public String image_2_path;
    private ImageImpl mImageImpl;
    private ArrayList<String> imagesList = new ArrayList<>();

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_customer_estimate_case);
        this.context = CustomerEstimateCaseActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        customerImpl = new CustomerImpl();
        customerImpl.init(context, this);

        mTechnicianImpl = new TechnicianImpl();
        mTechnicianImpl.init(context, this);
        mImageImpl = new ImageImpl();
        mImageImpl.init(context, this);

        this.customer = ((TradiuusApp) context.getApplicationContext()).customer;
        this.mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            ((TradiuusApp) context.getApplicationContext()).mTechnician = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        RegisterActivities.removeTop();
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setText("Next");
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
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    if (this.mService != null) {
                        replaceFragment(new CustomerEstimateReqConditionUploadFragment());
                    } else {
                        Utility.uiThreadAlert(context, "Warning", "Please select service before edit", "Ok", new Utility.OnDialogButtonClick() {
                            @Override
                            public void onOkayButtonClick() {

                            }
                        });
                    }
                } else if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
                    if (mTechnician != null) {
                        directUploadAndProceed();
                    } else {
                        uploadProblemImages();
                    }
                }

                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            int selectedIndex = getIntent().getExtras().getInt("emType");
            selectedTrade = customerImpl.trades.get(selectedIndex);
            customerImpl.getTradeQuestionnaire(selectedTrade.getTrade_id(), "2", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTradeQuestionnaire(Question questionnaire) {
        this.questionnaire = questionnaire;
        this.services = questionnaire.getServices();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                replaceFragment(new CustomerEstimateReqConditionFragment());
            }
        });
    }

    public void onServiceItemSelect(Service services) {
        this.mService = services;
        this.mService.setService_type_id("2");
    }

    public HashMap<String, Question> qsList = new HashMap<>();

    public void onQSChoose(Question question) {
        qsList.put(question.getQuestion_id(), question);
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
        Utility.uiThreadAlert(context, getString(R.string.app_name), message, "OK", new Utility.OnDialogButtonClick() {
            @Override
            public void onOkayButtonClick() {

            }
        });
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

    public void cancelRequest() {
        overridePendingTransition(0, 0);
        finish();
    }


    @Override
    public void onTechnicianFundSuccess(ArrayList<Technician> list) {

    }

    @Override
    public void onJobAssignedSuccess(final Job job) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("AssignedSuccess -> ", job.getJob_id());
                ((TradiuusApp) getApplication()).job = job;
                startActivity(new Intent(context, CustomerEstimateReqDetailActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    public void onImageUploadSuccess(ArrayList<String> images, String message, boolean directCall) {
        try {
            imagesList.addAll(images);
            if (directCall) {
                submitRequest();
            } else {
                ((TradiuusApp) getApplicationContext()).imagesList = imagesList;
                ((TradiuusApp) getApplicationContext()).mServiceParam = this.mService;
                ((TradiuusApp) getApplicationContext()).qsList = new ArrayList<Question>(this.qsList.values());
                ((TradiuusApp) getApplicationContext()).mTrade = selectedTrade;
                startActivity(new Intent(context, CustomerFindEstimateTechnicianActivity.class));
                overridePendingTransition(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void directUploadAndProceed() {
        if (TextUtils.isEmpty(image_1_path) && TextUtils.isEmpty(image_2_path)) {
            submitRequest();
        } else {
            ArrayList<String> imgList = new ArrayList<>();
            if (!TextUtils.isEmpty(image_1_path)) {
                imgList.add(image_1_path);
            }
            if (!TextUtils.isEmpty(image_2_path)) {
                imgList.add(image_2_path);
            }
            mImageImpl.compressProblemImage(this.customer.getCustomer_id(), imgList, true);
        }
    }

    private void uploadProblemImages() {
        if (TextUtils.isEmpty(image_1_path) && TextUtils.isEmpty(image_2_path)) {
            ((TradiuusApp) getApplicationContext()).mServiceParam = this.mService;
            ((TradiuusApp) getApplicationContext()).qsList = new ArrayList<Question>(this.qsList.values());
            ((TradiuusApp) getApplicationContext()).mTrade = selectedTrade;
            startActivity(new Intent(context, CustomerFindEstimateTechnicianActivity.class));
            overridePendingTransition(0, 0);
        } else {
            ArrayList<String> imgList = new ArrayList<>();
            if (!TextUtils.isEmpty(image_1_path)) {
                imgList.add(image_1_path);
            }
            if (!TextUtils.isEmpty(image_2_path)) {
                imgList.add(image_2_path);
            }
            mImageImpl.compressProblemImage(this.customer.getCustomer_id(), imgList, false);
        }
    }

    private void submitRequest() {
        try {
            JSONObject details = new JSONObject();
            details.put("trade_id", selectedTrade.getTrade_id());
            details.put("service_type", "1");
            if (imagesList != null && imagesList.size() > 0) {
                JSONArray array = new JSONArray();
                for (int i = 0; i < imagesList.size(); i++) {
                    array.put(i, imagesList.get(i));
                }
                details.put("images", array);
            } else {
                details.put("images", new JSONArray());
            }

            JSONObject serviceObject = new JSONObject();
            serviceObject.put("service_id", mService.getService_id());
            JSONArray jsonArray = new JSONArray();

            ArrayList<Question> qsList = new ArrayList<Question>(this.qsList.values());

            try {
                for (int i = 0; i < qsList.size(); i++) {
                    Question question = qsList.get(i);
                    JSONObject questionObject = new JSONObject();
                    questionObject.put("question_id", question.getQuestion_id());
                    questionObject.put("option_id", question.getOption_id());
                    jsonArray.put(i, questionObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serviceObject.put("service_question", jsonArray);

            details.put("service", serviceObject);

            mTechnicianImpl.assignJob(customer.getUser_id(), customer.getCustomer_id(), this.mTechnician.getTechnician_id(), String.valueOf(false), this.mTechnician.getContractor_details().getContractor_id(), details.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}