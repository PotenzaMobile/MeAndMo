package com.cipl.meandmo.model;

/**
 * Created by Dhruv rathod on 8/12/2021
 */
public class QuantityDiscount {
    String Quantity = "", Discount = "";

    public QuantityDiscount(String quantity, String discount) {
        Quantity = quantity;
        Discount = discount;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
