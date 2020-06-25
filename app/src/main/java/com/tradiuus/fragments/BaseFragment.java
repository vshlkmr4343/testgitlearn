package com.tradiuus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradiuus.R;


public class BaseFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = setActivityLayout(inflater);
        initUIComponent(rootView);
        initUIListener(rootView);
        initLoadCall(rootView);
        return rootView;
    }


    @Override
    public void onClick(View view) {
        viewClick(view);
    }

    protected View setActivityLayout(LayoutInflater inflater) {

        return null;
    }

    protected void initUIComponent(View rootView) {

    }

    protected void initUIListener(View rootView) {

    }

    protected void initLoadCall(View rootView) {

    }

    protected void viewClick(View view) {

    }

}
