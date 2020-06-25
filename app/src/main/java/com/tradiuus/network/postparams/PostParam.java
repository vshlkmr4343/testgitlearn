package com.tradiuus.network.postparams;

import com.tradiuus.models.Image;

import java.util.ArrayList;

public class PostParam {
    private String company_name;
    private String contact_person;
    private String mobile_number;
    private String website_url;
    private String email_id;
    private String password;
    private String license;
    private String license_number;
    private String gl_insurance;
    private String wc_insurance;
    private Image images;
    private String trade_id;
    private String total_technicians = "0";
    private String background_check = "10";
    private ArrayList<String> technicians = new ArrayList<String>();
    private String available_trucks = "1";
    private String business_start_time = "6.00 AM";
    private String business_end_time = "12.00 PM";
    private ArrayList<String> zip_codes = new ArrayList<String>();
    private ArrayList<ServiceParam> emergency_services = new ArrayList<ServiceParam>();
    private ArrayList<ServiceParam> estimate_services = new ArrayList<ServiceParam>();
    private String emergency_auto_response_time = "30";
    private String estimate_auto_response_time = "30";
    private String certification_and_oath = "1";

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public String getGl_insurance() {
        return gl_insurance;
    }

    public void setGl_insurance(String gl_insurance) {
        this.gl_insurance = gl_insurance;
    }

    public String getWc_insurance() {
        return wc_insurance;
    }

    public void setWc_insurance(String wc_insurance) {
        this.wc_insurance = wc_insurance;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getTotal_technicians() {
        return total_technicians;
    }

    public void setTotal_technicians(String total_technicians) {
        this.total_technicians = total_technicians;
    }

    public String getBackground_check() {
        return background_check;
    }

    public void setBackground_check(String background_check) {
        this.background_check = background_check;
    }

    public ArrayList<String> getTechnicians() {
        return technicians;
    }

    public void setTechnicians(ArrayList<String> technicians) {
        this.technicians = technicians;
    }

    public String getAvailable_trucks() {
        return available_trucks;
    }

    public void setAvailable_trucks(String available_trucks) {
        this.available_trucks = available_trucks;
    }

    public String getBusiness_start_time() {
        return business_start_time;
    }

    public void setBusiness_start_time(String business_start_time) {
        this.business_start_time = business_start_time;
    }

    public String getBusiness_end_time() {
        return business_end_time;
    }

    public void setBusiness_end_time(String business_end_time) {
        this.business_end_time = business_end_time;
    }

    public ArrayList<String> getZip_codes() {
        return zip_codes;
    }

    public void setZip_codes(ArrayList<String> zip_codes) {
        this.zip_codes = zip_codes;
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

    public String getEmergency_auto_response_time() {
        return emergency_auto_response_time;
    }

    public void setEmergency_auto_response_time(String emergency_auto_response_time) {
        this.emergency_auto_response_time = emergency_auto_response_time;
    }

    public String getEstimate_auto_response_time() {
        return estimate_auto_response_time;
    }

    public void setEstimate_auto_response_time(String estimate_auto_response_time) {
        this.estimate_auto_response_time = estimate_auto_response_time;
    }

    public String getCertification_and_oath() {
        return certification_and_oath;
    }

    public void setCertification_and_oath(String certification_and_oath) {
        this.certification_and_oath = certification_and_oath;
    }
}
