package com.tradiuus.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.tradiuus.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkController {

    public static NetworkController sharedInstance = new NetworkController();
    public static Context mContext = null;
    public static OkHttpClient client;
    public final int DEFAULT_TIMEOUT = 30 * 1000;
    public static final String CONTENT_TYPE_HEADER = "content-type";
    public static final String JSON_TYPE = "application/json; charset=utf-8";
    public static final MediaType JSON_MEDIA = MediaType.parse(JSON_TYPE);
    public static final String API_KEY = "12345678";
    public static final String DEVICE_TYPE = "android";//"ios";

    public static class NetworkResponses {
        public static String UNREACHABLE_RESPONSE;
    }

    public interface NetworkKeys {
        String ERROR_DESCRIPTION_KEY = "error-description";
    }

    /* PHP SERVICE END POINT*/
    public interface NetworkRoutes {
        String CONFIG_DATA = "request=appConfig/getAllConfig";

        String CUSTOMER_SIGN_UP = "request=customer/addCustomer";
        String CUSTOMER_SIGN_IN = "request=user/userLogin";
        String CUSTOMER_LOGOUT = "request=user/logout";
        String CUSTOMER_FORGOT_PASSWORD = "request=user/forgotPassword";
        String CUSTOMER_TRADE_QUESTIONNAIRE = "request=customer/getTradeQuestionnaire";
        String CUSTOMER_NEAR_TECHNICIAN = "request=customer/findTechnicians";
        String CUSTOMER_PROMOTIONS = "request=customer/getPromotions";
        String CUSTOMER_SUGGESTION = "request=customer/suggestion";
        String CUSTOMER_CHANGE_LOCATION = "request=customer/changeLocation";
        String CUSTOMER_UPLOAD_IMAGE="request=customer/uploadCustomerImage";

        String UPDATE_PASSWORD = "request=user/updatePassword";
        String CHECK_EMAIL = "request=user/isEmailExists";
        String CONTRACTOR_SIGN_UP = "request=contractor/addContractor";
        String CONTRACTOR_UPLOAD_IMAGE = "request=contractor/uploadContractorImage";
        String CONTRACTOR_UPLOAD_PROFILE = "request=contractor/updateProfile";
        String CONTRACTOR_UPLOAD_TECHNICIAN = "request=technician/updateProfile";
        String CONTRACTOR_UPDATE_DATA = "request=contractor/updateContractor";
        String CONTRACTOR_JOB_HISTORY = "request=job/jobHistory";

        String TECHNICIAN_ASSIGN_JOB = "request=job/assignJob";
        String TECHNICIAN_CANCEL_JOB = "request=job/cancelJob";
        String CONTRACTOR_GEO_JOBS = "request=job/getGeoJobs";
        String TECHNICIAN_CHANGE_WORK = "request=user/changeWorkAvailability";
        String TECHNICIAN_ADD_GEOJOB = "request=job/addGeoJob";
        String TECHNICIAN_UPDATE_LOCATION = "request=technician/updateLocation";
        String TECHNICIAN_COMPLETE_JOB ="request=job/completeJob";
    }

    public static NetworkController getInstance() {
        return sharedInstance;
    }

    public static void setContext(Context ctx) {
        mContext = ctx;
    }

    private NetworkController() {
        client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    public static Boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean checkForNetwork(NetworkResponseHandler handler) {
        if (!isNetworkAvailable(mContext)) {
            handler.withErrorDescription(NetworkResponses.UNREACHABLE_RESPONSE);
            return false;
        }
        return true;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("URL IS",""+mContext.getString(R.string.DEV_BASE_URL) + relativeUrl);
        return mContext.getString(R.string.DEV_BASE_URL) + relativeUrl;
    }

    private static void makeCall(Request request, final NetworkResponseHandler handler) {

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    JSONObject respJson = new JSONObject();
                    respJson.put(NetworkKeys.ERROR_DESCRIPTION_KEY, e.getLocalizedMessage());
                    handler.onFailure(respJson);
                } catch (Exception ec) {
                    Log.e("NetworkController", "Could not parse JSON body: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject rjson = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {

                        handler.onSuccess(rjson);
                    } else {

                        handler.onFailure(rjson);
                    }
                } catch (Exception e) {
                    Log.e("NetworkController", "Could not parse JSON body: " + e.getMessage());
//                    try {
//                        JSONObject object = new JSONObject();
//                        object.put("responseCode", "301");
//                        object.put("responseMessage", "Could not parse JSON body");
//                        handler.onFailure(object);
//                    } catch (JSONException e1) {
//                        e1.printStackTrace();
//                    }
                }
            }
        });
    }

    private static void get(String url, Map<String, String> params, NetworkResponseHandler responseHandler) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(getAbsoluteUrl(url)).newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.get(key));
        }
        String finalUrl = urlBuilder.build().toString();
        Request.Builder requestBuilder = new Request.Builder().url(finalUrl);
        makeCall(requestBuilder.build(), responseHandler);
    }

    public static void postBase(String url, RequestBody requestBody, NetworkResponseHandler handler) {
        Request.Builder builder = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .post(requestBody)
                .addHeader(CONTENT_TYPE_HEADER, JSON_TYPE);
        makeCall(builder.build(), handler);
    }

    private static void post(String url, JSONObject jsonBody, NetworkResponseHandler responseHandler) {
        RequestBody requestBody = RequestBody.create(JSON_MEDIA, jsonBody.toString());
        postBase(url, requestBody, responseHandler);
    }
}
