package com.cipl.meandmo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notify {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("message")
    @Expose
    public String message;


}
