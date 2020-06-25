package com.tradiuus.models;

public class LuxuryData {

    private String location;
    private String desc;
    private String id;
    private String name;
    private String review;
    public boolean select;

    public LuxuryData(String location, String desc, String name, String id, String review, boolean select) {
        this.location = location;
        this.desc = desc;
        this.name = name;
        this.id = id;
        this.review = review;
        this.select = select;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
