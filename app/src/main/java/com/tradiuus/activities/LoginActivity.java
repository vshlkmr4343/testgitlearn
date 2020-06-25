package com.tradiuus.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.models.User;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.helper.Utility;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity implements User.OnUserLoginListener {

    private Context context;
    private List<LinearLayout> rdList = new ArrayList<>();
    private CustomProgressDialog pgDialog;
    private AppCompatSpinner spn_user;
    private TextView help;
    private EditText edt_email;
    private EditText edt_password;
    private User user;

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_login);
        this.context = LoginActivity.this;
        user = new User();
        user.init(context, this);
    }

    @Override
    protected void onDestroy() {
        RegisterActivities.removeTop();
        super.onDestroy();
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
    protected void initUIComponent() {
        spn_user = (AppCompatSpinner) findViewById(R.id.login_scr_spn_user);
        rdList.add((LinearLayout) findViewById(R.id.login_scr_rd_customer));
        rdList.add((LinearLayout) findViewById(R.id.login_scr_rd_admin));
        edt_email = (EditText) findViewById(R.id.login_scr_edt_email);
        edt_password = (EditText) findViewById(R.id.login_scr_edt_password);
        help = (TextView) findViewById(R.id.help);
        help.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.login_scr_btn_proceed)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.login_scr_btn_login)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.forgotPasswordButton)).setOnClickListener(this);
        help.setOnClickListener(this);
        spn_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setUserType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            for (int i = 0; i < rdList.size(); i++) {
                LinearLayout rdItem = rdList.get(i);
                rdItem.setTag(i);
                rdItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int index = (int) v.getTag();
                            if (user.getLastSelectedIndex() != index) {
                                rdList.get(index).getChildAt(0).setSelected(true);
                                rdList.get(user.getLastSelectedIndex()).getChildAt(0).setSelected(false);
                                user.setLastSelectedIndex(index);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            rdList.get(0).getChildAt(0).setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initLoadCall() {
        ArrayAdapter mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, user.getUserTypes());
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_user.setAdapter(mArrayAdapter);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.login_scr_btn_proceed:
                proceed();
                break;
            case R.id.login_scr_btn_login:
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                user.signIn(email, password);
                break;
            case R.id.help: {
                startActivity(new Intent(context, HelpActivity.class));
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.forgotPasswordButton:
                forgotPasswordDialog();
                break;
        }
    }


    @Override
    public void goToContractorPage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent mIntent = new Intent(context, ContractorDashboardActivity.class);
                //mIntent.putExtra("loginUser", true);
                startActivity(mIntent);
                overridePendingTransition(0, 0);
                RegisterActivities.removeAllActivities();
            }
        });
    }

    @Override
    public void goToCustomerPage() {
        startActivity(new Intent(context, CustomerDashboardActivity.class));
        overridePendingTransition(0, 0);
        RegisterActivities.removeAllActivities();
    }

    @Override
    public void goToTechnicianPage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, TechnicianDashboardActivity.class));
                overridePendingTransition(0, 0);
                RegisterActivities.removeAllActivities();
            }
        });
    }

    public void proceed() {
        switch (user.getLastSelectedIndex()) {
            case 0:
                startActivity(new Intent(context, CustomerSignupActivity.class));
                overridePendingTransition(0, 0);
                break;
            case 1:
                startActivity(new Intent(context, ContractorSignupActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }

    private void forgotPasswordDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.inflate_dialog_forgotpassword);
            final EditText emailId = (EditText) dialog.findViewById(R.id.emailId);
            ((TextView) dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ((TextView) dialog.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailAdd = emailId.getText().toString();
                    if (!TextUtils.isEmpty(emailAdd)) {
                        user.forgotPassword(emailAdd);
                        dialog.dismiss();
                    }
                }
            });
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
