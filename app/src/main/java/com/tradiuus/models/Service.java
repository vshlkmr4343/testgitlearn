package com.tradiuus.models;

import java.util.ArrayList;

public class Service {
    private String service_type_id;
    private String service_type_name;
    private String contractor_charge;
    private String contractor_response_time;
    private String price_message;
    private String technician_message;

    private String service_id;
    private String service_name;



    private String service_max_price;
    private String service_min_price;
    private ArrayList<Question> service_questions = new ArrayList<Question>();
    private ArrayList<Question> service_question = new ArrayList<Question>();
    public boolean isSelected = false;

    public String getService_max_price() {
        return service_max_price;
    }

    public void setService_max_price(String service_max_price) {
        this.service_max_price = service_max_price;
    }

    public String getService_min_price() {
        return service_min_price;
    }

    public void setService_min_price(String service_min_price) {
        this.service_min_price = service_min_price;
    }
    public String getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(String service_type_id) {
        this.service_type_id = service_type_id;
    }

    public String getService_type_name() {
        return service_type_name;
    }

    public void setService_type_name(String service_type_name) {
        this.service_type_name = service_type_name;
    }

    public String getContractor_charge() {
        return contractor_charge;
    }

    public void setContractor_charge(String contractor_charge) {
        this.contractor_charge = contractor_charge;
    }

    public String getContractor_response_time() {
        return contractor_response_time;
    }

    public void setContractor_response_time(String contractor_response_time) {
        this.contractor_response_time = contractor_response_time;
    }

    public String getPrice_message() {
        return price_message;
    }

    public void setPrice_message(String price_message) {
        this.price_message = price_message;
    }

    public String getTechnician_message() {
        return technician_message;
    }

    public void setTechnician_message(String technician_message) {
        this.technician_message = technician_message;
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

    public ArrayList<Question> getService_questions() {
        return service_questions;
    }

    public void setService_questions(ArrayList<Question> service_question) {
        this.service_questions = service_question;
    }

    public ArrayList<Question> getService_question() {
        return service_question;
    }

    public void setService_question(ArrayList<Question> service_questions) {
        this.service_question = service_questions;
    }
}
