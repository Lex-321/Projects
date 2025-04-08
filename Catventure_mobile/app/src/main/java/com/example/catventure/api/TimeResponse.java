package com.example.catventure.api;

import com.google.gson.annotations.SerializedName;

public class TimeResponse {
    @SerializedName("datetime")
    private String datetime;
    public String getDatetime() {
        return datetime;
    }
}
