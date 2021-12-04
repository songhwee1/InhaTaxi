package com.example.inhataxi;


public class SampleData {
    private String start;
    private String end;
    private String phone;

    public SampleData(String start, String end, String phone) {
        this.start = start;
        this.end = end;
        this.phone = phone;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }
}