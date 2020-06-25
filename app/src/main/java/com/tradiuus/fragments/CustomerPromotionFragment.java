package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.activities.CustomerDashboardActivity;
import com.tradiuus.models.CustomerImpl;
import com.tradiuus.models.Promotion;

import java.util.ArrayList;
import java.util.List;

public class CustomerPromotionFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private DisplayImageOptions options;
    private ProgressBar progressBar;
    private ListView promotions;
    private PromotionAdapter mPromotionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_promotion, null);
        this.context = getActivity();
        initView(rootView);
        initViewLoad();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void initView(View rootView) {
        promotions = (ListView) rootView.findViewById(R.id.promotions);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pg_dialog);

        mPromotionAdapter = new PromotionAdapter(context, new ArrayList<Promotion>());
        promotions.setAdapter(mPromotionAdapter);
    }

    public void initViewLoad() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pager_loading_bg)
                .showImageOnFail(R.drawable.pager_loading_bg)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();


        ((CustomerDashboardActivity) getActivity()).customerImpl.getPromotions(new CustomerImpl.OnPromotionalDataListener() {
            @Override
            public void getPromotions(final List<Promotion> promotions) {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (promotions != null && promotions.size() > 0) {
                                    mPromotionAdapter.refresh((ArrayList<Promotion>) promotions);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onShowPgDialog() {
                showPG();
            }

            @Override
            public void onHidePgDialog() {
                hidePG();
            }

            @Override
            public void showAlert(String message) {
            }
        });
    }

    private void showPG() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hidePG() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private class PromotionAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<Promotion> promotions = new ArrayList<>();
        private DisplayImageOptions options;

        public PromotionAdapter(Context context, ArrayList<Promotion> promotions) {
            this.promotions = promotions;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.technician_avt)
                    .showImageOnFail(R.drawable.technician_avt)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .build();
        }

        public void refresh(ArrayList<Promotion> jobs) {
            if (jobs != null && jobs.size() > 0) {
                this.promotions = new ArrayList<>();
                this.promotions.addAll(jobs);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return (promotions != null && promotions.size() > 0) ? promotions.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.inflate_promotion_row, null);
                holder.service_logo = (ImageView) convertView.findViewById(R.id.promotionImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Promotion mPromotion = promotions.get(position);

            try {
                ImageLoader.getInstance().displayImage(mPromotion.getPromotion_image(), holder.service_logo, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            public ImageView service_logo;
        }
    }
}
