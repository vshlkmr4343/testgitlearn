package com.tradiuus.models;

import java.util.ArrayList;

public class Job {
    private String job_id;
    private String service_job_id;
    private String job_name;
    private String job_cost;
    private String geo_job;
    private String geo_job_user_id;
    private String geo_job_user_type;
    private String job_status;
    private String status;
    private Review job_review;
    private String job_rating;
    private ArrayList<String> images = new ArrayList<String>();
    private String created_on;
    private String updated_on;
    private String service_type;
    private Trade job_trade;
    private Customer customer_details;
    private ArrayList<Technician> technician = new ArrayList<Technician>();

    public Place getPlace_details() {
        return place_details;
    }

    public void setPlace_details(Place place_details) {
        this.place_details = place_details;
    }

    private Place place_details;
    public boolean select = false;

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getService_job_id() {
        return service_job_id;
    }

    public void setService_job_id(String service_job_id) {
        this.service_job_id = service_job_id;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getJob_cost() {
        return job_cost;
    }

    public void setJob_cost(String job_cost) {
        this.job_cost = job_cost;
    }

    public String getGeo_job() {
        return geo_job;
    }

    public void setGeo_job(String geo_job) {
        this.geo_job = geo_job;
    }

    public String getGeo_job_user_id() {
        return geo_job_user_id;
    }

    public void setGeo_job_user_id(String geo_job_user_id) {
        this.geo_job_user_id = geo_job_user_id;
    }

    public String getGeo_job_user_type() {
        return geo_job_user_type;
    }

    public void setGeo_job_user_type(String geo_job_user_type) {
        this.geo_job_user_type = geo_job_user_type;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Review getJob_review() {
        return job_review;
    }

    public void setJob_review(Review job_review) {
        this.job_review = job_review;
    }

    public String getJob_rating() {
        return job_rating;
    }

    public void setJob_rating(String job_rating) {
        this.job_rating = job_rating;
    }

//    public ArrayList<Image> getImages() {
//        return images;
//    }
//
//    public void setImages(ArrayList<Image> images) {
//        this.images = images;
//    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public Trade getJob_trade() {
        return job_trade;
    }

    public void setJob_trade(Trade job_trade) {
        this.job_trade = job_trade;
    }

    public Customer getCustomer_details() {
        return customer_details;
    }

    public void setCustomer_details(Customer customer_details) {
        this.customer_details = customer_details;
    }

    public ArrayList<Technician> getTechnician() {
        return technician;
    }

    public void setTechnician(ArrayList<Technician> technician) {
        this.technician = technician;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}

