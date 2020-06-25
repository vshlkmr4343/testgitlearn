package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.ContractorEmergencyRequestActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Job;
import com.tradiuus.models.Question;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Technician;
import com.tradiuus.network.postparams.EmergencyRequestImage;
import com.tradiuus.widgets.CustomImageViewerDialog;

import java.util.ArrayList;
import java.util.List;

public class ContractorEmergencyReqListDetailsFragment extends Fragment implements View.OnClickListener {
    public static ContractorEmergencyReqListDetailsFragment fragment;
    private Context context;
    public Contractor mContractor;

    public ImageView service_logo;
    public TextView service_contractor_name;
    public TextView service_name;
    public TextView service_job_id;
    public TextView service_posted_by;
    public TextView service_booked;
    public TextView service_work_status;
    public TextView service_mark_as_completed;
    public TextView service_ph_no;
    public ImageView status_icon;
    public LinearLayout status_card;
    public TextView service_man_address;
    public TextView service_job_detail;
    public LinearLayout service_item;

    private TextView service_cost;
    private ImageView service_rating_icon;
    private TextView service_rating;

    private ImageGrid adapter;
    private RecyclerView recyclerView;
    public Job jobDetail;
    private DisplayImageOptions options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractor_emergency_req_list_details, null);
        this.context = getActivity();
        jobDetail = ((TradiuusApp) context.getApplicationContext()).jobDetail;
        initView(rootView);
        initViewLoad();
        return rootView;
    }

    @Override
    public void onDestroy() {
        fragment = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.service_images);
        service_logo = (ImageView) rootView.findViewById(R.id.service_logo);
        service_contractor_name = (TextView) rootView.findViewById(R.id.service_contractor_name);
        service_name = (TextView) rootView.findViewById(R.id.service_name);
        service_job_id = (TextView) rootView.findViewById(R.id.service_job_id);
        service_posted_by = (TextView) rootView.findViewById(R.id.service_posted_by);
        service_booked = (TextView) rootView.findViewById(R.id.service_booked);
        service_work_status = (TextView) rootView.findViewById(R.id.service_work_status);
        service_mark_as_completed = (TextView) rootView.findViewById(R.id.service_mark_as_completed);
        service_ph_no = (TextView) rootView.findViewById(R.id.service_ph_no);

        status_icon = (ImageView) rootView.findViewById(R.id.status_icon);
        status_card = (LinearLayout) rootView.findViewById(R.id.status_card);

        service_man_address = (TextView) rootView.findViewById(R.id.service_man_address);
        service_job_detail = (TextView) rootView.findViewById(R.id.service_job_detail);
        service_item = (LinearLayout) rootView.findViewById(R.id.service_item);

        service_cost = (TextView) rootView.findViewById(R.id.service_cost);
        service_rating_icon = (ImageView) rootView.findViewById(R.id.service_rating_icon);
        service_rating = (TextView) rootView.findViewById(R.id.service_rating);

        status_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showRatingDialog(context, jobDetail);
            }
        });
    }

    private void initViewLoad() {
        this.options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.technician_avt)
                .showImageOnFail(R.drawable.technician_avt)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        mContractor = ((ContractorEmergencyRequestActivity) getActivity()).mContractor;

        adapter = new ImageGrid(context, new ArrayList<EmergencyRequestImage>());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        try {
            Technician technician = jobDetail.getTechnician().get(0);

            service_contractor_name.setText(technician.getTechnician_name());
            ImageLoader.getInstance().displayImage(technician.getTechnician_profile(), service_logo, options);
            service_name.setText(jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());
            service_job_id.setText("Job Id: " + jobDetail.getJob_name());
            service_posted_by.setText("Posted By: " + jobDetail.getCustomer_details().getFirst_name() + " " + jobDetail.getCustomer_details().getLast_name());
            service_ph_no.setText(jobDetail.getCustomer_details().getMobile_number());
            service_booked.setText(Utility.getDateTime(jobDetail.getCreated_on()));
            service_work_status.setText(Utility.getStatusMd2(jobDetail.getJob_status(), jobDetail.getUpdated_on()));

            status_card.setBackgroundColor(Color.parseColor(Utility.getStatusColor(jobDetail.getJob_status())));
            service_mark_as_completed.setText(Utility.getStatusCard(jobDetail.getJob_status()));
            status_icon.setImageDrawable(getResources().getDrawable(Utility.getStatusIcon(jobDetail.getJob_status())));

            service_man_address.setText(jobDetail.getCustomer_details().getFull_address());
            service_job_detail.setText(jobDetail.getJob_trade().getTrade_name() + " for " + jobDetail.getJob_trade().getService().getService_name());

            service_cost.setText(jobDetail.getJob_cost());
            service_rating_icon.setImageDrawable(getResources().getDrawable(Utility.getRatingIcon(jobDetail.getJob_rating())));
            service_rating.setText(jobDetail.getJob_rating());

            service_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        CustomImageViewerDialog imageViewerDialog = new CustomImageViewerDialog(context);
                        imageViewerDialog.prepareAndShowDialog(jobDetail.getTechnician().get(0).getTechnician_profile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadQS();
    }

    private void loadQS() {
        try {
            ArrayList<Question> question = jobDetail.getJob_trade().getService().getService_questions();
            for (int i = 0; i < question.size(); i++) {
                View v = LayoutInflater.from(context).inflate(R.layout.service_item_row, null);
                ((TextView) v.findViewById(R.id.title)).setText(question.get(i).getQuestion());
                ((TextView) v.findViewById(R.id.desc)).setText(question.get(i).getOption());
                service_item.addView(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ImageGrid extends RecyclerView.Adapter<ImageGrid.MyViewHolder> {

        private Context mContext;
        private List<EmergencyRequestImage> imageList;

        public ImageGrid(Context mContext, List<EmergencyRequestImage> categoryList) {
            this.mContext = mContext;
            this.imageList = categoryList;
        }

        @Override
        public int getItemCount() {
            return 2;// return imageList.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_grid_row, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
        }


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title;
            public ImageView thumbnail;

            public MyViewHolder(View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }

            @Override
            public void onClick(View v) {

            }
        }
    }

}
