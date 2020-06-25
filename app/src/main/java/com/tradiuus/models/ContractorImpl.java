package com.tradiuus.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tradiuus.TradiuusApp;
import com.tradiuus.helper.NetworkHandleListener;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;
import com.tradiuus.network.postparams.PostParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ContractorImpl {
    public Context context;
    public Contractor mContractor;
    private OnDashboardDataChangedListener onContractorDataChangeListener;
    private OnLuxuryDataChangedListener onLuxuryDataChangedListener;
    private OnMyServiceDataChangedListener onMyServiceDataChangedListener;
    private OnProfileDataChangedListener onProfileDataChangedListener;
    private OnSettingsUpdateListener onSettingsUpdateListener;
    private OnContractorSignUpListener onContractorSignUpListener;
    private OnRequestSyncChangedListener onRequestSyncChangedListener;
    private ConfigData mConfigData;
    private List<Value> value;

    public Contractor getmContractor() {
        return mContractor;
    }

    public void setmContractor(Contractor mContractor) {
        this.mContractor = mContractor;
    }

    public void init(Context context, OnContractorSignUpListener onContractorSignUpListener) {
        this.context = context;
        this.onContractorSignUpListener = onContractorSignUpListener;
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
        value = mConfigData.getCustomer().getModule_questions().get(0).getValue();
    }

    public void init(Context context, OnDashboardDataChangedListener onContractorDataChangeListener) {
        this.context = context;
        this.onContractorDataChangeListener = onContractorDataChangeListener;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    public void init(Context context, OnRequestSyncChangedListener onRequestSyncChangedListener) {
        this.context = context;
        this.onRequestSyncChangedListener = onRequestSyncChangedListener;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    public void init(Context context, OnLuxuryDataChangedListener onLuxuryDataChangedListener) {
        this.context = context;
        this.onLuxuryDataChangedListener = onLuxuryDataChangedListener;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    public void init(Context context, OnMyServiceDataChangedListener onMyServiceDataChangedListener) {
        this.context = context;
        this.onMyServiceDataChangedListener = onMyServiceDataChangedListener;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    public void init(Context context, OnProfileDataChangedListener onProfileDataChangedListener) {
        this.context = context;
        this.onProfileDataChangedListener = onProfileDataChangedListener;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    public void init(Context context, OnSettingsUpdateListener onSettingsUpdateListener) {
        this.context = context;
        this.onSettingsUpdateListener = onSettingsUpdateListener;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    public interface OnDashboardDataChangedListener extends NetworkHandleListener {
        void onDataFetchDone();

        void logoutSuccess(String message);

        void updateWorkingStatus(String msg);

        void failToUpdateWorkingStatus(String message, String msg);
    }

    public interface OnRequestSyncChangedListener extends NetworkHandleListener {
        void onDataFetchDone();
    }

    public interface OnLuxuryDataChangedListener extends NetworkHandleListener {
        void onLuxuryDataFetchDone(ArrayList<Job> jobList, HashMap<String, Job> map);

        void onLuxuryDataAdded(String message);
    }

    public interface OnMyServiceDataChangedListener extends NetworkHandleListener {
        void onUpdateServiceData(boolean callForAll, String message);

        void onUpdateTime(String message);
    }

    public interface OnProfileDataChangedListener extends NetworkHandleListener {
        void onImageChanged(String message);

        void onProfileDataChanged(JSONObject response);

        void onRemoveTechnician(String message);

        void onTechnicianAddUpdate(String message);
    }

    public interface OnSettingsUpdateListener extends NetworkHandleListener {
        void onPasswordUpdateSuccess(String msg);
    }

    public interface OnContractorSignUpListener extends NetworkHandleListener {
        void goToProfilePic(Contractor mContractor);

        void goToContractor(String cardNo);
    }

    public void signUpContractor(PostParam postParam) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }

        try {
            onContractorSignUpListener.onShowPgDialog();
            String postBody = new Gson().toJson(postParam);
            RequestBody formBody = new FormBody.Builder()
                    .add("member_data", postBody)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_SIGN_UP, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    signUpContractorSuccessEvent(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onContractorSignUpListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signUpContractorSuccessEvent(final JSONObject response) {
        try {
            onContractorSignUpListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject userJson = response.getJSONObject("user_info");
                            Contractor mContractor = new Gson().fromJson(userJson.toString(), Contractor.class);
                            mContractor.isCardSave = false;
                            UserPreference.saveContractor(context, mContractor);
                            onContractorSignUpListener.goToProfilePic(mContractor);
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

    public void saveCardInfo(String user_id, String contractor_id, String memberData, String update) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onContractorSignUpListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", user_id)
                    .add("contractor_id", contractor_id)
                    .add("update", update)
                    .add("member_data", memberData)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPDATE_DATA, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onCardInfoSuccessEvent(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onContractorSignUpListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCardInfoSuccessEvent(final JSONObject response) {
        try {
            onContractorSignUpListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            String cardNo = response.getString("credit_card_number");
                            onContractorSignUpListener.goToContractor(cardNo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        onContractorSignUpListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String oldPwd, String newPwd) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onSettingsUpdateListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", mContractor.getUser_id())
                    .add("old_password", oldPwd)
                    .add("new_password", newPwd)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.UPDATE_PASSWORD, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onSettingsUpdateListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onSettingsUpdateListener.onPasswordUpdateSuccess(response.getString("message"));
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
                    onSettingsUpdateListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void getServices() {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            if (onContractorDataChangeListener != null) {
                onContractorDataChangeListener.onShowPgDialog();
            } else if (onRequestSyncChangedListener != null) {
                onRequestSyncChangedListener.onShowPgDialog();
            }
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mContractor.getContractor_id())
                    .add("user_type", String.valueOf(mContractor.getUser_type()))
                    .add("device_type", NetworkController.DEVICE_TYPE)
                    .add("device_token", UserPreference.getFCMToken(context))
                    .build();
            Log.d("CONTRACTOR_JOB_HISTORY","RequestData:"+"api_key="+NetworkController.API_KEY+"user_id="+mContractor.getContractor_id()+"user_type="+String.valueOf(mContractor.getUser_type())+"device_type="+NetworkController.DEVICE_TYPE+"device_token="+UserPreference.getFCMToken(context));
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_JOB_HISTORY, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                  //  Log.d("CONTRACTOR_JOB_HISTORY","Response:"+response);
                    onDataFetchDone(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    if (onContractorDataChangeListener != null) {
                        onContractorDataChangeListener.onHidePgDialog();
                    } else if (onRequestSyncChangedListener != null) {
                        onRequestSyncChangedListener.onHidePgDialog();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    private void onDataFetchDone(final JSONObject response) {
        try {
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject jobsJson = response.getJSONObject("jobs");
                            JSONArray emergency_jobs = jobsJson.getJSONArray("emergency_jobs");
                            JSONArray estimate_jobs = jobsJson.getJSONArray("estimate_jobs");
                            ((TradiuusApp) context.getApplicationContext()).emergencyJobList = new Gson().fromJson(emergency_jobs.toString(), new TypeToken<List<Job>>() {
                            }.getType());
                            ((TradiuusApp) context.getApplicationContext()).estimateJobList = new Gson().fromJson(estimate_jobs.toString(), new TypeToken<List<Job>>() {
                            }.getType());
                            if (onContractorDataChangeListener != null) {
                                onContractorDataChangeListener.onDataFetchDone();
                            } else if (onRequestSyncChangedListener != null) {
                                onRequestSyncChangedListener.onDataFetchDone();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (onContractorDataChangeListener != null) {
                                onContractorDataChangeListener.onHidePgDialog();
                            } else if (onRequestSyncChangedListener != null) {
                                onRequestSyncChangedListener.onHidePgDialog();
                            }
                        }
                        break;
                    default:
                        if (onContractorDataChangeListener != null) {
                            onContractorDataChangeListener.onHidePgDialog();
                            onContractorDataChangeListener.showAlert(response.getString("message"));
                        } else if (onRequestSyncChangedListener != null) {
                            onRequestSyncChangedListener.onHidePgDialog();
                            onRequestSyncChangedListener.showAlert(response.getString("message"));
                        }
                        break;
                }
                if (onContractorDataChangeListener != null) {
                    onContractorDataChangeListener.onHidePgDialog();
                } else if (onRequestSyncChangedListener != null) {
                    onRequestSyncChangedListener.onHidePgDialog();
                }
            }
            if (onContractorDataChangeListener != null) {
                onContractorDataChangeListener.onHidePgDialog();
            } else if (onRequestSyncChangedListener != null) {
                onRequestSyncChangedListener.onHidePgDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onContractorDataChangeListener != null) {
                onContractorDataChangeListener.onHidePgDialog();
            } else if (onRequestSyncChangedListener != null) {
                onRequestSyncChangedListener.onHidePgDialog();
            }
        }
    }

    public void getLuxuryData() {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onLuxuryDataChangedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mContractor.getContractor_id())
                    .add("user_type", String.valueOf(mContractor.getUser_type()))
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_GEO_JOBS, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    luxuryDataFetchSuccess(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onLuxuryDataChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    private void luxuryDataFetchSuccess(final JSONObject response) {
        try {
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONArray jobsJSONArray = response.getJSONArray("jobs");
                            ArrayList<Job> jobList = new Gson().fromJson(jobsJSONArray.toString(), new TypeToken<List<Job>>() {
                            }.getType());
                            HashMap<String, Job> map = new HashMap<>();
                            if (jobList != null) {
                                for (Job job : jobList) {
                                    map.put(job.getJob_id(), job);
                                }
                            }
                            onLuxuryDataChangedListener.onHidePgDialog();
                            onLuxuryDataChangedListener.onLuxuryDataFetchDone(jobList, map);
                        } catch (Exception e) {
                            e.printStackTrace();
                            onLuxuryDataChangedListener.onShowPgDialog();
                            onLuxuryDataChangedListener.showAlert(response.getString("message"));
                        }
                        break;
                    default:
                        onLuxuryDataChangedListener.onHidePgDialog();
                        onLuxuryDataChangedListener.showAlert(response.getString("message"));
                        break;
                }
                onLuxuryDataChangedListener.onHidePgDialog();
            }
            onLuxuryDataChangedListener.onHidePgDialog();
        } catch (Exception e) {
            e.printStackTrace();
            onLuxuryDataChangedListener.onHidePgDialog();
        }
    }

    public void updateContractorData(String metaData, String update, final boolean condition, final String tag) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            if (onProfileDataChangedListener != null) {
                onProfileDataChangedListener.onShowPgDialog();
            } else if (onMyServiceDataChangedListener != null) {
                onMyServiceDataChangedListener.onShowPgDialog();
            }

            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mContractor.getUser_id())
                    .add("contractor_id", mContractor.getContractor_id())
                    .add("update", update)
                    .add("member_data", metaData)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPDATE_DATA, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    updateContractorSuccess(response, condition, tag);
                }

                @Override
                public void onFailure(JSONObject error) {
                    if (onProfileDataChangedListener != null) {
                        onProfileDataChangedListener.onHidePgDialog();
                    } else if (onMyServiceDataChangedListener != null) {
                        onMyServiceDataChangedListener.onHidePgDialog();
                    }
                }
            });

        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    private void updateContractorSuccess(final JSONObject response, final boolean condition, String tag) {
        try {
            if (onProfileDataChangedListener != null) {
                onProfileDataChangedListener.onHidePgDialog();
            } else if (onMyServiceDataChangedListener != null) {
                onMyServiceDataChangedListener.onHidePgDialog();
            }
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            if (onMyServiceDataChangedListener != null) {
                                JSONObject userJson = response.getJSONObject("trade_services_details");
                                JSONArray jsonArray = userJson.getJSONArray("trade");
                                Trade mTradeSignIn = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), Trade.class);
                                mContractor.setTrade(new ArrayList<Trade>());
                                ArrayList<Trade> tradeSignIns = new ArrayList<>();
                                tradeSignIns.add(mTradeSignIn);
                                mContractor.setTrade(tradeSignIns);
                                UserPreference.saveContractor(context, mContractor);
                                onMyServiceDataChangedListener.onUpdateServiceData(condition, response.getString("message"));
                            } else if (onProfileDataChangedListener != null) {
                                if (tag.equalsIgnoreCase("profile")) {
                                    this.onProfileDataChangedListener.onProfileDataChanged(response);
                                } else if (tag.equalsIgnoreCase("addtech")) {
                                    JSONObject technician = response.getJSONObject("technician_details");
                                    Technician mTechnician = new Gson().fromJson(technician.toString(), Technician.class);
                                    if (condition) {
                                        mContractor.getTechnicians().add(mTechnician);
                                    } else {
                                        for (int i = 0; i < mContractor.getTechnicians().size(); i++) {
                                            if (mContractor.getTechnicians().get(i).getTechnician_id().equalsIgnoreCase(mTechnician.getTechnician_id())) {
                                                mContractor.getTechnicians().set(i, mTechnician);
                                                break;
                                            }
                                        }
                                    }
                                    UserPreference.saveContractor(context, mContractor);
                                    onProfileDataChangedListener.onTechnicianAddUpdate(response.getString("message"));
                                } else if (tag.equalsIgnoreCase("images")) {
                                    this.onProfileDataChangedListener.onImageChanged(response.getString("message"));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        if (onProfileDataChangedListener != null) {
                            onProfileDataChangedListener.showAlert(response.getString("message"));
                        } else if (onMyServiceDataChangedListener != null) {
                            onMyServiceDataChangedListener.showAlert(response.getString("message"));
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateServiceTime(String metaData, final String update) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onMyServiceDataChangedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mContractor.getUser_id())
                    .add("contractor_id", mContractor.getContractor_id())
                    .add("update", update)
                    .add("member_data", metaData)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPDATE_DATA, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    updateTimeSuccess(response, (update.equalsIgnoreCase("emergencyAutoResponse")) ? true : false);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onMyServiceDataChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTimeSuccess(final JSONObject response, final boolean callForAll) {
        try {
            onMyServiceDataChangedListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            if (callForAll) {
                                JSONObject userJson = response.getJSONObject("emergency_autoreponse");
                                mContractor.setEmergency_auto_response_time(userJson.getString("emergency_auto_response_time"));
                            } else {
                                JSONObject userJson = response.getJSONObject("estimate_autoreponse");
                                mContractor.setEstimate_auto_response_time(userJson.getString("estimate_auto_response_time"));
                            }
                            UserPreference.saveContractor(context, mContractor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        onMyServiceDataChangedListener.onUpdateTime(response.getString("message"));
                        break;
                    default:
                        onMyServiceDataChangedListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void uploadImages(String insurance_img, String license_img, String company_logo) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onProfileDataChangedListener.onShowPgDialog();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("api_key", NetworkController.API_KEY);
            if (!TextUtils.isEmpty(insurance_img)) {
                builder.addFormDataPart("generic_insurance_img", "generic_insurance_img.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), insurance_img));
            }
            if (!TextUtils.isEmpty(license_img)) {
                builder.addFormDataPart("trade_license_img", "trade_license_img.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), license_img));
            }
            if (!TextUtils.isEmpty(company_logo)) {
                builder.addFormDataPart("company_logo", "company_logo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), company_logo));
            }
            RequestBody formBody = builder.build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPLOAD_IMAGE, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onUploadSuccess(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onProfileDataChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onUploadSuccess(final JSONObject response) {
        try {
            onProfileDataChangedListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject imageDetail = response.getJSONObject("image_info");
                            mTradiuusContractor.setImages(new ImagePostParam(
                                    imageDetail.optString("image_url") + imageDetail.optString("generic_insurance_img"),
                                    imageDetail.optString("image_url") + imageDetail.optString("trade_license_img"),
                                    imageDetail.optString("image_url") + imageDetail.optString("company_logo")
                            ));
                            UserPreference.saveContractor(context, mTradiuusContractor);
                            onProfileDataChangedListener.onProfilePicChanged(response.getString("message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        onProfileDataChangedListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void removeTechnician(final String technician_id) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onProfileDataChangedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mContractor.getUser_id())
                    .add("contractor_id", mContractor.getContractor_id())
                    .add("update", "removeTechnician")
                    .add("technician_id", technician_id)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPDATE_DATA, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onRemoveSuccess(response, technician_id);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onProfileDataChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onRemoveSuccess(JSONObject response, String technician_id) {
        try {
            onProfileDataChangedListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            ArrayList<Technician> newList = new ArrayList<>();
                            for (int i = 0; i < mContractor.getTechnicians().size(); i++) {
                                if (!mContractor.getTechnicians().get(i).getTechnician_id().equalsIgnoreCase(technician_id)) {
                                    newList.add(mContractor.getTechnicians().get(i));
                                }
                            }
                            mContractor.setTechnicians(newList);
                            UserPreference.saveContractor(context, mContractor);
                            onProfileDataChangedListener.onRemoveTechnician(response.getString("message"));
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

    public void addGeoJob(String user_id, String user_type, ArrayList<String> ids) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onLuxuryDataChangedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("user_type", user_type)
                    .add("job_id", ids.toString())
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.TECHNICIAN_ADD_GEOJOB, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onLuxuryDataChangedListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onLuxuryDataChangedListener.onLuxuryDataAdded(response.getString("message"));
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
                    onLuxuryDataChangedListener.onHidePgDialog();
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
            onContractorDataChangeListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_LOGOUT, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onContractorDataChangeListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onContractorDataChangeListener.logoutSuccess(response.getString("message"));
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
                    onContractorDataChangeListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void updateWorkingStatus(String user_id, String user_type, final String status) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onContractorDataChangeListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("user_type", user_type)
                    .add("status", status)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.TECHNICIAN_CHANGE_WORK, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onContractorDataChangeListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONObject user_info = response.getJSONObject("user_info");
                                    mContractor.setStatus(Integer.valueOf(user_info.optString("status")));
                                    UserPreference.saveContractor(context, mContractor);
                                    onContractorDataChangeListener.updateWorkingStatus(response.getString("message"));
                                    break;
                                default:
                                    onContractorDataChangeListener.failToUpdateWorkingStatus(response.getString("message"), status);
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onContractorDataChangeListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }
}
