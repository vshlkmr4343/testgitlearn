package com.tradiuus.models;

import android.util.Log;

public class Technician {
    private String user_type;
    private String user_id;
    private String technician_id;
    private String status;
    private String email_id;
    private String technician_name;
    private String mobile;
    private String profile_image;
    private String contractor_id;
    private String company_name;
    private String name;
    private String email;
    public boolean isNew = false;
    public boolean isExpand = false;
    private String technician_profile;
    private String technician_mobile;
    private String technician_email;

    private String distance;
    private String technician_message;
    private String technician_latitude;
    private String technician_longitude;
    private String trade_id;
    private String service_id;
    private String service_name;
    private String service_type;
    private Contractor contractor_details;
    private String faqs;
    private String videos;

    public Technician() {

    }

    public Technician(String technician_id, String name, String mobile, String email, boolean isNew, boolean isExpand) {
        this.technician_id = technician_id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.isNew = isNew;
        this.isExpand = isExpand;

    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTechnician_id() {
        return technician_id;
    }

    public void setTechnician_id(String technician_id) {
        this.technician_id = technician_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getTechnician_name() {
        return technician_name;
    }

    public void setTechnician_name(String technician_name) {
        this.technician_name = technician_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getContractor_id() {
        return contractor_id;
    }

    public void setContractor_id(String contractor_id) {
        this.contractor_id = contractor_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getTechnician_profile() {
        return technician_profile;
    }

    public void setTechnician_profile(String technician_profile) {
        this.technician_profile = technician_profile;
    }

    public String getTechnician_mobile() {
       Log.d("Technician_mobile:",""+technician_mobile);
        return technician_mobile;
    }

    public void setTechnician_mobile(String Technician_mobile) {

        this.technician_mobile = technician_mobile;
    }

    public String getTechnician_email() {
        return technician_email;
    }

    public void setTechnician_email(String technician_email) {
        this.technician_email = technician_email;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTechnician_message() {
        return technician_message;
    }

    public void setTechnician_message(String technician_message) {
        this.technician_message = technician_message;
    }

    public String getTechnician_latitude() {
        return technician_latitude;
    }

    public void setTechnician_latitude(String technician_latitude) {
        this.technician_latitude = technician_latitude;
    }

    public String getTechnician_longitude() {
        return technician_longitude;
    }

    public void setTechnician_longitude(String technician_longitude) {
        this.technician_longitude = technician_longitude;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
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

    public Contractor getContractor_details() {
        return contractor_details;
    }

    public void setContractor_details(Contractor contractor_details) {
        this.contractor_details = contractor_details;
    }

    public String getFaqs() {
        return faqs;
    }

    public void setFaqs(String faqs) {
        this.faqs = faqs;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }
}
