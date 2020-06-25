package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.CustomerDashboardActivity;
import com.tradiuus.adapters.ValueArrayAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Customer;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Value;
import com.tradiuus.widgets.CustomDrawableEditText;
import com.tradiuus.widgets.CustomEditText;
import com.tradiuus.widgets.CustomTextView;

import java.util.List;

public class CustomerSettingsFragment extends Fragment implements View.OnClickListener {
    private Context context;

    private AppCompatSpinner customer_live_in;
    private AppCompatSeekBar customer_distance_seekbar;

    private CustomEditText setting_edt_old_pwd;
    private CustomDrawableEditText setting_edt_new_pwd;
    private CustomEditText setting_edt_conf_pwd;
    private CustomEditText setting_edt_address;
    private CustomEditText setting_edt_pin;
    private CustomEditText setting_edt_city;
    private CustomTextView title_min;

    private ConfigData mConfigData;
    private List<Value> value;
    private int maxDistance = 1;
    private Value liveIn;
    private int liveInIndex = -1;
    private CustomerImpl customerImpl;
    public Customer customer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_settings, null);
        this.context = getActivity();
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
            case R.id.setting_btn_submit:
                updatePassword();
                break;
            case R.id.setting_btn_submit_others:
                submitLocation();
                break;
        }
    }

    private void initView(View rootView) {
        setting_edt_old_pwd = (CustomEditText) rootView.findViewById(R.id.setting_edt_old_pwd);
        setting_edt_new_pwd = (CustomDrawableEditText) rootView.findViewById(R.id.setting_edt_new_pwd);
        setting_edt_conf_pwd = (CustomEditText) rootView.findViewById(R.id.setting_edt_conf_pwd);

        setting_edt_address = (CustomEditText) rootView.findViewById(R.id.setting_edt_address);
        setting_edt_pin = (CustomEditText) rootView.findViewById(R.id.setting_edt_pin);
        setting_edt_city = (CustomEditText) rootView.findViewById(R.id.setting_edt_city);
        title_min = (CustomTextView) rootView.findViewById(R.id.title_min);

        customer_distance_seekbar = (AppCompatSeekBar) rootView.findViewById(R.id.customer_sign_seekbar);
        customer_live_in = (AppCompatSpinner) rootView.findViewById(R.id.customer_sign_spn);

        setting_edt_pin.setHint("Zip Code");

        ((CustomTextView) rootView.findViewById(R.id.setting_btn_submit)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.setting_btn_submit_others)).setOnClickListener(this);
    }

    public void initViewLoad() {
        try {
            mConfigData = ((TradiuusApp) getActivity().getApplicationContext()).mConfigData;
            value = mConfigData.getCustomer().getModule_questions().get(0).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        customer_distance_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        customer_live_in.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                liveIn = value.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            ValueArrayAdapter mArrayAdapter = new ValueArrayAdapter(context, value, true);
            customer_live_in.setAdapter(mArrayAdapter);

            List<Value> distanceValue = mConfigData.getCustomer().getModule_questions().get(1).getValue();
            customer_distance_seekbar.setMax(Integer.valueOf(distanceValue.get(0).getOption()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            setting_edt_address.setText(customer.getStreet_address());
            setting_edt_pin.setText(customer.getZip_code());
            setting_edt_city.setText(customer.getArea());
            customer_distance_seekbar.setProgress((Integer.valueOf(customer.getMax_distance())));
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).getOption_id().equalsIgnoreCase(customer.getLive_in())) {
                    liveIn = value.get(i);
                    liveInIndex = i;
                    break;
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    customer_live_in.setSelection(liveInIndex);
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void submitLocation() {
        String address = setting_edt_address.getText().toString();
        String city = setting_edt_city.getText().toString();
        String zipcode = setting_edt_pin.getText().toString();
        if (TextUtils.isEmpty(address)
                || TextUtils.isEmpty(city)
                || TextUtils.isEmpty(zipcode)) {
            Utility.uiThreadAlert(context, "Please check all the fields before move to next screen");
            return;
        }
        if (liveIn == null) {
            return;
        }
        ((CustomerDashboardActivity) getActivity()).updateUserLocation(address, city, zipcode, liveIn, maxDistance);
    }


    private void updatePassword() {
        String old = setting_edt_old_pwd.getText().toString();
        String password = setting_edt_new_pwd.getText().toString();
        String conf = setting_edt_conf_pwd.getText().toString();

        if (TextUtils.isEmpty(old)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(conf)) {
            Utility.uiThreadAlert(context, "Please check all the fields before move to next screen");
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
        ((CustomerDashboardActivity) getActivity()).updatePassword(old, password);
    }
}
