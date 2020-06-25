package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorProfileUpdateActivity;
import com.tradiuus.adapters.ValueArrayAdapter;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Value;
import com.tradiuus.widgets.maskededittext.MaskedEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ContractorProfileCompanyUpdateFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = ContractorProfileCompanyUpdateFragment.class.getSimpleName();
    private Context context;
    public Contractor mContractor;

    private EditText _company_name;
    private EditText _contact_name;
    private MaskedEditText _phone;
    private EditText _web;
    private EditText _license_no;

    private AppCompatSpinner _spn_license;
    private AppCompatSpinner _spn_gl;
    private AppCompatSpinner _spn_insurance;

    private List<Value> valuesLicense;
    private List<Value> valuesGl;
    private List<Value> valuesInsurance;

    private String selectedLicenseTag = "";
    private String selectedGlTag = "";
    private String selectedInsuranceTag = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_company_detail, null);
        this.context = getActivity();
        mContractor = ((ContractorProfileUpdateActivity) getActivity()).contractorImpl.getmContractor();
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
            case R.id.frg_contractor_btn_cmp_next:
                saveEvent();
                break;
        }
    }


    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.company_title)).setText("Update Company Info");
        ((LinearLayout) rootView.findViewById(R.id.customer_trade_section)).setVisibility(View.GONE);
        ((TextView) rootView.findViewById(R.id.frg_contractor_btn_cmp_next)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.frg_contractor_btn_cmp_next)).setText("UPDATE");

        _company_name = (EditText) rootView.findViewById(R.id.frg_contractor_company_name);
        _contact_name = (EditText) rootView.findViewById(R.id.frg_contractor_contant_name);
        _phone = (MaskedEditText) rootView.findViewById(R.id.frg_contractor_phone);
        _web = (EditText) rootView.findViewById(R.id.frg_contractor_web);
        _license_no = (EditText) rootView.findViewById(R.id.frg_contractor_license_no);

        _spn_license = (AppCompatSpinner) rootView.findViewById(R.id.frg_contractor_spn_license);
        _spn_gl = (AppCompatSpinner) rootView.findViewById(R.id.frg_contractor_spn_gl);
        _spn_insurance = (AppCompatSpinner) rootView.findViewById(R.id.frg_contractor_spn_insurance);

        valuesLicense = ((ContractorProfileUpdateActivity) getActivity()).mConfigData.getContractor().getModule_questions().get(0).getValue();
        valuesGl = ((ContractorProfileUpdateActivity) getActivity()).mConfigData.getContractor().getModule_questions().get(1).getValue();
        valuesInsurance = ((ContractorProfileUpdateActivity) getActivity()).mConfigData.getContractor().getModule_questions().get(2).getValue();


        ValueArrayAdapter licenseAdapter = new ValueArrayAdapter(context, valuesLicense);
        licenseAdapter.setSorterText(true);
        _spn_license.setAdapter(licenseAdapter);
        _spn_license.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLicenseTag = valuesLicense.get(position).getOption_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    }

    private void initViewLoad() {
        try {
            _company_name.setText(mContractor.getCompany_name());
            _contact_name.setText(mContractor.getContact_person());
            _phone.setText(mContractor.getMobile_number());
            _web.setText(mContractor.getWebsite_url());
            _license_no.setText(mContractor.getLicense_number());


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedLicenseTag = mContractor.getLicense();
                    _spn_license.setSelection(getSearchIndex(mContractor.getLicense(), valuesLicense));

                    selectedGlTag = mContractor.getGl_insurance();
                    _spn_gl.setSelection(getSearchIndex(mContractor.getGl_insurance(), valuesGl));

                    selectedInsuranceTag = mContractor.getWc_insurance();
                    _spn_insurance.setSelection(getSearchIndex(mContractor.getWc_insurance(), valuesInsurance));
                }
            }, 1000);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSearchIndex(String id, List<Value> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getOption_id().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return 0;
    }

    private void saveEvent() {
        String company_name = _company_name.getText().toString();
        String contact_name = _contact_name.getText().toString();
        String phone = _phone.getUnMaskedText().toString();
        String web = _web.getText().toString();
        String license_no = _license_no.getText().toString();

        if (TextUtils.isEmpty(company_name)
                || TextUtils.isEmpty(contact_name)
                || TextUtils.isEmpty(phone)
                || (!TextUtils.isEmpty(selectedLicenseTag) && selectedLicenseTag.equalsIgnoreCase("3") && TextUtils.isEmpty(license_no))
                || TextUtils.isEmpty(selectedGlTag)
                || TextUtils.isEmpty(selectedInsuranceTag)) {
            Utility.uiThreadAlert(context, getString(R.string.app_name), "Please check all the field values before update", "Ok", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {

                }
            });
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("company_name", company_name);
            jsonObject.put("contact_person", contact_name);
            jsonObject.put("mobile_number", phone);
            jsonObject.put("website_url", web);
            jsonObject.put("license", selectedLicenseTag);
            jsonObject.put("license_number", license_no);
            jsonObject.put("gl_insurance", selectedGlTag);
            jsonObject.put("wc_insurance", selectedInsuranceTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ContractorProfileUpdateActivity) getActivity()).updateProfileData(jsonObject.toString(), "companyInfo", ContractorProfileUpdateActivity.FragTag.COMPANY);
    }
}
