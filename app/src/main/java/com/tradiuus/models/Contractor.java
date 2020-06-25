package com.tradiuus.models;

import java.util.ArrayList;
import java.util.List;

public class Contractor {
    private String user_id;
    private String contractor_id;
    private int user_type;
    private String company_name;
    private int status;
    private String contact_person;
    private String mobile_number;
    private String website_url;
    private String email_id;
    private String license;
    private String license_number;
    private String gl_insurance;
    private String wc_insurance;
    private String video_url;
    private Image images;
    private ArrayList<Trade> trade;
    private int total_technicians;
    private String background_check;
    private ArrayList<Technician> technicians = new ArrayList<>();
    private String available_trucks;
    private String business_start_time;
    private String business_end_time;
    private ArrayList<String> zip_codes = new ArrayList<String>();
    private String emergency_auto_response_time;
    private String estimate_auto_response_time;
    private String certification_and_oath;
    private String cardNo;
    public boolean isCardSave = false;
    public String credit_card_number;

    private String module_id;
    private String module_name;
    private List<Question> module_questions;
    private List<Job> geo_jobs;
    private String overall_rating;
    private boolean shownImp = false;
    private String faqs;
    private String videos;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContractor_id() {
        return contractor_id;
    }

    public void setContractor_id(String contractor_id) {
        this.contractor_id = contractor_id;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public ArrayList<Trade> getTrade() {
        return trade;
    }

    public void setTrade(ArrayList<Trade> trade) {
        this.trade = trade;
    }

    public int getTotal_technicians() {
        return total_technicians;
    }

    public void setTotal_technicians(int total_technicians) {
        this.total_technicians = total_technicians;
    }

    public String getBackground_check() {
        return background_check;
    }

    public void setBackground_check(String background_check) {
        this.background_check = background_check;
    }

    public ArrayList<Technician> getTechnicians() {
        return technicians;
    }

    public void setTechnicians(ArrayList<Technician> technicians) {
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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public boolean isCardSave() {
        return isCardSave;
    }

    public void setCardSave(boolean cardSave) {
        isCardSave = cardSave;
    }

    public String getCredit_card_number() {
        return credit_card_number;
    }

    public void setCredit_card_number(String credit_card_number) {
        this.credit_card_number = credit_card_number;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public List<Question> getModule_questions() {
        return module_questions;
    }

    public void setModule_questions(List<Question> module_questions) {
        this.module_questions = module_questions;
    }

    public List<Job> getGeo_jobs() {
        return geo_jobs;
    }

    public void setGeo_jobs(List<Job> geo_jobs) {
        this.geo_jobs = geo_jobs;
    }

    public String getOverall_rating() {
        return overall_rating;
    }

    public void setOverall_rating(String overall_rating) {
        this.overall_rating = overall_rating;
    }

    public boolean isShownImp() {
        return shownImp;
    }

    public void setShownImp(boolean shownImp) {
        this.shownImp = shownImp;
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
