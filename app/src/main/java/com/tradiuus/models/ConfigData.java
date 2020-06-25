package com.tradiuus.models;

import android.util.Log;

import java.util.List;

public class ConfigData {
    private String image_url;
    private List<Image> images;
    private List<Service> service_types;
    private Customer customer;
    private Contractor contractor;
    private List<Trade> trades;
    private String about_us;
    private String privacy_policy;
    private String terms_of_use;
    private List<Reason> reasonList;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Service> getService_types() {
        return service_types;
    }

    public void setService_types(List<Service> service_types) {
        this.service_types = service_types;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public List<Trade> getTrades() {
        Log.d("GET_TRADES",""+this.trades);
        return this.trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
        Log.d("Set_Trades:","Trades"+this.trades);
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public String getPrivacy_policy() {
        return privacy_policy;
    }

    public void setPrivacy_policy(String privacy_policy) {
        this.privacy_policy = privacy_policy;
    }

    public String getTerms_of_use() {
        return terms_of_use;
    }

    public void setTerms_of_use(String terms_of_use) {
        this.terms_of_use = terms_of_use;
    }

    public List<Reason> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<Reason> reasonList) {
        this.reasonList = reasonList;
    }
}

