package com.tradiuus.models;

public class Value {
    private String option_id;
    private String option;

    public Value() {
    }

    public Value(String option_id, String option) {
        this.option_id = option_id;
        this.option = option;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
