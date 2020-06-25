package com.tradiuus.models;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.tradiuus.TradiuusApp;
import com.tradiuus.helper.NetworkHandleListener;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;

import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class User extends ViewModel {
    private Context context;
    private OnUserLoginListener onUserLoginListener;
    private String[] userTypes = {"Customer", "Contractor Admin", "Technician"};
    private int lastSelectedIndex = 0;
    private int userType = 0;
    private ConfigData mConfigData;
    private List<Value> value;

    public void init(Context context, OnUserLoginListener onLoginViewModelListener) {
        this.context = context;
        this.onUserLoginListener = onLoginViewModelListener;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        value = mConfigData.getCustomer().getModule_questions().get(0).getValue();
    }


    public int getLastSelectedIndex() {
        return lastSelectedIndex;
    }

    public void setLastSelectedIndex(int lastSelectedIndex) {
        this.lastSelectedIndex = lastSelectedIndex;
    }

    public String[] getUserTypes() {
        return userTypes;
    }

    public void setUserTypes(String[] userTypes) {
        this.userTypes = userTypes;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }


    public void signIn(String email, String password) {
        String type = userType + 1 + "";
        if (TextUtils.isEmpty(email) ) {
            Utility.uiThreadAlert(context, "Warning", "Please enter Email ID", "Ok", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {

                }
            });
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Utility.uiThreadAlert(context, "Warning", "Please enter Password", "Ok", new Utility.OnDialogButtonClick() {
                @Override
                public void onOkayButtonClick() {

                }
            });
            return;
        }


        try {
            if (!NetworkController.isNetworkAvailable(context)) {
                return;
            }
            onUserLoginListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_type", type)
                    .add("email", email)
                    .add("password", password)
                    .add("device_type", NetworkController.DEVICE_TYPE)
                    .add("device_token", UserPreference.getFCMToken(context))
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_SIGN_IN, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onSuccessEvent(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onUserLoginListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    private void onSuccessEvent(final JSONObject response) {
        try {
            onUserLoginListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            if (userType == 0) {
                                JSONObject userJson = response.getJSONObject("user_info");
                                Customer customer = new Gson().fromJson(userJson.toString(), Customer.class);
                                customer.setLng(userJson.optString("long"));//having issue with this key
                                UserPreference.saveCustomer(context, customer);
                                onUserLoginListener.goToCustomerPage();
                            } else if (userType == 1) {
                                JSONObject userJson = response.getJSONObject("user_info");
                                Contractor mContractor = new Gson().fromJson(userJson.toString(), Contractor.class);
                                mContractor.isCardSave = true;
                                UserPreference.saveContractor(context, mContractor);
                                onUserLoginListener.goToContractorPage();
                            } else if (userType == 2) {
                                JSONObject userJson = response.getJSONObject("user_info");
                                Technician mTechnician = new Gson().fromJson(userJson.toString(), Technician.class);
                                UserPreference.saveTechnician(context, mTechnician);
                                onUserLoginListener.goToTechnicianPage();
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        onUserLoginListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forgotPassword(String email) {
        if (TextUtils.isEmpty(email)) {
            return;
        }
        try {
            if (!NetworkController.isNetworkAvailable(context)) {
                return;
            }
            onUserLoginListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_FORGOT_PASSWORD, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onUserLoginListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onUserLoginListener.showAlert(response.getString("message"));
                                    break;
                                default:
                                    onUserLoginListener.showAlert(response.getString("message"));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onUserLoginListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public interface OnUserLoginListener extends NetworkHandleListener {

        void goToContractorPage();

        void goToCustomerPage();

        void goToTechnicianPage();
    }

}
