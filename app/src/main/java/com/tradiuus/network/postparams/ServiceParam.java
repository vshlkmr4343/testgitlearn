package com.tradiuus.network.postparams;

public class ServiceParam {
    private String service_id;
    private String price_max;
    private String price_min;
    private String job_time;
    private String service_name;
    public int isSelected = 0;

    public ServiceParam(String service_id, String price_max, String price_min, String job_time) {
        this.service_id = service_id;
        this.price_min = price_min;
        this.price_max = price_max;
        this.job_time = job_time;
    }

    public ServiceParam(String service_id, String price_max, String price_min, String job_time, String service_name) {
        this.service_id = service_id;
        this.price_max = price_max;
        this.price_min = price_min;
        this.job_time = job_time;
        this.service_name = service_name;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getPrice_min() {
        return price_min;
    }

    public void setPrice_min(String price_min) {
        this.price_min = price_min;
    }

    public String getPrice_max() {
        return price_max;
    }

    public void setPrice_max(String price_max) {
        this.price_max = price_max;
    }

    public String getJob_time() {
        return job_time;
    }

    public void setJob_time(String job_time) {
        this.job_time = job_time;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }
}
