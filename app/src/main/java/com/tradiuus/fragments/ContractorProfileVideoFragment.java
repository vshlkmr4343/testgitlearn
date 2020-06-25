package com.tradiuus.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.ContractorProfileUpdateActivity;
import com.tradiuus.activities.ContractorVideoRecordActivity;
import com.tradiuus.models.Contractor;

public class ContractorProfileVideoFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = ContractorProfileVideoFragment.class.getSimpleName();
    private Context context;
    public Contractor mContractor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_section, null);
        this.context = getActivity();
        this.mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
        initView(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_btn_record:
                Intent mIntent = new Intent(getActivity(), ContractorVideoRecordActivity.class);
                ((ContractorProfileUpdateActivity) getActivity()).startActivityForResult(mIntent, 2009);
                ((ContractorProfileUpdateActivity) getActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.setting_btn_play:
                try {
                    String url = mContractor.getVideo_url();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 2009) {
            mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
        }
    }

    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.setting_btn_record)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.setting_btn_play)).setOnClickListener(this);
    }
}
