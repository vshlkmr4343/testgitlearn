package com.tradiuus.widgets;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;


public class CustomImageViewerDialog {
    private Context context;
    private Dialog mDialog;
    private DisplayImageOptions options;

    public CustomImageViewerDialog(Context context) {
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.technician_avt)
                .showImageOnFail(R.drawable.technician_avt)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    public void prepareAndShowDialog(String url) {
        try {
            mDialog = new ProgressDialog(context);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCancelable(true);
            mDialog.show();
            mDialog.setContentView(R.layout.inflate_imageviewer_dialog);
            try {
                ImageView img = (ImageView) mDialog.findViewById(R.id.image);
                ImageLoader.getInstance().displayImage(url, img, options);
                ((ImageView) mDialog.findViewById(R.id.action_cross)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            Window window = mDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
