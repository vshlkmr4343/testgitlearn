package com.tradiuus.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tradiuus.R;
import com.tradiuus.models.Job;
import com.tradiuus.widgets.camera.CameraVideoFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void uiThreadMsg(final Context context, final String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utility.showToast(context, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uiThreadAlert(final Context context, final String title, final String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle(title);
                    builder1.setMessage(msg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uiThreadAlert(final Context context, final String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle(context.getString(R.string.app_name));
                    builder1.setMessage(msg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uiThreadAlert(final Context context, final String msg, final OnDialogButtonClick onDialogButtonClick) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle("Alert");
                    builder1.setMessage(msg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (onDialogButtonClick != null) {
                                        onDialogButtonClick.onOkayButtonClick();
                                    }
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uiThreadAlert(final Context context, final String title, final String msg, final String pButton, final OnDialogButtonClick onDialogButtonClick) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle(title);
                    builder1.setMessage(msg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            pButton,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (onDialogButtonClick != null) {
                                        onDialogButtonClick.onOkayButtonClick();
                                    }
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uiThreadAlert(final Context context, final String title, final String msg, final String pButton, final String nButton, final OnDialogMultiButtonClick onDialogMultiButtonClick) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle(title);
                    builder1.setMessage(msg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            pButton,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (onDialogMultiButtonClick != null) {
                                        onDialogMultiButtonClick.onOkayButtonClick();
                                    }
                                }
                            });

                    builder1.setNegativeButton(
                            nButton,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (onDialogMultiButtonClick != null) {
                                        onDialogMultiButtonClick.onCancelButtonClick();
                                    }
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uiThreadAlert(final Context context, final String msg, final OnDialogMultiButtonClick onDialogMultiButtonClick) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle("Alert");
                    builder1.setMessage(msg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (onDialogMultiButtonClick != null) {
                                        onDialogMultiButtonClick.onOkayButtonClick();
                                    }
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (onDialogMultiButtonClick != null) {
                                        onDialogMultiButtonClick.onCancelButtonClick();
                                    }
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String readJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean isValidPassword(final String password) {
        /*
        ^                 # start-of-string
        (?=.*[0-9])       # a digit must occur at least once
        (?=.*[a-z])       # a lower case letter must occur at least once
        (?=.*[A-Z])       # an upper case letter must occur at least once
        (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
        (?=\\S+$)          # no whitespace allowed in the entire string
        .{4,}             # anything, at least six places though
        $                 # end-of-string
         */
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static interface OnDialogButtonClick {
        void onOkayButtonClick();
    }

    public static interface OnDialogMultiButtonClick {
        void onOkayButtonClick();

        void onCancelButtonClick();
    }

    public static String getDateTime(String timeStamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");
            calendar.setTimeInMillis(Long.valueOf(timeStamp) * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dMMMyy, hh:mm a");
            Date currentTimeZone = (Date) calendar.getTime();
            return sdf.format(currentTimeZone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getDayInCount(String timeStamp1, String timeStamp2) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
            Date currentTimeZone = (Date) calendar.getTime();

            calendar.setTimeInMillis(Long.valueOf(timeStamp1) * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            String date1 = sdf.format(currentTimeZone);

            calendar.setTimeInMillis(Long.valueOf(timeStamp2) * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            String date2 = sdf.format(currentTimeZone);

            Date d1 = new Date(date2);
            Date d2 = new Date(date2);
            long diff = d1.getTime() - d2.getTime();
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            int hours = (int) (diff / (1000 * 60 * 60));
            int minutes = (int) (diff / (1000 * 60));
            int seconds = (int) (diff / (1000));

            return 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getStatus(String status) {
        switch (status) {
            case "1":
                return "In Transit";
            case "2":
                return "Cancelled";
            case "3":
                return "Direct Call";
            case "4":
                return "Close";
            case "5":
                return "Completed";
        }
        return "In Transit";
    }

    public static String getStatusMd2(String status, String time) {
        switch (status) {
            case "1":
//                return "In Transit " + getDateTime(time);
                return "Not complete yet";
            case "2":
                return "Cancelled on " + getDateTime(time);
            case "3":
                return "N/A";
            case "4":
                //closed to completed
                return "Completed on " + getDateTime(time);
            case "5":
                return "Completed on " + getDateTime(time);
        }
        return "In Transit";
    }

    public static String getStatusCard(String status) {
        switch (status) {
            case "1":
                return "MARK AS COMPLETE";
            case "2":
                return "CANCEL";
            case "3":
                return "DIRECT CALL";
            case "4":
//                return "COMPLETED";
                return "CLOSE";
            case "5":
                return "CLOSE";
        }
        return "IN TRANSIT";
    }

    public static String getStatusColor(String status) {
        switch (status) {
            case "1":
                return "#F45244";
            case "2":
                return "#F45244";
            case "3":
                return "#BABABA";
            case "4":
//                return "#77BE44";
                return "#F45244";
            case "5":
                return "#F45244";
        }
        return "#F45244";
    }

    public static int getStatusIcon(String status) {
        switch (status) {
            case "1":
                return R.drawable.reviewinactive_2;
            case "2":
                return R.drawable.cross_cion;
            case "3":
                return R.drawable.directcall;
            case "4":
                return R.drawable.reviewinactive_2;
            case "5":
                return R.drawable.reviewactive_1;
        }
        return R.drawable.reviewactive_1;
    }

    public static int getRatingIcon(String rating) {
        try {
            if (Integer.valueOf(rating) > 0) {
                return R.drawable.ratingactive;
            } else {
                return R.drawable.ratinginactive;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return R.drawable.reviewinactive_2;
    }

    public static void showRatingDialog(Context context, Job jobDetail) {
        try {
            if (!jobDetail.getJob_status().equalsIgnoreCase("5")) {
                return;
            }
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.inflate_rating_dialog);

            LinearLayout review_rating = (LinearLayout) dialog.findViewById(R.id.review_rating);
            TextView review_comment = (TextView) dialog.findViewById(R.id.review_comment);
            TextView review_by = (TextView) dialog.findViewById(R.id.review_by);
            ImageView review_cross = (ImageView) dialog.findViewById(R.id.review_cross);

            review_cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            try {
                review_comment.setText(jobDetail.getJob_review().getReview());
                review_by.setText(jobDetail.getCustomer_details().getFirst_name() + " " + jobDetail.getCustomer_details().getLast_name());

                int rating = Integer.valueOf(jobDetail.getJob_rating());
                for (int i = 0; i < 5; i++) {
                    ImageView ratingV = new ImageView(context);
                    ratingV.setLayoutParams(new LinearLayout.LayoutParams(35, 35));
                    if (rating > 0 && i < rating) {
                        ratingV.setImageDrawable(context.getResources().getDrawable(R.drawable.ratingstaryellow_2));
                    } else {
                        ratingV.setImageDrawable(context.getResources().getDrawable(R.drawable.ratingstargray));
                    }
                    review_rating.addView(ratingV);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertTimeToHr(int time) {
        String showTime = time + "";
        try {
            if (time == 30) {
                showTime = time + " mins";
            } else {
                int hr = (time / 60);
                int min = (time % 60);
                showTime = ((hr > 0) ? hr + " hr" : "") + " " + ((min > 0) ? min + " min" : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showTime;
    }

    public static String convertTimeToHr_(int time) {
        String showTime = time + "";
        try {
            if (time == 30) {
                showTime = time + " mins";
            } else {
                int hr = (time / 60);
                int min = (time % 60);
                if (hr > 0) {
                    showTime = ((hr > 0) ? hr + " hr" : "") + " " + ((min > 0) ? min + " min" : "");
                } else {
                    showTime = String.valueOf(min);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showTime;
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static File getOutputMediaFile() {
        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), CameraVideoFragment.VIDEO_DIRECTORY_NAME);
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("GET_OUTPUT_MEDIA_FILE", "Oops! Failed create directory");
                return null;
            }
        }
        return new File(mediaStorageDir.getPath());
    }

    public static File create(File destDir){
        File cacheFile = null;
        try {
            cacheFile = new File(destDir,
                    "VIDEO_" + System.currentTimeMillis() + ".mp4"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheFile;
    }
}
