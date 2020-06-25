package com.tradiuus.activities;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.tradiuus.R;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ContractorImpl;
import com.tradiuus.widgets.CustomDrawableEditText;
import com.tradiuus.widgets.CustomEditText;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;


public class ContractorSettingActivity extends BaseActivity implements ContractorImpl.OnSettingsUpdateListener {


    private Context context;
    private CustomProgressDialog pgDialog;
    public ContractorImpl contractorImpl;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    private CustomEditText setting_edt_old_pwd;
    private CustomDrawableEditText setting_edt_new_pwd;
    private CustomEditText setting_edt_conf_pwd;


    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_setting);
        this.context = ContractorSettingActivity.this;
        contractorImpl = new ContractorImpl();
        contractorImpl.init(context, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);

        setting_edt_old_pwd = (CustomEditText) findViewById(R.id.setting_edt_old_pwd);
        setting_edt_new_pwd = (CustomDrawableEditText) findViewById(R.id.setting_edt_new_pwd);
        setting_edt_conf_pwd = (CustomEditText) findViewById(R.id.setting_edt_conf_pwd);

    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.setting_btn_submit)).setOnClickListener(this);
        contractor_dashboard_back.setOnClickListener(this);
        setting_edt_new_pwd.setDrawableClickListener(new CustomDrawableEditText.DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        Utility.uiThreadAlert(context, "Password", "Password must have a minimum of 8 characters, can only contain alphanumeric character and at least one uppercase");
                        break;
                    default:
                        break;
                }
            }
        });
        try {
            ((CustomTextView) findViewById(R.id.btn_HELP)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        openHelpDialog(context, contractorImpl.mContractor.getFaqs(), contractorImpl.mContractor.getVideos());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.setting_btn_submit:
                submitEvent();
                break;
        }
    }

    @Override
    protected void initLoadCall() {

    }

    @Override
    public void onPasswordUpdateSuccess(String msg) {
        Utility.uiThreadAlert(context, msg, new Utility.OnDialogButtonClick() {
            @Override
            public void onOkayButtonClick() {
                onBackPressed();
            }
        });
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


    private void submitEvent() {
        String edtOldPwd = setting_edt_old_pwd.getText().toString();
        String edtNewPwd = setting_edt_new_pwd.getText().toString();
        String edtConfPwd = setting_edt_conf_pwd.getText().toString();

        if (TextUtils.isEmpty(edtOldPwd) || TextUtils.isEmpty(edtNewPwd) || TextUtils.isEmpty(edtConfPwd)) {
            Utility.uiThreadAlert(context, "Please check all the fields before submit");
            return;
        }

        if (!edtNewPwd.equalsIgnoreCase(edtConfPwd)) {
            Utility.uiThreadAlert(context, "Password miss matched");
            return;
        }
        if (!Utility.isValidPassword(edtNewPwd)) {
            Utility.uiThreadAlert(context, "Password must have a minimum of 8 characters, can only contain alphanumeric character and at least one uppercase");
            return;
        }
        contractorImpl.updatePassword(edtOldPwd, edtNewPwd);
    }
}