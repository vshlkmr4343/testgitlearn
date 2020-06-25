package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.models.ConfigData;

public class CustomerAboutTradiuusFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private WebView mWebView;
    private ProgressBar progressBar;
    public ConfigData mConfigData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_about_tradiuus, null);
        this.context = getActivity();
        mConfigData = ((TradiuusApp) context.getApplicationContext()).mConfigData;
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
        progressBar = (ProgressBar) rootView.findViewById(R.id.pg_dialog);
        mWebView = (WebView) rootView.findViewById(R.id.webview);
    }

    public void initViewLoad() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new CustomWebViewClient(progressBar));
        mWebView.loadUrl(mConfigData.getAbout_us());
    }

    public class CustomWebViewClient extends WebViewClient {
        private ProgressBar progressBar;

        public CustomWebViewClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
