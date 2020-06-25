package com.tradiuus.network.postparams;

import java.util.ArrayList;

public class UpdatePostParam {

    private String trade_id;
    private ArrayList<ServiceParam> emergency_services = new ArrayList<ServiceParam>();
    private ArrayList<ServiceParam> estimate_services = new ArrayList<ServiceParam>();

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public ArrayList<ServiceParam> getEmergency_services() {
        return emergency_services;
    }

    public void setEmergency_services(ArrayList<ServiceParam> emergency_services) {
        this.emergency_services = emergency_services;
    }

    public ArrayList<ServiceParam> getEstimate_services() {
        return estimate_services;
    }

    public void setEstimate_services(ArrayList<ServiceParam> estimate_services) {
        this.estimate_services = estimate_services;
    }
}
