package com.tradiuus.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tradiuus.TradiuusApp;
import com.tradiuus.helper.NetworkHandleListener;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CustomerImpl {
    private Context context;
    private OnCustomerSignUpListener onCustomerSignUpListener;
    private OnCustomerGetTradeQuestionnaireListener onGetTradeQuestionnaireListener;
    private OnCustomerDashboardListener onCustomerDashboardListener;
    private ConfigData mConfigData;
    private List<Value> value;
    public List<Trade> trades;

    public void init(Context context, OnCustomerSignUpListener onCustomerSignUpListener) {
        this.context = context;
        this.onCustomerSignUpListener = onCustomerSignUpListener;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        value = mConfigData.getCustomer().getModule_questions().get(0).getValue();
    }

    public void init(Context context, OnCustomerGetTradeQuestionnaireListener onCustomerGetTradeQuestionnaireListener) {
        this.context = context;
        this.onGetTradeQuestionnaireListener = onCustomerGetTradeQuestionnaireListener;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        trades = new ArrayList<>(((TradiuusApp) context.getApplicationContext()).mConfigData.getTrades());
    }

    public void init(Context context, OnCustomerDashboardListener onCustomerDashboardListener) {
        this.context = context;
        this.onCustomerDashboardListener = onCustomerDashboardListener;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        trades = new ArrayList<>(((TradiuusApp) context.getApplicationContext()).mConfigData.getTrades());
    }

    public void signUpCustomer(String fname, String lname, String phone, String address, String city, String zipcode, String email, String password, String liveIn, String maxDistance) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }

        try {
            onCustomerSignUpListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("first_name", fname)
                    .add("last_name", lname)
                    .add("mobile_number", phone)
                    .add("email_id", email)
                    .add("street_address", address)
                    .add("area", city)
                    .add("zip_code", zipcode)
                    .add("password", password)
                    .add("max_distance", maxDistance)
                    .add("live_in", liveIn)
                    .add("user_type", "1")
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_SIGN_UP, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    signUpCustomerSuccessEvent(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onCustomerSignUpListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    private void signUpCustomerSuccessEvent(final JSONObject response) {
        try {
            onCustomerSignUpListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject userJson = response.getJSONObject("user_info");
                            Customer customer = new Gson().fromJson(userJson.toString(), Customer.class);
                            customer.setLng(userJson.optString("long"));//having issue with this key
                            customer.setUser_type("1");
                            UserPreference.saveCustomer(context, customer);
                            onCustomerSignUpListener.goToCustomerPage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        onCustomerSignUpListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnCustomerSignUpListener extends NetworkHandleListener {
        void goToCustomerPage();
    }

    public interface OnCustomerDashboardListener extends NetworkHandleListener {
        void foundTechnicians(ArrayList<Technician> technicians);

        void jobHistory(ArrayList<Job> emergencyJobs, ArrayList<Job> estimateJobs);

        void removeAllTech(String message);

        void locationUpdate(AddressInfo mAddressInfo, String message);

        void onPasswordUpdateSuccess(String message);

        void logoutSuccess(String message);
    }

    public interface OnCustomerGetTradeQuestionnaireListener extends NetworkHandleListener {
        void getTradeQuestionnaire(Question questionnaire);
    }

    public interface OnPromotionalDataListener extends NetworkHandleListener {
        void getPromotions(List<Promotion> promotions);
    }

    public interface OnSuggestionSendListener extends NetworkHandleListener {
        void suggestionSend(String message);
    }

    public void getTradeQuestionnaire(String trade_id, String service_type, String service_id) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }

        try {
            onGetTradeQuestionnaireListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("trade_id", trade_id)
                    .add("service_type", service_type)
                    .add("service_id", service_id)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_TRADE_QUESTIONNAIRE, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    parseTradeQuestionnaire(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onGetTradeQuestionnaireListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    private void parseTradeQuestionnaire(final JSONObject response) {
        try {
            onGetTradeQuestionnaireListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject questionnaireJson = response.getJSONObject("questionnaire");
                            Question questionnaire = new Gson().fromJson(questionnaireJson.toString(), Question.class);
                            onGetTradeQuestionnaireListener.getTradeQuestionnaire(questionnaire);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        onGetTradeQuestionnaireListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCustomerJobHistoryService(String user_id, String user_type) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        RequestBody formBody = new FormBody.Builder()
                .add("api_key", NetworkController.API_KEY)
                .add("user_id", user_id)
                .add("user_type", user_type)
                .add("device_type", NetworkController.DEVICE_TYPE)
                .add("device_token", UserPreference.getFCMToken(context))
                .build();
        NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_JOB_HISTORY, formBody, new NetworkResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!TextUtils.isEmpty(response.toString())) {
                        int responseCode = response.getInt("status");
                        switch (responseCode) {
                            case 1:
                                JSONObject jobJSONObject = response.getJSONObject("jobs");
                                JSONArray emergency_jobs = jobJSONObject.getJSONArray("emergency_jobs");
                                ArrayList<Job> emergencyJobs = new Gson().fromJson(emergency_jobs.toString(), new TypeToken<List<Job>>() {
                                }.getType());
                                JSONArray estimate_jobs = jobJSONObject.getJSONArray("estimate_jobs");
                                ArrayList<Job> estimateJobs = new Gson().fromJson(estimate_jobs.toString(), new TypeToken<List<Job>>() {
                                }.getType());
                                onCustomerDashboardListener.jobHistory(emergencyJobs, estimateJobs);
                                break;
                            default:
                                onCustomerDashboardListener.showAlert(response.getString("message"));
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(JSONObject error) {

            }
        });
    }

    public void getTechnicianNearMe(String customer_id, String user_id, String latitude, String longitude, String page, String distance) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }

        try {
            //onCustomerDashboardListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("customer_id", customer_id)
                    .add("user_id", user_id)
                    /*.add("customer_id", "140")
                    .add("user_id", "601")*/
                    .add("latitude", latitude)
                    .add("longitude", longitude)
                    .add("page", page)
                    .add("distance", distance)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_NEAR_TECHNICIAN, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        //onCustomerDashboardListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    ArrayList<Technician> technicians = new ArrayList<>();
                                    try {

                                        JSONArray techniciansJson = response.getJSONArray("technicians");
                                        technicians = new Gson().fromJson(techniciansJson.toString(), new TypeToken<List<Technician>>() {
                                        }.getType());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (JsonSyntaxException e) {
                                        e.printStackTrace();
                                    }
                                    onCustomerDashboardListener.foundTechnicians(technicians);
                                    break;
                                default:
                                    onCustomerDashboardListener.removeAllTech(response.getString("message"));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    //onCustomerDashboardListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }


    public void getPromotions(final OnPromotionalDataListener listener) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            listener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_PROMOTIONS, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    listener.onHidePgDialog();
                    try {
                        JSONArray promotionsJSONArray = response.getJSONArray("promotions");
                        List<Promotion> promotions = new Gson().fromJson(promotionsJSONArray.toString(), new TypeToken<List<Promotion>>() {
                        }.getType());
                        listener.getPromotions(promotions);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    listener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void sendSuggestion(String customer_id, String user_id, String suggestion, final OnSuggestionSendListener listener) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            listener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("customer_id", customer_id)
                    .add("user_id", user_id)
                    .add("suggestion", suggestion)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_SUGGESTION, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    listener.onHidePgDialog();
                    try {
                        listener.suggestionSend(response.getString("message"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    listener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void changeLocation(String customer_id, String user_id, String street_address,
                               String area, String zip_code, String live_in, String radius) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onCustomerDashboardListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("customer_id", customer_id)
                    .add("user_id", user_id)
                    .add("street_address", street_address)
                    .add("area", area)
                    .add("zip_code", zip_code)
                    .add("live_in", live_in)
                    .add("radius", radius)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_CHANGE_LOCATION, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onCustomerDashboardListener.onHidePgDialog();
                    try {
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONObject addressJson = response.getJSONObject("addressInfo");
                                    AddressInfo mAddressInfo = new Gson().fromJson(addressJson.toString(), AddressInfo.class);
                                    mAddressInfo.setLng(addressJson.optString("long"));//having issue with this key
                                    if (mAddressInfo != null) {
                                        Customer customer = ((TradiuusApp) context.getApplicationContext()).customer;
                                        customer.setArea(mAddressInfo.getArea());
                                        customer.setLat(mAddressInfo.getLat());
                                        customer.setLng(mAddressInfo.getLng());
                                        customer.setMax_distance(mAddressInfo.getRadius());
                                        customer.setFull_address(mAddressInfo.getFull_address());
                                        customer.setStreet_address(mAddressInfo.getStreet_address());
                                        customer.setZip_code(mAddressInfo.getZip_code());
                                        customer.setLive_in(mAddressInfo.getLive_in());
                                        UserPreference.saveCustomer(context, customer);
                                    }
                                    onCustomerDashboardListener.locationUpdate(mAddressInfo, response.getString("message"));
                                    break;
                                default:
                                    onCustomerDashboardListener.showAlert(response.getString("message"));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onCustomerDashboardListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void updatePassword(String user_id, String oldPwd, String newPwd) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onCustomerDashboardListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("old_password", oldPwd)
                    .add("new_password", newPwd)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.UPDATE_PASSWORD, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onCustomerDashboardListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onCustomerDashboardListener.onPasswordUpdateSuccess(response.getString("message"));
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

                @Override
                public void onFailure(JSONObject error) {
                    onCustomerDashboardListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void logoutUser(String user_id) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onCustomerDashboardListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_LOGOUT, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onCustomerDashboardListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onCustomerDashboardListener.logoutSuccess(response.getString("message"));
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

                @Override
                public void onFailure(JSONObject error) {
                    onCustomerDashboardListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }
}
