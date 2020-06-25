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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class TechnicianImpl {

    private Context context;
    public Technician mTechnician;
    private OnTechnicianDataChangedListener onTechnicianDataChangedListener;
    private OnTechnicianLuxuryDataChangedListener onTechnicianLuxuryDataChangedListener;
    private OnSettingsUpdateListener onSettingsUpdateListener;
    private OnTechnicianFundListener onTechnicianFundListener;
    private OnJobCancelListener onJobCancelListener;
    private OnTechnicianJobCompletedListener onTechnicianJobCompletedListener;

    public void init(Context context, OnTechnicianDataChangedListener onTechnicianDataChangedListener) {
        this.context = context;
        this.onTechnicianDataChangedListener = onTechnicianDataChangedListener;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    public void init(Context context, OnTechnicianLuxuryDataChangedListener onTechnicianLuxuryDataChangedListener) {
        this.context = context;
        this.onTechnicianLuxuryDataChangedListener = onTechnicianLuxuryDataChangedListener;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    public void init(Context context, OnSettingsUpdateListener onSettingsUpdateListener) {
        this.context = context;
        this.onSettingsUpdateListener = onSettingsUpdateListener;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    public void init(Context context, OnTechnicianFundListener onTechnicianFundListener) {
        this.context = context;
        this.onTechnicianFundListener = onTechnicianFundListener;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    public void init(Context context, OnJobCancelListener onJobCancelListener) {
        this.context = context;
        this.onJobCancelListener = onJobCancelListener;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    public void init(Context context, OnTechnicianJobCompletedListener onTechnicianJobCompletedListener) {
        this.context = context;
        this.onTechnicianJobCompletedListener = onTechnicianJobCompletedListener;
        mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
    }

    public interface OnTechnicianDataChangedListener extends NetworkHandleListener {
        void onDataFetchDone();

        void updateWorkingStatus(String msg);

        void failToUpdateWorkingStatus(String message, String msg);

        void logoutSuccess(String msg);
    }

    public interface OnTechnicianLuxuryDataChangedListener extends NetworkHandleListener {
        void onLuxuryDataFetchDone(ArrayList<Job> jobList, HashMap<String, Job> map);

        void onLuxuryDataAdded(String message);
    }

    public interface OnSettingsUpdateListener extends NetworkHandleListener {
        void onPasswordUpdateSuccess(String msg);
    }

    public interface OnTechnicianFundListener extends NetworkHandleListener {
        void onTechnicianFundSuccess(ArrayList<Technician> list);

        void onJobAssignedSuccess(Job job);
    }

    public interface OnTechnicianJobCompletedListener extends NetworkHandleListener {
        void onJobCompletedSuccess(String job_id, String status, String status_value, int index);

        void onDataSetRefreshed();
    }

    public interface OnJobCancelListener extends NetworkHandleListener {
        void onJobCancelSuccess(Job job, String msg);
    }

    public void getServicesData() {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            if (onTechnicianJobCompletedListener != null) {
                onTechnicianJobCompletedListener.onShowPgDialog();
            } else if (onTechnicianDataChangedListener != null) {
                onTechnicianDataChangedListener.onShowPgDialog();
            }
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mTechnician.getTechnician_id())
                    .add("user_type", String.valueOf(mTechnician.getUser_type()))
                    .add("device_type", NetworkController.DEVICE_TYPE)
                    .add("device_token", UserPreference.getFCMToken(context))
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_JOB_HISTORY, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    onDataFetchDone(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    if (onTechnicianJobCompletedListener != null) {
                        onTechnicianJobCompletedListener.onHidePgDialog();
                    } else if (onTechnicianDataChangedListener != null) {
                        onTechnicianDataChangedListener.onHidePgDialog();
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
                            if (onTechnicianJobCompletedListener != null) {
                                onTechnicianJobCompletedListener.onDataSetRefreshed();
                                onTechnicianJobCompletedListener.onHidePgDialog();
                            } else if (onTechnicianDataChangedListener != null) {
                                onTechnicianDataChangedListener.onDataFetchDone();
                                onTechnicianDataChangedListener.onHidePgDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (onTechnicianJobCompletedListener != null) {
                                onTechnicianJobCompletedListener.onHidePgDialog();
                            } else if (onTechnicianDataChangedListener != null) {
                                onTechnicianDataChangedListener.onHidePgDialog();
                            }
                        }
                        break;
                    default:
                        onTechnicianDataChangedListener.showAlert(response.getString("message"));
                        onTechnicianDataChangedListener.onHidePgDialog();
                        break;
                }
                if (onTechnicianJobCompletedListener != null) {
                    onTechnicianJobCompletedListener.onHidePgDialog();
                } else if (onTechnicianDataChangedListener != null) {
                    onTechnicianDataChangedListener.onHidePgDialog();
                }
            }
            if (onTechnicianJobCompletedListener != null) {
                onTechnicianJobCompletedListener.onHidePgDialog();
            } else if (onTechnicianDataChangedListener != null) {
                onTechnicianDataChangedListener.onHidePgDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onTechnicianJobCompletedListener != null) {
                onTechnicianJobCompletedListener.onHidePgDialog();
            } else if (onTechnicianDataChangedListener != null) {
                onTechnicianDataChangedListener.onHidePgDialog();
            }
        }
    }

    public void getLuxuryData() {
        try {
            onTechnicianLuxuryDataChangedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("user_id", mTechnician.getTechnician_id())
                    .add("user_type", String.valueOf(mTechnician.getUser_type()))
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_GEO_JOBS, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    luxuryDataFetchSuccess(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    onTechnicianLuxuryDataChangedListener.onHidePgDialog();
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

                            onTechnicianLuxuryDataChangedListener.onHidePgDialog();
                            onTechnicianLuxuryDataChangedListener.onLuxuryDataFetchDone(jobList, map);

                        } catch (Exception e) {
                            e.printStackTrace();
                            onTechnicianLuxuryDataChangedListener.onHidePgDialog();
                        }
                        break;
                    default:
                        onTechnicianLuxuryDataChangedListener.onHidePgDialog();
                        onTechnicianLuxuryDataChangedListener.showAlert(response.getString("message"));
                        break;
                }
                onTechnicianLuxuryDataChangedListener.onHidePgDialog();
            }
            onTechnicianLuxuryDataChangedListener.onHidePgDialog();
        } catch (Exception e) {
            e.printStackTrace();
            onTechnicianLuxuryDataChangedListener.onHidePgDialog();
        }
    }

    public void updatePassword(String oldPwd, String newPwd) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onSettingsUpdateListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", mTechnician.getUser_id())
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


    public void getAvailableTechnician(String user_id, String customer_id, String service_id, String service_type, String technician_id,
                                       String latitude, String longitude, String distance) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onTechnicianFundListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("customer_id", customer_id)
                    .add("service_id", service_id)
                    .add("service_type", service_type)
                    .add("technician_id", technician_id)
                    .add("latitude", latitude)
                    .add("longitude", longitude)
                    .add("distance", distance)
                    .add("page", "1")
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_NEAR_TECHNICIAN, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onTechnicianFundListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONArray jobsJSONArray = response.getJSONArray("technicians");
                                    ArrayList<Technician> technicians = new Gson().fromJson(jobsJSONArray.toString(), new TypeToken<List<Technician>>() {
                                    }.getType());
                                    onTechnicianFundListener.onTechnicianFundSuccess(technicians);
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
                    onTechnicianFundListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public void assignJob(String user_id, String customer_id, String technician_id, String direct_call, String contractor_id, String questionnaire_details) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onTechnicianFundListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("customer_id", customer_id)
                    .add("technician_id", technician_id)
                    .add("direct_call", direct_call)
                    .add("contractor_id", contractor_id)
                    .add("questionnaire_details", questionnaire_details)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.TECHNICIAN_ASSIGN_JOB, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onTechnicianFundListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONObject job_info = response.getJSONObject("job_info");
                                    Job job = new Gson().fromJson(job_info.toString(), Job.class);
                                    onTechnicianFundListener.onJobAssignedSuccess(job);
                                    break;
                                default:
                                    Utility.uiThreadAlert(context, response.getString("message"));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onTechnicianFundListener.onHidePgDialog();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onTechnicianFundListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
            onTechnicianFundListener.onHidePgDialog();
        }
    }

    public void cancelJob(String customer_id, String job_id, String reason_id, String technician_id) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onJobCancelListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("api_key", NetworkController.API_KEY)
                    .add("customer_id", customer_id)
                    .add("job_id", job_id)
                    .add("reason_id", reason_id)
                    .add("technician_id", technician_id)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.TECHNICIAN_CANCEL_JOB, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onJobCancelListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONObject job_info = response.getJSONObject("job_info");
                                    Job job = new Gson().fromJson(job_info.toString(), Job.class);
                                    onJobCancelListener.onJobCancelSuccess(job, response.getString("message"));
                                    break;
                                default:
                                    Utility.uiThreadAlert(context, response.getString("message"));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onJobCancelListener.onHidePgDialog();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onJobCancelListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
            onJobCancelListener.onHidePgDialog();
        }
    }

    public void logoutUser(String user_id) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onTechnicianDataChangedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", user_id)
                    .add("api_key", NetworkController.API_KEY)
                    .build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_LOGOUT, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onTechnicianDataChangedListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onTechnicianDataChangedListener.logoutSuccess(response.getString("message"));
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
                    onTechnicianDataChangedListener.onHidePgDialog();
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
            onTechnicianDataChangedListener.onShowPgDialog();
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
                        onTechnicianDataChangedListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONObject user_info = response.getJSONObject("user_info");
                                    Technician mTechnician = ((TradiuusApp) context.getApplicationContext()).mTechnician;
                                    mTechnician.setStatus(user_info.optString("status"));
                                    UserPreference.saveTechnician(context, mTechnician);
                                    onTechnicianDataChangedListener.updateWorkingStatus(response.getString("message"));
                                    break;
                                default:
                                    onTechnicianDataChangedListener.failToUpdateWorkingStatus(response.getString("message"), status);
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onTechnicianDataChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }


    public void addGeoJob(String user_id, String user_type, ArrayList<String> ids) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onTechnicianLuxuryDataChangedListener.onShowPgDialog();
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
                        onTechnicianLuxuryDataChangedListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    onTechnicianLuxuryDataChangedListener.onLuxuryDataAdded(response.getString("message"));
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
                    onTechnicianLuxuryDataChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
        }
    }

    public interface OnJobTechnicianDetailListener {
        void onTechnicianFound(Technician technician);

        void onFail();
    }

    public void getCurrentJobTechnicianDetails(String user_id, String user_type, final boolean isEmergencyJob, final String jobId, final OnJobTechnicianDetailListener onJobTechnicianDetailListener) {
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

                                if (isEmergencyJob) {
                                    JSONArray emergency_jobs = jobJSONObject.getJSONArray("emergency_jobs");
                                    ArrayList<Job> emergencyJobs = new Gson().fromJson(emergency_jobs.toString(), new TypeToken<List<Job>>() {
                                    }.getType());
                                    if (emergencyJobs != null && emergencyJobs.size() > 0) {
                                        Technician technician = null;
                                        for (Job job : emergencyJobs) {
                                            if (job.getJob_id().equalsIgnoreCase(jobId)) {
                                                technician = job.getTechnician().get(0);
                                                break;
                                            }
                                        }
                                        if (technician != null) {
                                            onJobTechnicianDetailListener.onTechnicianFound(technician);
                                        }
                                    }
                                } else {
                                    JSONArray estimate_jobs = jobJSONObject.getJSONArray("estimate_jobs");
                                    ArrayList<Job> estimateJobs = new Gson().fromJson(estimate_jobs.toString(), new TypeToken<List<Job>>() {
                                    }.getType());
                                    if (estimateJobs != null && estimateJobs.size() > 0) {
                                        Technician technician = null;
                                        for (Job job : estimateJobs) {
                                            if (job.getJob_id().equalsIgnoreCase(jobId)) {
                                                technician = job.getTechnician().get(0);
                                                break;
                                            }
                                        }
                                        if (technician != null) {
                                            onJobTechnicianDetailListener.onTechnicianFound(technician);
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(JSONObject error) {
                onJobTechnicianDetailListener.onFail();
            }
        });
    }

    public void completeJob(String customer_id, final String technician_id, String job_id, final String service_type, final int index) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onTechnicianJobCompletedListener.onShowPgDialog();
            RequestBody formBody = new FormBody.Builder()
                    .add("customer_id", customer_id)
                    .add("technician_id", technician_id)
                    .add("job_id", job_id)
                    .add("service_type", service_type)
                    .add("api_key", NetworkController.API_KEY)
                    .build();

            NetworkController.postBase(NetworkController.NetworkRoutes.TECHNICIAN_COMPLETE_JOB, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onTechnicianJobCompletedListener.onHidePgDialog();
                        if (!TextUtils.isEmpty(response.toString())) {
                            int responseCode = response.getInt("status");
                            switch (responseCode) {
                                case 1:
                                    JSONObject user_info = response.getJSONObject("job_info");
                                    String status = user_info.optString("status");
                                    String status_value = user_info.optString("status_value");
                                    String job_id = user_info.optString("job_id");
                                    onTechnicianJobCompletedListener.onJobCompletedSuccess(job_id, status, status_value, index);
                                    Utility.uiThreadAlert(context, response.getString("message"));
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
                    onTechnicianJobCompletedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            Log.e("NetworkController", "JSON Exception: " + e.getLocalizedMessage());
            onTechnicianJobCompletedListener.onHidePgDialog();
        }
    }
}
