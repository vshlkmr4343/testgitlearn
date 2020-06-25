package com.tradiuus.widgets;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradiuus.R;


public class CustomProgressDialog {
    private Context context;
    private ProgressDialog pgDialog;

    public CustomProgressDialog(Context context) {
        this.context = context;
    }

    public void prepareAndShowDialog() {
        try {
            pgDialog = new ProgressDialog(context);
            pgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pgDialog.setCancelable(false);
            pgDialog.show();
            pgDialog.setContentView(R.layout.inflate_pgdialog);
            Window window = pgDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareAndShowDialog(String message) {
        try {
            pgDialog = new ProgressDialog(context);
            pgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pgDialog.setCancelable(false);
            pgDialog.show();
            pgDialog.setContentView(R.layout.inflate_pgdialog);
            Window window = pgDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            try {
                ((CustomTextView) pgDialog.findViewById(R.id.message)).setText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMessage(final String message) {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((CustomTextView) pgDialog.findViewById(R.id.message)).setText(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissDialog() {
        if (pgDialog != null) {
            pgDialog.dismiss();
            pgDialog = null;
        }
    }
}
