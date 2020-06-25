package com.tradiuus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.helper.compressor.SiliCompressor;
import com.tradiuus.models.Contractor;
import com.tradiuus.network.CountingRequestBody;
import com.tradiuus.network.NetworkController;
import com.tradiuus.network.NetworkResponseHandler;
import com.tradiuus.widgets.CustomProgressDialog;
import com.tradiuus.widgets.CustomTextView;
import com.tradiuus.widgets.camera.CameraFragment;

import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ContractorVideoRecordActivity extends BaseActivity {

    private Context context;
    public Contractor mContractor;
    private CustomTextView contractor_dashboard_back;
    private CustomTextView contractor_dashboard_logut;
    private CustomTextView videoTiming;
    private CustomTextView playPauseButton;
    private CustomTextView playVideo;
    private CustomTextView uploadButton;
    private ImageView switchCamera;
    private boolean buttonState = false;
    private long maxTime = 1 * 15 * 60 * 1000;
    private long interval = 1 * 1000;
    public boolean lockUI = false;

    @Override
    protected void setActivityLayout() {
        setContentView(R.layout.activity_contractor_videorecord);
        this.context = ContractorVideoRecordActivity.this;
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initUIComponent() {
        contractor_dashboard_back = (CustomTextView) findViewById(R.id.contractor_dashboard_back);
        contractor_dashboard_logut = (CustomTextView) findViewById(R.id.contractor_dashboard_logut);
        contractor_dashboard_back.setVisibility(View.VISIBLE);
        contractor_dashboard_logut.setVisibility(View.INVISIBLE);

        videoTiming = (CustomTextView) findViewById(R.id.timing);
        playPauseButton = (CustomTextView) findViewById(R.id.startButton);
        playPauseButton.setOnClickListener(this);

        playVideo = (CustomTextView) findViewById(R.id.playVideo);
        uploadButton = (CustomTextView) findViewById(R.id.uploadButton);
        switchCamera = (ImageView) findViewById(R.id.switchCamera);
        contractor_dashboard_back.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        switchCamera.setOnClickListener(this);

        switchCamera.setColorFilter(Color.parseColor("#25B7D3"), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void initUIListener() {
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.contractor_dashboard_back:
                onBackPressed();
                break;

            case R.id.uploadButton:
                /*uploadVideo(((CameraFragment) getSupportFragmentManager().getFragments().get(0)).getCurrentPath());*/
                compressAndUploadVideo(((CameraFragment) getSupportFragmentManager().getFragments().get(0)).getCurrentPath());
                break;
            case R.id.switchCamera:
                if (buttonState) {
                    try {
                        ((CameraFragment) getSupportFragmentManager().getFragments().get(0)).stopRecordingVideo();
                        ((CameraFragment) getSupportFragmentManager().getFragments().get(0)).changeCamera();
                        stopTimer();
                        buttonState = false;
                        changePlayPauseState();
                        videoTiming.setText("00:00");
                        videoTiming.setVisibility(View.VISIBLE);
                        playPauseButton.setVisibility(View.VISIBLE);
                        playVideo.setVisibility(View.INVISIBLE);
                        uploadButton.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ((CameraFragment) getSupportFragmentManager().getFragments().get(0)).changeCamera();
                }
                break;

            case R.id.startButton:
                try {
                    if (lockUI) {
                        return;
                    }
                    if (buttonState) {
                        buttonState = false;
                        stopTimer();
                        changePlayPauseState();
                        videoTiming.setVisibility(View.INVISIBLE);
                        playPauseButton.setVisibility(View.INVISIBLE);
                        playVideo.setVisibility(View.VISIBLE);
                        uploadButton.setVisibility(View.VISIBLE);
                    } else {
                        buttonState = true;
                        videoTiming.setText("00:00");
                        changePlayPauseState();
                        startTimer();
                    }
                    ((CameraFragment) getSupportFragmentManager().getFragments().get(0)).recordVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                    lockUI = false;
                    onHidePgDialog();
                }
                break;
        }
    }

    @Override
    protected void initLoadCall() {
        try {
            changePlayPauseState();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, CameraFragment.newInstance()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changePlayPauseState() {
        playPauseButton.setText(buttonState ? "StopRecording" : "Start Recording");
    }


    private Handler handler = new Handler();
    private long time = 0;

    private void startTimer() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, interval);
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
        time = 0;
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            time++;
            if (time > 60) {
                time = 0;
                stopTimer();
            } else {
                showTime(time);
                handler.postDelayed(runnable, interval);
            }
        }
    };

    private void showTime(final long totalSecs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    long hours = totalSecs / 3600;
                    long minutes = (totalSecs % 3600) / 60;
                    long seconds = totalSecs % 60;
                    /*videoTiming.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));*/
                    videoTiming.setText(String.format("%02d:%02d", minutes, seconds));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onShowPgDialog() {
        try {
            pgDialog = new CustomProgressDialog(context);
            pgDialog.prepareAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onShowCustomPgDialog(String msg) {
        try {
            pgDialog = new CustomProgressDialog(context);
            pgDialog.prepareAndShowDialog(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onHidePgDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pgDialog != null){
                        pgDialog.dismissDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void compressAndUploadVideo(final String sourceFile) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            public String filePath = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                onShowCustomPgDialog("Prepare video...");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    filePath = SiliCompressor.with(context).compressVideo(sourceFile, Utility.getOutputMediaFile().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onHidePgDialog();
                uploadVideo(filePath);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void uploadVideo(String path) {
        if (!NetworkController.isNetworkAvailable(context)) {
            return;
        }
        try {
            onShowCustomPgDialog("Uploading...");
            File file = new File(path);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("api_key", NetworkController.API_KEY);
            builder.addFormDataPart("update", "companyVideo");
            builder.addFormDataPart("user_id", mContractor.getUser_id());
            builder.addFormDataPart("contractor_id", mContractor.getContractor_id());
            if (!TextUtils.isEmpty(path)) {
                builder.addFormDataPart("video", file.getName(), RequestBody.create(MediaType.parse("video/mp4"), file));
            }
            RequestBody formBody = builder.build();
            // Decorate the request body to keep track of the upload progress
            CountingRequestBody countingBody = new CountingRequestBody(formBody,
                    new CountingRequestBody.Listener() {

                        @Override
                        public void onRequestProgress(long bytesWritten, long contentLength) {
                            try {
                                float percentage = 100f * bytesWritten / contentLength;
                                pgDialog.updateMessage("Uploading..." + (int) percentage + "%");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            NetworkController.postBase(NetworkController.NetworkRoutes.CONTRACTOR_UPDATE_DATA, countingBody, new NetworkResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        onHidePgDialog();
                        if (response.has("status") && response.optString("status").equalsIgnoreCase("1")) {
                            JSONObject videoObject = response.getJSONObject("video_info");
                            String url = videoObject.optString("video_url");
                            if (!TextUtils.isEmpty(url)) {
                                mContractor.setVideo_url(url);
                                UserPreference.saveContractor(context, mContractor);
                                setResult(Activity.RESULT_OK, new Intent());
                                overridePendingTransition(0, 0);
                                finish();
                            }
                        } else {
                            Utility.uiThreadAlert(context, response.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(JSONObject error) {
                    onHidePgDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}