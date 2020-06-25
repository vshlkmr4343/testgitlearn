package com.tradiuus.models;

public class Reason {
    private String reason_id;
    private String reason;
    private String display_order;
    public boolean isSelected = false;

    public Reason(String reason_id, String reason, String display_order, boolean isSelected) {
        this.reason_id = reason_id;
        this.reason = reason;
        this.display_order = display_order;
        this.isSelected = isSelected;
    }

    public String getReason_id() {
        return reason_id;
    }

    public void setReason_id(String reason_id) {
        this.reason_id = reason_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(String display_order) {
        this.display_order = display_order;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
