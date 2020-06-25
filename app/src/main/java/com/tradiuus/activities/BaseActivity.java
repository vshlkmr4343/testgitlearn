package com.tradiuus.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tradiuus.R;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Image;
import com.tradiuus.models.Reason;
import com.tradiuus.models.Service;
import com.tradiuus.models.Trade;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Customer;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    protected ConfigData mConfigData;
    protected CustomProgressDialog pgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = BaseActivity.this;
        this.mConfigData = new ConfigData();
        setActivityLayout();
        initUIComponent();
        initUIListener();
        initLoadCall();
    }

    @Override
    public void onClick(View view) {
        viewClick(view);
    }

    protected void setActivityLayout() {

    }

    protected void initUIComponent() {

    }

    protected void initUIListener() {

    }

    protected void initLoadCall() {

    }

    protected void viewClick(View view) {

    }

    protected void loadPagerData(ArrayList<Image> images, String image_url) {
    }

    protected void loadConfigData() {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }

        pgDialog = new CustomProgressDialog(context);
        pgDialog.prepareAndShowDialog();

        if (mConfigData != null) {
            pgDialog.dismissDialog();
            loadPagerData(new ArrayList<Image>(mConfigData.getImages()), mConfigData.getImage_url());
            return;
        }

        mConfigData = UserPreference.getConfigData(context);
        if (mConfigData != null) {
            pgDialog.dismissDialog();
            loadPagerData(new ArrayList<Image>(mConfigData.getImages()), mConfigData.getImage_url());
            return;
        }

        try {
            RequestBody formBody = new FormBody.Builder().add("api_key", NetworkController.API_KEY).build();
            NetworkController.postBase(NetworkController.NetworkRoutes.CONFIG_DATA, formBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {

                    onSuccessEvent(response);
                }

                @Override
                public void onFailure(JSONObject error) {
                    runOnUiThread(new Runnable() {
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
            if (!TextUtils.isEmpty(response.toString())) {
                int responseCode = response.getInt("status");
                switch (responseCode) {
                    case 1:
                        try {
                            JSONObject appAllConfig = response.getJSONObject("appAllConfig");
                            //mConfigData = new ConfigData();
                            Gson gson = new Gson();
                            mConfigData.setImage_url(appAllConfig.getString("image_url"));
                            Type imageType = new TypeToken<List<Image>>() {
                            }.getType();
                            List<Image> listImage = gson.fromJson(appAllConfig.getJSONArray("images").toString(), imageType);
                            mConfigData.setImages(listImage);

                            Type tradeType = new TypeToken<List<Trade>>() {
                            }.getType();
                            List<Trade> trades = gson.fromJson(appAllConfig.getJSONArray("trades").toString(), tradeType);
                            mConfigData.setTrades(trades);

                            JSONObject jsonObject = appAllConfig.getJSONObject("questions");
                            JSONObject customerObject = jsonObject.getJSONArray("customer").getJSONObject(0);
                            JSONObject contractorObject = jsonObject.getJSONArray("contractor").getJSONObject(0);

                            mConfigData.setCustomer(gson.fromJson(customerObject.toString(), Customer.class));
                            mConfigData.setContractor(gson.fromJson(contractorObject.toString(), Contractor.class));

                            mConfigData.setAbout_us(appAllConfig.getString("about_us"));
                            mConfigData.setPrivacy_policy(appAllConfig.getString("privacy_policy"));
                            mConfigData.setTerms_of_use(appAllConfig.getString("terms_of_use"));

                            try {
                                Type service_type = new TypeToken<List<Service>>() {
                                }.getType();
                                List<Service> service_types = gson.fromJson(appAllConfig.getJSONArray("service_types").toString(), service_type);
                                mConfigData.setService_types(service_types);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Type reasons = new TypeToken<List<Reason>>() {
                                }.getType();
                                List<Reason> reasonList = gson.fromJson(appAllConfig.getJSONArray("job_cancellation_reasons").toString(), reasons);
                                mConfigData.setReasonList(reasonList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            UserPreference.saveConfigData(context, mConfigData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        loadPagerData(new ArrayList<Image>(mConfigData.getImages()), mConfigData.getImage_url());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Utility.uiThreadMsg(context, response.getString("responseMessage"));
                        break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgDialog.dismissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logoutDialog(final OnLogoutListener onLogoutListener) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Logout");
        builder1.setMessage("Do you want to logout?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (onLogoutListener != null) {
                            onLogoutListener.onProceed();
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (onLogoutListener != null) {
                            onLogoutListener.onCancel();
                        }
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    interface OnLogoutListener {
        void onProceed();

        void onCancel();
    }


    public void openHelpDialog(final Context context, final String faq, final String videos) {
        try {
            final Dialog helpDialog = new Dialog(context);
            helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            helpDialog.setCancelable(true);
            helpDialog.setContentView(R.layout.inflate_dialog_help);

            ((CustomTextView) helpDialog.findViewById(R.id.btn_tutorial)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((Activity) context).startActivity(new Intent(context, TutorialActivity.class));
                        ((Activity) context).overridePendingTransition(0, 0);
                        helpDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ((CustomTextView) helpDialog.findViewById(R.id.btn_faq)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(context, ActivityWebView.class);
                        intent.putExtra("URL", faq);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(0, 0);
                        helpDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ((CustomTextView) helpDialog.findViewById(R.id.btn_videos)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(context, ActivityWebView.class);
                        intent.putExtra("URL", videos);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(0, 0);
                        helpDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ((CustomTextView) helpDialog.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        helpDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            helpDialog.show();
            Window window = helpDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
