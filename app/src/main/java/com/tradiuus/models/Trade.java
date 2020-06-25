package com.tradiuus.models;

import com.tradiuus.network.postparams.ServiceParam;

import java.util.ArrayList;

public class Trade {
    private String trade_id;
    private String trade_name;
    private String trade_name_prefix;
    private String trade_workman;
    private String featured_normal_image;
    private String featured_selected_image;
    private String more_trade_section_image;
    private String on_map_image;
    private String trade_order;
    private ArrayList<Trade> services;
    public int isSelected = 0;
    private String map_image;
    private Service service;
    private ArrayList<ServiceParam> emergency_services = new ArrayList<ServiceParam>();
    private ArrayList<ServiceParam> estimate_services = new ArrayList<ServiceParam>();
    private String service_id;
    private String service_name;

    private String service_type;
    private String emergency_rating;
    private String estimate_rating;




    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getTrade_name() {
        return trade_name;
    }

    public void setTrade_name(String trade_name) {
        this.trade_name = trade_name;
    }

    public String getTrade_name_prefix() {
        return trade_name_prefix;
    }

    public void setTrade_name_prefix(String trade_name_prefix) {
        this.trade_name_prefix = trade_name_prefix;
    }

    public String getTrade_workman() {
        return trade_workman;
    }

    public void setTrade_workman(String trade_workman) {
        this.trade_workman = trade_workman;
    }

    public String getFeatured_normal_image() {
        return featured_normal_image;
    }

    public void setFeatured_normal_image(String featured_normal_image) {
        this.featured_normal_image = featured_normal_image;
    }

    public String getFeatured_selected_image() {
        return featured_selected_image;
    }

    public void setFeatured_selected_image(String featured_selected_image) {
        this.featured_selected_image = featured_selected_image;
    }

    public String getMore_trade_section_image() {
        return more_trade_section_image;
    }

    public void setMore_trade_section_image(String more_trade_section_image) {
        this.more_trade_section_image = more_trade_section_image;
    }

    public String getOn_map_image() {
        return on_map_image;
    }

    public void setOn_map_image(String on_map_image) {
        this.on_map_image = on_map_image;
    }

    public String getTrade_order() {
        return trade_order;
    }

    public void setTrade_order(String trade_order) {
        this.trade_order = trade_order;
    }

    public ArrayList<Trade> getServices() {
        return services;
    }

    public void setServices(ArrayList<Trade> services) {
        this.services = services;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public String getMap_image() {
        return map_image;
    }

    public void setMap_image(String map_image) {
        this.map_image = map_image;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getEmergencyServices() {
        String emergency = "";
        for (ServiceParam serviceParam : this.emergency_services) {
            emergency = emergency + serviceParam.getService_name() + ", ";
        }
        if (emergency.length() > 3) {
            emergency = emergency.substring(0, emergency.length() - 2);
        }
        return emergency;
    }

    public String getEstimateServices() {
        String emergency = "";
        for (ServiceParam serviceParam : this.estimate_services) {
            emergency = emergency + serviceParam.getService_name() + ", ";
        }
        if (emergency.length() > 3) {
            emergency = emergency.substring(0, emergency.length() - 2);
        }
        return emergency;
    }

    public String getEmergency_rating() {
        return emergency_rating;
    }

    public void setEmergency_rating(String emergency_rating) {
        this.emergency_rating = emergency_rating;
    }

    public String getEstimate_rating() {
        return estimate_rating;
    }

    public void setEstimate_rating(String estimate_rating) {
        this.estimate_rating = estimate_rating;
    }
}
