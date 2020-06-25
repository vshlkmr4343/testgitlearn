package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorSignupActivity;
import com.tradiuus.adapters.ValueArrayAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Value;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;
import com.tradiuus.widgets.CustomDrawableEditText;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.maskededittext.MaskedEditText;

import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ContractorCompanyDetailFragment extends Fragment implements View.OnClickListener {

    private Context context;
    public static ContractorCompanyDetailFragment fragment;
    private CustomProgressDialog pgDialog;

    private EditText _company_name;
    private EditText _contact_name;
    private MaskedEditText _phone;
    private EditText _web;
    private EditText _email;
    private CustomDrawableEditText _pwd;
    private EditText _conf_pwd;
    private EditText _license_no;

    private AppCompatSpinner _spn_license;
    private AppCompatSpinner _spn_gl;
    private AppCompatSpinner _spn_insurance;

    private List<Value> valuesLicense;
    private List<Value> valuesGl;
    private List<Value> valuesInsurance;

    private int selectedLicenseTagIndex = 0;
    private String selectedLicenseTag = "";
    private String selectedGlTag = "";
    private String selectedInsuranceTag = "";

    public static ContractorCompanyDetailFragment newInstance(int position, Context context) {
        if (fragment == null)
            fragment = new ContractorCompanyDetailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_company_detail, null);
        this.context = getActivity();

        initView(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        fragment = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_contractor_btn_cmp_next:
                nextEvent();
                break;
        }
    }

    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.frg_contractor_btn_cmp_next)).setOnClickListener(this);

        _company_name = (EditText) rootView.findViewById(R.id.frg_contractor_company_name);
        _contact_name = (EditText) rootView.findViewById(R.id.frg_contractor_contant_name);
        _phone = (MaskedEditText) rootView.findViewById(R.id.frg_contractor_phone);
        _web = (EditText) rootView.findViewById(R.id.frg_contractor_web);
        _email = (EditText) rootView.findViewById(R.id.frg_contractor_email);
        _pwd = (CustomDrawableEditText) rootView.findViewById(R.id.frg_contractor_pwd);
        _conf_pwd = (EditText) rootView.findViewById(R.id.frg_contractor_conf_pwd);
        _license_no = (EditText) rootView.findViewById(R.id.frg_contractor_license_no);

        _spn_license = (AppCompatSpinner) rootView.findViewById(R.id.frg_contractor_spn_license);
        _spn_gl = (AppCompatSpinner) rootView.findViewById(R.id.frg_contractor_spn_gl);
        _spn_insurance = (AppCompatSpinner) rootView.findViewById(R.id.frg_contractor_spn_insurance);

        valuesLicense = ((ContractorSignupActivity) getActivity()).mConfigData.getContractor().getModule_questions().get(0).getValue();
        valuesGl = ((ContractorSignupActivity) getActivity()).mConfigData.getContractor().getModule_questions().get(1).getValue();
        valuesInsurance = ((ContractorSignupActivity) getActivity()).mConfigData.getContractor().getModule_questions().get(2).getValue();


        ValueArrayAdapter licenseAdapter = new ValueArrayAdapter(context, valuesLicense);
        licenseAdapter.setSorterText(true);
        _spn_license.setAdapter(licenseAdapter);
        _spn_license.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLicenseTagIndex = position;
                selectedLicenseTag = valuesLicense.get(position).getOption_id();
                _license_no.setText("");
                _license_no.setEnabled((position == 1 || position == 2) ? false  : true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Noting selected","");
            }
        });

        ValueArrayAdapter glAdapter = new ValueArrayAdapter(context, valuesGl);
        _spn_gl.setAdapter(glAdapter);
        _spn_gl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGlTag = valuesGl.get(position).getOption_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ValueArrayAdapter insuranceAdapter = new ValueArrayAdapter(context, valuesInsurance);
        _spn_insurance.setAdapter(insuranceAdapter);
        _spn_insurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedInsuranceTag = valuesInsurance.get(position).getOption_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        _pwd.setDrawableClickListener(new CustomDrawableEditText.DrawableClickListener() {
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

    private void nextEvent() {
        String company_name = _company_name.getText().toString();
        String contact_name = _contact_name.getText().toString();
        String phone = _phone.getUnMaskedText().toString();
        String web = _web.getText().toString();
        String email = _email.getText().toString();
        String pwd = _pwd.getText().toString();
        String conf_pwd = _conf_pwd.getText().toString();
        String license_no = _license_no.getText().toString();

        if (TextUtils.isEmpty(company_name)
                || TextUtils.isEmpty(contact_name)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(pwd)
                || TextUtils.isEmpty(conf_pwd)
                || TextUtils.isEmpty(selectedLicenseTag)
                || TextUtils.isEmpty(selectedGlTag)
                || TextUtils.isEmpty(selectedInsuranceTag)) {
            Utility.uiThreadAlert(context, "Please check all the field values before hit the next button");
            return;
        }

        if (selectedLicenseTagIndex == 0 && TextUtils.isEmpty(license_no)) {
            Utility.uiThreadAlert(context, "Please provide license details");
            return;
        }

        if (!pwd.equalsIgnoreCase(conf_pwd)) {
            Utility.uiThreadAlert(context, "Password miss matched");
            return;
        }

        if (!Utility.isValidPassword(conf_pwd)) {
            Utility.uiThreadAlert(context, "Password must have a minimum of 8 characters, can only contain alphanumeric character and at least one uppercase");
            return;
        }

        ((ContractorSignupActivity) getActivity()).postParam.setCompany_name(company_name);
        ((ContractorSignupActivity) getActivity()).postParam.setContact_person(contact_name);
        ((ContractorSignupActivity) getActivity()).postParam.setMobile_number(phone);
        ((ContractorSignupActivity) getActivity()).postParam.setWebsite_url(web);
        ((ContractorSignupActivity) getActivity()).postParam.setEmail_id(email);
        ((ContractorSignupActivity) getActivity()).postParam.setPassword(pwd);
        ((ContractorSignupActivity) getActivity()).postParam.setLicense_number(license_no);
        ((ContractorSignupActivity) getActivity()).postParam.setLicense(selectedLicenseTag);
        ((ContractorSignupActivity) getActivity()).postParam.setGl_insurance(selectedGlTag);
        ((ContractorSignupActivity) getActivity()).postParam.setWc_insurance(selectedInsuranceTag);

        pgDialog = new CustomProgressDialog(context);
        pgDialog.prepareAndShowDialog();


        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", pwd)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CHECK_EMAIL, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onSuccessEvent(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgDialog.dismissDialog();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }


    private void onSuccessEvent(final JSONObject response) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog.dismissDialog();
                }
            });
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ContractorSignupActivity) getActivity()).nextEvent(ContractorSignupActivity.FragTag.ImageUpload);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Utility.uiThreadAlert(context, response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}