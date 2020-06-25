package com.tradiuus.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.adapters.ValueArrayAdapter;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Value;
import com.tradiuus.widgets.CustomDrawableEditText;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;
import com.tradiuus.widgets.maskededittext.MaskedEditText;

import java.util.List;

public class CustomerSignupActivity extends BaseActivity implements CustomerImpl.OnCustomerSignUpListener {

    private Context context;
    private CustomProgressDialog pgDialog;
    private AppCompatSpinner customer_sign_spn;
    private AppCompatSeekBar customer_sign_seekbar;
    private EditText customer_sign_fname;
    private EditText customer_sign_lname;
    private MaskedEditText customer_sign_ph;
    private EditText customer_sign_add;
    private EditText customer_sign_city;
    private EditText customer_sign_zipcode;
    private EditText customer_sign_email;
    private CustomDrawableEditText customer_sign_password;
    private EditText customer_sign_conf_password;
    private TextView title_min;
    private TextView btnCancel;

    private ConfigData mConfigData;
    private List<Value> value;
    private int maxDistance = 1;
    private Value liveIn;
    private CustomerImpl customerImpl;

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_customer_signup);
        this.context = CustomerSignupActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        value = mConfigData.getCustomer().getModule_questions().get(0).getValue();
        customerImpl = new CustomerImpl();
        customerImpl.init(context, this);
    }

    @Override
    protected void onDestroy() {
        RegisterActivities.removeTop();
        super.onDestroy();
    }

    @Override
    protected void initUIComponent() {
        customer_sign_seekbar = (AppCompatSeekBar) findViewById(R.id.customer_sign_seekbar);
        customer_sign_spn = (AppCompatSpinner) findViewById(R.id.customer_sign_spn);
        customer_sign_fname = (EditText) findViewById(R.id.customer_sign_fname);
        customer_sign_lname = (EditText) findViewById(R.id.customer_sign_lname);
        customer_sign_ph = (MaskedEditText) findViewById(R.id.customer_sign_ph);
        customer_sign_add = (EditText) findViewById(R.id.customer_sign_add);
        customer_sign_city = (EditText) findViewById(R.id.customer_sign_city);
        customer_sign_zipcode = (EditText) findViewById(R.id.customer_sign_zipcode);
        customer_sign_email = (EditText) findViewById(R.id.customer_sign_email);
        customer_sign_password = (CustomDrawableEditText) findViewById(R.id.customer_sign_password);
        customer_sign_conf_password = (EditText) findViewById(R.id.customer_sign_conf_password);
        title_min = (TextView) findViewById(R.id.title_min);

        btnCancel = (TextView) findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initUIListener() {
        ((CustomTextView) findViewById(R.id.login_scr_btn_proceed)).setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        customer_sign_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxDistance = progress;
                title_min.setText(maxDistance + " mi");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        customer_sign_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveIn = value.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        customer_sign_password.setDrawableClickListener(new CustomDrawableEditText.DrawableClickListener() {
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
    }

    @Override
    protected void initLoadCall() {
        try {
            ValueArrayAdapter mArrayAdapter = new ValueArrayAdapter(context, value, true);
            customer_sign_spn.setAdapter(mArrayAdapter);

            List<Value> distanceValue = mConfigData.getCustomer().getModule_questions().get(1).getValue();
            customer_sign_seekbar.setMax(Integer.valueOf(distanceValue.get(0).getOption()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.login_scr_btn_proceed:
                signUpButtonEvent();
                break;
            case R.id.btnCancel:
                overridePendingTransition(0, 0);
                finish();
                break;
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

    @Override
    public void showAlert(String message) {
        try {
            Utility.uiThreadAlert(context, getString(R.string.app_name), message, "Ok", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signUpButtonEvent() {
        String fname = customer_sign_fname.getText().toString();
        String lname = customer_sign_lname.getText().toString();
        String phone = customer_sign_ph.getUnMaskedText().toString();
        String address = customer_sign_add.getText().toString();
        String city = customer_sign_city.getText().toString();
        String zipcode = customer_sign_zipcode.getText().toString();
        String email = customer_sign_email.getText().toString();
        String password = customer_sign_password.getText().toString();
        String conf = customer_sign_conf_password.getText().toString();
        if (TextUtils.isEmpty(fname)
                || TextUtils.isEmpty(lname)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(city)
                || TextUtils.isEmpty(zipcode)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(conf)) {
            Utility.uiThreadAlert(context, "Please check all the fields before move to next screen");
            return;
        }

        if (liveIn == null) {
            return;
        }

        if (!password.equalsIgnoreCase(conf)) {
            Utility.uiThreadAlert(context, "Password miss matched");
            return;
        }
        if (!Utility.isValidPassword(password)) {
            Utility.uiThreadAlert(context, "Password must have a minimum of 8 characters, can only contain alphanumeric character and at least one uppercase");
            return;
        }
        customerImpl.signUpCustomer(fname, lname, phone, address, city, zipcode, email, password, liveIn.getOption_id() + "", maxDistance + "");
    }

    @Override
    public void goToCustomerPage() {
        Intent mIntent = new Intent(context, CustomerDashboardActivity.class);
        mIntent.putExtra("newReg", true);
        startActivity(mIntent);
        overridePendingTransition(0, 0);
        RegisterActivities.removeAllActivities();
    }
}