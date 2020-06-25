package com.tradiuus.models;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.tradiuus.helper.NetworkHandleListener;
import com.tradiuus.helper.Utility;
import com.tradiuus.helper.compressor.SiliCompressor;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageImpl {
    public Context context;
    private OnImageUploadChangedListener onImageUploadChangedListener;
    private OnProblemImageUploadListener onProblemImageUploadListener;

    public void init(Context context, OnImageUploadChangedListener onImageUploadChangedListener) {
        this.context = context;
        this.onImageUploadChangedListener = onImageUploadChangedListener;
    }

    public void init(Context context, OnProblemImageUploadListener onProblemImageUploadListener) {
        this.context = context;
        this.onProblemImageUploadListener = onProblemImageUploadListener;
    }

    public interface OnImageUploadChangedListener extends NetworkHandleListener {
        void onImageUploadSuccess(Image image, String message);

        void onUserPicUploadSuccess(String imageUrl, String message);
    }

    public interface OnProblemImageUploadListener extends NetworkHandleListener {
        void onImageUploadSuccess(ArrayList<String> images, String message, boolean directCall);
    }

    public void uploadContractorImages(final String insurance_img, final String license_img, final String company_logo, final String userId, String contractorId) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onImageUploadChangedListener.onShowPgDialog();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("api_key", NetworkController.API_KEY);
            if (!TextUtils.isEmpty(userId)) {
                builder.addFormDataPart("user_id", userId);
            }
            if (!TextUtils.isEmpty(userId)) {
                builder.addFormDataPart("contractor_id", contractorId);
            }
            if (!TextUtils.isEmpty(insurance_img)) {
                File insuranceFile = new File(insurance_img);
                builder.addFormDataPart("generic_insurance_img", insuranceFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), insuranceFile));
            }
            if (!TextUtils.isEmpty(license_img)) {
                File licenseFile = new File(license_img);
                builder.addFormDataPart("trade_license_img", licenseFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), licenseFile));
            }
            if (!TextUtils.isEmpty(company_logo)) {
                File companyFile = new File(company_logo);
                builder.addFormDataPart("company_logo", companyFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), companyFile));
            }
            RequestBody formBody = builder.build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPLOAD_IMAGE, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        new File(insurance_img).delete();
                        new File(license_img).delete();
                        new File(company_logo).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onUploadSuccess(response, TextUtils.isEmpty(userId));
                }

                @Override
                public void onFailure(JSONObject error) {
                    onImageUploadChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onUploadSuccess(final JSONObject response, boolean freshUpload) {
        try {
            onImageUploadChangedListener.onHidePgDialog();
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject imageDetail = response.getJSONObject("image_info");
                            Image image = new Image();
                            if (imageDetail.has("generic_insurance_img")) {
                                if (freshUpload) {
                                    image.setGeneric_insurance_img(imageDetail.optString("generic_insurance_img"));
                                } else if (imageDetail.optString("generic_insurance_img").contains(imageDetail.optString("image_url"))) {
                                    image.setGeneric_insurance_img(imageDetail.optString("generic_insurance_img"));
                                } else {
                                    image.setGeneric_insurance_img(imageDetail.optString("image_url") + imageDetail.optString("generic_insurance_img"));
                                }
                            }
                            if (imageDetail.has("trade_license_img")) {
                                if (freshUpload) {
                                    image.setTrade_license_img(imageDetail.optString("trade_license_img"));
                                } else if (imageDetail.optString("trade_license_img").contains(imageDetail.optString("image_url"))) {
                                    image.setTrade_license_img(imageDetail.optString("trade_license_img"));
                                } else {
                                    image.setTrade_license_img(imageDetail.optString("image_url") + imageDetail.optString("trade_license_img"));
                                }
                            }
                            if (imageDetail.has("company_logo")) {
                                if (freshUpload) {
                                    image.setCompany_logo(imageDetail.optString("company_logo"));
                                } else if (imageDetail.optString("company_logo").contains(imageDetail.optString("image_url"))) {
                                    image.setCompany_logo(imageDetail.optString("company_logo"));
                                } else {
                                    image.setCompany_logo(imageDetail.optString("image_url") + imageDetail.optString("company_logo"));
                                }
                            }
                            if (imageDetail.has("image_url")) {
                                image.setImage_url(imageDetail.optString("image_url"));
                            }
                            onImageUploadChangedListener.onImageUploadSuccess(image, response.getString("message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        onImageUploadChangedListener.showAlert(response.getString("message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadUserPic(String uerId, final String sourceFile) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            File file = new File(sourceFile);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("technician_id", uerId);
            builder.addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
            builder.addFormDataPart("api_key", NetworkController.API_KEY);
            final RequestBody formBody = builder.build();
            onImageUploadChangedListener.onShowPgDialog();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPLOAD_TECHNICIAN, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        new File(sourceFile).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onImageUploadChangedListener.onHidePgDialog();
                    if (!TextUtils.isEmpty(response.toString())) {
                        int responseCode = response.optInt("status");
                        switch (responseCode) {
                            case 1:
                                try {
                                    JSONObject imageDetail = response.getJSONObject("image_info");
                                    onImageUploadChangedListener.onUserPicUploadSuccess(imageDetail.optString("photo"), response.optString("message"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utility.uiThreadMsg(context, response.toString());
                                }
                                break;
                            default:
                                onImageUploadChangedListener.showAlert(response.optString("message"));
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onImageUploadChangedListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compressAndUploadTechnicianImage(final String uerId, final String sourceFile) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            public String filePath = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                onImageUploadChangedListener.onShowPgDialog();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                filePath = SiliCompressor.with(context).compress(sourceFile, Utility.getOutputMediaFile());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onImageUploadChangedListener.onHidePgDialog();
                uploadUserPic(uerId, filePath);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void compressAndUploadContractorImages(final String insurance_img, final String license_img, final String cyompan_logo, final String userId, final String contractorId) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            String filePathInsurance = null;
            String filePathLicense = null;
            String filePathCyompan = null;
            File mediaStorageDir = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                onImageUploadChangedListener.onShowPgDialog();
                mediaStorageDir = Utility.getOutputMediaFile();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (!TextUtils.isEmpty(insurance_img))
                        filePathInsurance = SiliCompressor.with(context).compress(insurance_img, mediaStorageDir);
                    if (!TextUtils.isEmpty(license_img))
                        filePathLicense = SiliCompressor.with(context).compress(license_img, mediaStorageDir);
                    if (!TextUtils.isEmpty(cyompan_logo))
                        filePathCyompan = SiliCompressor.with(context).compress(cyompan_logo, mediaStorageDir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onImageUploadChangedListener.onHidePgDialog();
                try {
                    uploadContractorImages(filePathInsurance, filePathLicense, filePathCyompan, userId, contractorId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void compressProblemImage(final String uerId, final ArrayList<String> sourceFile, final boolean directCall) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            public ArrayList<String> filePath = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                onProblemImageUploadListener.onShowPgDialog();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < sourceFile.size(); i++) {
                    filePath.add(SiliCompressor.with(context).compress(sourceFile.get(i), Utility.getOutputMediaFile()));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onProblemImageUploadListener.onHidePgDialog();
                uploadProblemPic(uerId, filePath, directCall);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void uploadProblemPic(String uerId, final ArrayList<String> sourceFile, final boolean directCall) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (int i = 0; i < sourceFile.size(); i++) {
                File file = new File(sourceFile.get(i));
                String fileName = "problem_imgg" + i + ".jpeg";
                builder.addFormDataPart("image[" + i + "]", fileName, RequestBody.create(MediaType.parse("image/jpeg"), file));
            }
            builder.addFormDataPart("api_key", NetworkController.API_KEY);
            final RequestBody formBody = builder.build();
            onProblemImageUploadListener.onShowPgDialog();
            NetworkController.postBase(NetworkController.NetworkRoutes.CUSTOMER_UPLOAD_IMAGE, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        for (int i = 0; i < sourceFile.size(); i++) {
                            new File(sourceFile.get(i)).delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onProblemImageUploadListener.onHidePgDialog();
                    if (!TextUtils.isEmpty(response.toString())) {
                        int responseCode = response.optInt("status");
                        switch (responseCode) {
                            case 1:
                                try {
                                    JSONObject imageDetail = response.getJSONObject("image_info");
                                    JSONArray array = imageDetail.getJSONArray("image");
                                    String baseUrl = imageDetail.getString("image_url");
                                    ArrayList<String> list = new ArrayList<>();
                                    for (int i = 0; i < array.length(); i++) {
                                        list.add(array.getString(i));
                                    }
                                    onProblemImageUploadListener.onImageUploadSuccess(list, response.optString("message"), directCall);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utility.uiThreadMsg(context, response.toString());
                                }
                                break;
                            default:
                                onProblemImageUploadListener.showAlert(response.optString("message"));
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onProblemImageUploadListener.onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
