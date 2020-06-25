package com.tradiuus.models;

public class Image {
    private String image_id;
    private String caption;
    private String image;
    private String image_order;

    public Image() {

    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_order() {
        return image_order;
    }

    public void setImage_order(String image_order) {
        this.image_order = image_order;
    }

    private String generic_insurance_img;
    private String trade_license_img;
    private String company_logo;
    private String image_url;

    public Image(String generic_insurance_img, String trade_license_img, String company_logo) {
        this.generic_insurance_img = generic_insurance_img;
        this.trade_license_img = trade_license_img;
        this.company_logo = company_logo;
    }

    public String getGeneric_insurance_img() {
        return generic_insurance_img;
    }

    public void setGeneric_insurance_img(String generic_insurance_img) {
        this.generic_insurance_img = generic_insurance_img;
    }

    public String getTrade_license_img() {
        return trade_license_img;
    }

    public void setTrade_license_img(String trade_license_img) {
        this.trade_license_img = trade_license_img;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
