package com.tradiuus.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.fragments.ContractorCardDetailFragment;
import com.tradiuus.fragments.ContractorCompanyDetailFragment;
import com.tradiuus.fragments.ContractorEstimatePriceFragment;
import com.tradiuus.fragments.ContractorImageUploadFragment;
import com.tradiuus.fragments.ContractorProfilePicFragment;
import com.tradiuus.fragments.ContractorTradeTypeFragment;
import com.tradiuus.helper.LocationHandler;
import com.tradiuus.helper.RegisterActivities;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.ContractorImpl;
import com.tradiuus.models.Image;
import com.tradiuus.models.ImageImpl;
import com.tradiuus.models.Trade;
import com.tradiuus.network.postparams.PostParam;
import com.tradiuus.network.postparams.ServiceParam;
import com.tradiuus.widgets.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContractorSignupActivity extends BaseActivity implements ContractorImpl.OnContractorSignUpListener, ImageImpl.OnImageUploadChangedListener {
    private final static String TAG = ContractorSignupActivity.class.getSimpleName();
    private Context context;
    private CustomProgressDialog pgDialog;
    public PostParam postParam = new PostParam();
    public ConfigData mConfigData;
    public Trade selectedTrade;
    private Contractor mContractor;
    private ContractorImpl contractorImpl;
    private ImageImpl mImageImpl;
    private TextView btnCancel;
    private TextView title;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LatLng currentLatLang;

    public static enum FragTag {
        CompanyDetail, EstimatePrice, ImageUpload, TradeType, ProfilePic, CardDetail, CardSave
    }

    @Override
    protected void setActivityLayout() {
        RegisterActivities.registerActivity(this);
        setContentView(R.layout.activity_contractor_signup);
        this.context = ContractorSignupActivity.this;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
        contractorImpl = new ContractorImpl();
        contractorImpl.init(context, this);

        mImageImpl = new ImageImpl();
        mImageImpl.init(context, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        askPermissionForLocation();
    }

    @Override
    protected void onDestroy() {
        RegisterActivities.removeTop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
            title.setText("SIGN UP as CONTRACTOR");
            btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initUIComponent() {
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        title = (TextView) findViewById(R.id.title);
        btnCancel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initUIListener() {
        btnCancel.setOnClickListener(this);
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initLoadCall() {
        if (mContractor != null && !mContractor.isCardSave) {
            title.setText("CARD INFO");
            replaceFragment(new ContractorCardDetailFragment());
        } else {
            title.setText("SIGN UP as CONTRACTOR");
            replaceFragment(new ContractorCompanyDetailFragment());
        }
//        replaceFragment(new ContractorTradeTypeFragment());
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    public void nextEvent(FragTag tag) {
        switch (tag) {
            case CompanyDetail:
                title.setText("SIGN UP as CONTRACTOR");
                replaceFragment(new ContractorCompanyDetailFragment());
                break;
            case ImageUpload:
                title.setText("SIGN UP as CONTRACTOR");
                replaceFragment(new ContractorImageUploadFragment());
                break;
            case TradeType:
                title.setText("SIGN UP as CONTRACTOR");
                replaceFragment(new ContractorTradeTypeFragment());
                break;
            case EstimatePrice:
                title.setText("SIGN UP as CONTRACTOR");
                replaceFragment(new ContractorEstimatePriceFragment());
                break;
            case ProfilePic:
                title.setText("SIGN UP as CONTRACTOR");
                replaceFragment(new ContractorProfilePicFragment());
                break;
            case CardDetail:
                title.setText("CARD INFO");
                btnCancel.setVisibility(View.GONE);
                replaceFragment(new ContractorCardDetailFragment());
                break;
            case CardSave:

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
        Utility.uiThreadAlert(context, message);
    }

    @Override
    public void goToProfilePic(Contractor contractor) {
        this.mContractor = contractor;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextEvent(ContractorSignupActivity.FragTag.ProfilePic);
            }
        });
    }

    @Override
    public void onImageUploadSuccess(Image image, String message) {
        postParam.setImages(image);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextEvent(ContractorSignupActivity.FragTag.TradeType);
            }
        });
    }

    @Override
    public void onUserPicUploadSuccess(String imagePath, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextEvent(ContractorSignupActivity.FragTag.CardDetail);
            }
        });
    }

    @Override
    public void goToContractor(String cardNo) {
        mContractor.isCardSave = true;
        mContractor.setCardNo(cardNo);
        UserPreference.saveContractor(context, mContractor);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent mIntent = new Intent(context, ContractorDashboardActivity.class);
                mIntent.putExtra("new_user", true);
                startActivity(mIntent);
                overridePendingTransition(0, 0);
                RegisterActivities.removeAllActivities();
            }
        });
    }


    public void backEvent() {
        onBackPressed();
    }

    private void replaceFragment(Fragment fragment) {
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

    public void submitInitial(String priceMax, String priceMin, String totalTime) {
        ArrayList<Trade> tradeServices = selectedTrade.getServices();
        ArrayList<ServiceParam> emergencyServices = new ArrayList<>();
        ArrayList<ServiceParam> estimateServices = new ArrayList<>();
        for (Trade tradeService : tradeServices) {
            if (tradeService.getService_type().equalsIgnoreCase("1")) {
                emergencyServices.add(new ServiceParam(tradeService.getService_id(), priceMax, priceMin, totalTime));
            } else if (tradeService.getService_type().equalsIgnoreCase("2")) {
                estimateServices.add(new ServiceParam(tradeService.getService_id(), priceMax, priceMin, totalTime));
            }
        }
        postParam.setTrade_id(selectedTrade.getTrade_id());
        postParam.setEmergency_services(emergencyServices);
        postParam.setEstimate_services(estimateServices);
        contractorImpl.signUpContractor(postParam);
    }

    public void uploadImageNetwork(String insurance_path, String license_path, String company_path) {
        try {
            mImageImpl.compressAndUploadContractorImages(insurance_path, license_path, company_path, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadPicNetwork(String sourceFile) {
        try {
            mImageImpl.uploadUserPic(mContractor.getUser_id(), sourceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitCardInfo(String card_number, String exp_month, String exp_year, String cvv) {
        String metaData = "";
        try {
            JSONObject cardDetailsJson = new JSONObject();
            JSONObject cardJson = new JSONObject();
            cardJson.put("card_number", card_number);
            cardJson.put("exp_month", exp_month);
            cardJson.put("exp_year", exp_year);
            cardJson.put("cvv", cvv);
            cardDetailsJson.put("card_details", cardJson);
            metaData = cardDetailsJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        contractorImpl.saveCardInfo(mContractor.getUser_id(), mContractor.getContractor_id(), metaData, "creditCardInformation");
    }

    private void askPermissionForLocation() {
        try {
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            try {
                                Log.i(TAG, report.toString());
                                if (report.getDeniedPermissionResponses().size() > 0) {
                                    Utility.uiThreadAlert(context, getString(R.string.msg_permission), new Utility.OnDialogMultiButtonClick() {
                                        @Override
                                        public void onOkayButtonClick() {
                                            askPermissionForLocation();
                                        }

                                        @Override
                                        public void onCancelButtonClick() {
                                            onBackPressed();
                                        }
                                    });
                                } else {
                                    if (!LocationHandler.getInstance(context).isLocationServiceAvailable()) {
                                        LocationHandler.getInstance(context).createLocationServiceError(context);
                                    }
                                    mLocationRequest = new LocationRequest();
                                    mLocationRequest.setInterval(3000);
                                    mLocationRequest.setFastestInterval(3000);
                                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            Log.i(TAG, token.toString());
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        private boolean firstTimeLoad = true;

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            try {
                for (Location location : locationResult.getLocations()) {
                    currentLatLang = new LatLng(location.getLatitude(), location.getLongitude());
                    if (firstTimeLoad) {
                        firstTimeLoad = false;
                        getZipCode(currentLatLang);
                    }
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };

    private void getZipCode(LatLng currentLatLang) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLatLang.latitude, currentLatLang.longitude, 1);
            Log.i(TAG, addresses.size() + "");
            ArrayList<String> zips = new ArrayList<>();
            zips.add(addresses.get(0).getPostalCode());
            postParam.setZip_codes(zips);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}