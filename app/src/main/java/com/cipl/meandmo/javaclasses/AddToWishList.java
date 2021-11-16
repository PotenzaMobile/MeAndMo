package com.cipl.meandmo.javaclasses;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.like.animation.SparkButton;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.WishList;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;

import org.json.JSONObject;

public class AddToWishList implements OnResponseListner {

    private Activity       activity;
    private DatabaseHelper databaseHelper;
    private CategoryList productDetail;

    public AddToWishList(Activity activity) {
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }

    public void addToWishList(final SparkButton ivWishList, String detail, final TextViewRegular tvPrice1) {

        this.productDetail = new Gson().fromJson(detail, new TypeToken<CategoryList>() {
        }.getType());


        final String userid = ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.ID, "");
        ivWishList.setActivetint(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ivWishList.setColors(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)), Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        if (Constant.IS_WISH_LIST_ACTIVE) {
            ivWishList.setVisibility(View.VISIBLE);

            if (databaseHelper.getWishlistProduct(productDetail.id + "")) {
                ivWishList.setChecked(true);
            } else {
                ivWishList.setChecked(false);
            }
        } else {
            ivWishList.setVisibility(View.GONE);
        }
        ivWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.getWishlistProduct(productDetail.id + "")) {
                    ivWishList.setChecked(false);
                    if (!userid.equals("")) {
                        removeWishList(true, userid, productDetail.id + "");
                    }
                    databaseHelper.deleteFromWishList(productDetail.id + "");
                } else {
                    ivWishList.setChecked(true);
                    ivWishList.playAnimation();
                    WishList wishList = new WishList();
                    wishList.setProduct(new Gson().toJson(productDetail));
                    wishList.setCspPrice(new Gson().toJson(productDetail.cspPrices));
                    wishList.setProductid(productDetail.id + "");
                    databaseHelper.addToWishList(wishList);
                    if (!userid.equals("")) {
                        addWishList(true, userid, productDetail.id + "");
                    }

                    String value = tvPrice1.getText().toString();
                    if (value.contains(Constant.CURRENCYSYMBOL)) {
                        value = value.replaceAll(Constant.CURRENCYSYMBOL, "");
                    }
                    if (value.contains(Constant.CURRENCYSYMBOL)) {
                        value = value.replace(Constant.CURRENCYSYMBOL, "");
                    }
                    value = value.replaceAll("\\s", "");
                    value = value.replaceAll(",", "");
                    try {
                        ((BaseActivity) activity).logAddedToWishlistEvent(String.valueOf(productDetail.id), productDetail.name, Constant.CURRENCYSYMBOL, Double.parseDouble(value));
                    } catch (Exception e) {

                    }
                }
            }
        });
    }


    public void addWishList(boolean isDialogShow, String userid, String productid) {
        if (Utils.isInternetConnected(activity)) {
            if (isDialogShow) {
                ((BaseActivity) activity).showProgress("");
            }

            PostApi postApi = new PostApi(activity, RequestParamUtils.addWishList, this, ((BaseActivity) activity).getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, userid);
                jsonObject.put(RequestParamUtils.PRODUCT_ID, productid);
                postApi.callPostApi(new URLS().ADD_TO_WISHLIST + ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    public void removeWishList(boolean isDialogShow, String userid, String productid) {
        if (Utils.isInternetConnected(activity)) {
            if (isDialogShow) {
                ((BaseActivity) activity).showProgress("");
            }

            PostApi postApi = new PostApi(activity, RequestParamUtils.removeWishList, this, ((BaseActivity) activity).getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, userid);
                jsonObject.put(RequestParamUtils.PRODUCT_ID, productid);
                postApi.callPostApi(new URLS().REMOVE_FROM_WISHLIST, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        ((BaseActivity) activity).dismissProgress();
    }
}
