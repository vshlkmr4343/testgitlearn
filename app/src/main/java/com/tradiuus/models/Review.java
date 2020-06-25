package com.tradiuus.models;

public class Review {
    private String overall;
    private String price;
    private String quantity;
    private String timeliness;
    private String professionalism;
    private String review;

    public String getOverall() {
        return overall;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getTimeliness() {
        return timeliness;
    }

    public String getProfessionalism() {
        return professionalism;
    }

    public String getReview() {
        return review;
    }

    public void setOverall(String overall) {
        this.overall = overall;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setTimeliness(String timeliness) {
        this.timeliness = timeliness;
    }

    public void setProfessionalism(String professionalism) {
        this.professionalism = professionalism;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
