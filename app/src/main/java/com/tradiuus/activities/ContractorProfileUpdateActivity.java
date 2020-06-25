package com.tradiuus.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.ContractorProfileAvailabilityUpdateFragment;
import com.tradiuus.fragments.ContractorProfileCardUpdateFragment;
import com.tradiuus.fragments.ContractorProfileCompanyUpdateFragment;
import com.tradiuus.fragments.ContractorProfileImagesUpdateFragment;
import com.tradiuus.fragments.ContractorProfileTechnicianUpdateFragment;
import com.tradiuus.fragments.ContractorProfileVideoFragment;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.ContractorImpl;
import com.tradiuus.models.Image;
import com.tradiuus.models.ImageImpl;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ContractorProfileUpdateActivity extends BaseActivity implements ContractorImpl.OnProfileDataChangedListener, ImageImpl.OnImageUploadChangedListener {
    private Context context;
    private CustomProgressDialog pgDialog;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    public ContractorImpl contractorImpl;
    public ImageImpl imageImpl;
    public ConfigData mConfigData;
    private FragTag currentFragmentTagForUpdate;

    public static enum FragTag {
        COMPANY, LOGO, TECHNICIAN, AREA, CARD, VIDEO
    }

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_profile_update);
        this.context = ContractorProfileUpdateActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        contractorImpl = new ContractorImpl();
        contractorImpl.init(context, this);
        imageImpl = new ImageImpl();
        imageImpl.init(context, this);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            Fragment uploadType = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (uploadType != null) {
                uploadType.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;
            case R.id.contractor_dashboard_logut:
                setResult(RESULT_OK, new Intent());
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            FragTag fragTag = FragTag.values()[getIntent().getExtras().getInt("frag_tag")];
            loadFragment(fragTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProfileDataChanged(JSONObject response) {
        updateUserDate(response, currentFragmentTagForUpdate);
    }

    @Override
    public void onImageUploadSuccess(Image image, String message) {
        try {
            if (contractorImpl.getmContractor().getImages() != null) {
                if (!TextUtils.isEmpty(image.getCompany_logo())) {
                    contractorImpl.getmContractor().getImages().setCompany_logo(image.getCompany_logo());
                }
                if (!TextUtils.isEmpty(image.getGeneric_insurance_img())) {
                    contractorImpl.getmContractor().getImages().setGeneric_insurance_img(image.getGeneric_insurance_img());
                }
                if (!TextUtils.isEmpty(image.getTrade_license_img())) {
                    contractorImpl.getmContractor().getImages().setTrade_license_img(image.getTrade_license_img());
                }
                UserPreference.saveContractor(context, contractorImpl.getmContractor());

                JSONObject obj = new JSONObject();
                String generic_insurance_img = contractorImpl.getmContractor().getImages().getGeneric_insurance_img().replace(image.getImage_url(), "");
                obj.put("generic_insurance_img", generic_insurance_img);
                String trade_license_img = contractorImpl.getmContractor().getImages().getTrade_license_img().replace(image.getImage_url(), "");
                obj.put("trade_license_img", trade_license_img);
                String company_logo = contractorImpl.getmContractor().getImages().getCompany_logo().replace(image.getImage_url(), "");
                obj.put("company_logo", company_logo);
                JSONObject objImage = new JSONObject();
                objImage.put("images", obj);
                contractorImpl.updateContractorData(objImage.toString(), "companyImages", false, "images");
            } else {
                contractorImpl.getmContractor().setImages(image);
                UserPreference.saveContractor(context, contractorImpl.getmContractor());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChanged(String message) {
        Utility.uiThreadAlert(context, message, new Utility.OnDialogButtonClick() {
            @Override
            public void onOkayButtonClick() {
                onBackPressed();
            }
        });
    }

    @Override
    public void onUserPicUploadSuccess(String imagePath, String message) {

    }

    @Override
    public void onRemoveTechnician(String message) {
        Utility.uiThreadAlert(context, getString(R.string.app_name), message, "Ok", new Utility.OnDialogButtonClick() {
            @Override
            public void onOkayButtonClick() {
                onBackPressed();
            }
        });
    }

    @Override
    public void onTechnicianAddUpdate(String message) {
        Utility.uiThreadAlert(context, getString(R.string.app_name), message, "Ok", new Utility.OnDialogButtonClick() {
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

    private void replaceFragment(Fragment fragment) {
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

    /**
     * @param tag
     */
    public void loadFragment(FragTag tag) {
        switch (tag) {
            case COMPANY:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorProfileCompanyUpdateFragment());
                break;
            case LOGO:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorProfileImagesUpdateFragment());
                break;
            case TECHNICIAN:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorProfileTechnicianUpdateFragment());
                break;
            case AREA:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorProfileAvailabilityUpdateFragment());
                break;
            case CARD:
                contractor_dashboard_logut.setVisibility(View.VISIBLE);
                replaceFragment(new ContractorProfileCardUpdateFragment());
                break;
            case VIDEO:
                contractor_dashboard_logut.setVisibility(View.INVISIBLE);
                replaceFragment(new ContractorProfileVideoFragment());
                ((TextView) findViewById(R.id.title)).setText("Profile Video");
                break;
        }
    }

    public void updateProfileData(String metaData, String update, final FragTag fragTag) {
        currentFragmentTagForUpdate = fragTag;
        contractorImpl.updateContractorData(metaData, update, false, "profile");
    }

    public void uploadImageNetwork(String insurance_path, String license_path, String company_path) {
        imageImpl.compressAndUploadContractorImages(insurance_path, license_path, company_path, contractorImpl.mContractor.getUser_id(), contractorImpl.mContractor.getContractor_id());
    }

    public void removeTechnician(final String id) {
        contractorImpl.removeTechnician(id);
    }

    public void technicianAddUpdate(String metaData, String update, final boolean isNew) {
        contractorImpl.updateContractorData(metaData, update, isNew, "addtech");
    }

    private void updateUserDate(JSONObject response, FragTag fragTag) {
        try {
            switch (fragTag) {
                case COMPANY:
                    try {
                        JSONObject jsonCompanyObject = response.getJSONObject("company_info");
                        contractorImpl.getmContractor().setCompany_name(jsonCompanyObject.getString("company_name"));
                        contractorImpl.getmContractor().setContact_person(jsonCompanyObject.getString("contact_person"));
                        contractorImpl.getmContractor().setMobile_number(jsonCompanyObject.getString("mobile_number"));
                        contractorImpl.getmContractor().setWebsite_url(jsonCompanyObject.getString("website_url"));
                        contractorImpl.getmContractor().setLicense_number(jsonCompanyObject.getString("license_number"));
                        contractorImpl.getmContractor().setLicense(jsonCompanyObject.getString("license"));
                        contractorImpl.getmContractor().setGl_insurance(jsonCompanyObject.getString("gl_insurance"));
                        contractorImpl.getmContractor().setWc_insurance(jsonCompanyObject.getString("wc_insurance"));
                        UserPreference.saveContractor(context, contractorImpl.getmContractor());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case LOGO:
                    break;

                case TECHNICIAN:
                    break;

                case AREA:
                    try {
                        JSONObject jsonObject = response.getJSONObject("availability_info");
                        contractorImpl.getmContractor().setAvailable_trucks(jsonObject.getString("available_trucks"));
                        contractorImpl.getmContractor().setBusiness_end_time(jsonObject.getString("business_end_time"));
                        contractorImpl.getmContractor().setBusiness_start_time(jsonObject.getString("business_start_time"));
                        JSONArray zipArray = jsonObject.getJSONArray("zip_codes");
                        ArrayList<String> zipArrayList = new ArrayList<>();
                        for (int i = 0; i < zipArray.length(); i++) {
                            zipArrayList.add(zipArray.getString(i));
                        }
                        contractorImpl.getmContractor().setZip_codes(zipArrayList);
                        UserPreference.saveContractor(context, contractorImpl.getmContractor());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case CARD:
                    try {
                        String cardNo = response.getString("credit_card_number");
                        contractorImpl.getmContractor().isCardSave = true;
                        contractorImpl.getmContractor().setCardNo(cardNo);
                        UserPreference.saveContractor(context, contractorImpl.getmContractor());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case VIDEO:
                    break;
            }
            Utility.uiThreadAlert(context, getString(R.string.app_name), response.getString("message"), "Ok", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {
                    onBackPressed();
                }
            });
            currentFragmentTagForUpdate = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}