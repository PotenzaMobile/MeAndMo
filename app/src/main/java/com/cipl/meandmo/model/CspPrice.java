package com.cipl.meandmo.model;

/**
 * Created by Dhruv rathod on 8/16/2021
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
public class CspPrice {

    @SerializedName("role")
    @Expose
    public String role;
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("min_qty")
    @Expose
    public String minQty;
    @SerializedName("price_type")
    @Expose
    public String priceType;

    public CspPrice withRole(String role) {
        this.role = role;
        return this;
    }

    public CspPrice withPrice(String price) {
        this.price = price;
        return this;
    }

    public CspPrice withMinQty(String minQty) {
        this.minQty = minQty;
        return this;
    }

    public CspPrice withPriceType(String priceType) {
        this.priceType = priceType;
        return this;
    }

    public CspPrice(String role, String price, String minQty, String priceType) {
        this.role = role;
        this.price = price;
        this.minQty = minQty;
        this.priceType = priceType;
    }
}
