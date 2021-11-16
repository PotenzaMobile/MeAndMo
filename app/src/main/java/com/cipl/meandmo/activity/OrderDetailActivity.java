package com.cipl.meandmo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.cipl.meandmo.R;
import com.cipl.meandmo.adapter.OrderDetailAdapter;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Orders;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailActivity extends BaseActivity implements OnItemClickListner, OnResponseListner {

    @BindView(R.id.tvOrderId)
    TextViewRegular tvOrderId;

    @BindView(R.id.tvOrderDateAndStatus)
    TextViewRegular tvOrderDateAndStatus;

    @BindView(R.id.tvTrackMessage1)
    TextViewRegular tvTrackMessage1;

    @BindView(R.id.tvTrackMessage2)
    TextViewRegular tvTrackMessage2;

    @BindView(R.id.tvTrackID)
    TextViewRegular tvTrackID;

    @BindView(R.id.tvSubTotal)
    TextViewBold tvSubTotal;

    @BindView(R.id.tvShippingCharges)
    TextViewBold tvShippingCharges;

    @BindView(R.id.tvPaymentMethodTitle)
    TextViewBold tvPaymentMethodTitle;

    @BindView(R.id.tvTotal)
    TextViewBold tvTotal;

    @BindView(R.id.tvTotalAmountlabel)
    TextViewBold tvTotalAmountlabel;

    @BindView(R.id.tvBillingEmail)
    TextViewLight tvBillingEmail;


    @BindView(R.id.tvShippingEmail)
    TextViewLight tvShippingEmail;

    @BindView(R.id.tvShippingPhone)
    TextViewLight tvShippingPhone;

    @BindView(R.id.tvBillingPhone)
    TextViewLight tvBillingPhone;

   /* @BindView(R.id.tvPhone)
    TextViewLight tvPhone;
*/

    @BindView(R.id.tvBillingAddresslabel)
    TextViewRegular tvBillingAddresslabel;


  /*  @BindView(R.id.tvBillingCompanyName)
    TextViewLight tvBillingCompanyName;*/

    @BindView(R.id.tvBillingName)
    TextViewLight tvBillingName;

 /* @BindView(R.id.tvBillingAddress1)
    TextViewLight tvBillingAddress1;*/

    @BindView(R.id.tvBillingAddress)
    TextViewLight tvBillingAddress;

   /* @BindView(R.id.tvBillingCityPin)
    TextViewLight tvBillingCityPin;*/

   /* @BindView(R.id.tvBillingCountryState)
    TextViewLight tvBillingCountryState;

    @BindView(R.id.tvShippingCompanyName)
    TextViewLight tvShippingCompanyName;*/

    //
    @BindView(R.id.tvShippingAddresslabel)
    TextViewRegular tvShippingAddresslabel;

    @BindView(R.id.tvShippingName)
    TextViewLight tvShippingName;

    @BindView(R.id.tvShippingAddress)
    TextViewLight tvShippingAddress;

   /* @BindView(R.id.tvShippingAddress2)
    TextViewLight tvShippingAddress2;

    @BindView(R.id.tvShippingCityPin)
    TextViewLight tvShippingCityPin;

    @BindView(R.id.tvShippingCountryState)
    TextViewLight tvShippingCountryState;*/

    @BindView(R.id.tvCancelOrder)
    TextViewBold tvCancelOrder;

    /////
    @BindView(R.id.ivBillingaddress)
    ImageView mivBillingaddress;

    @BindView(R.id.ivBillingname)
    ImageView mivBillingname;

    @BindView(R.id.ivBillingphoneno)
    ImageView mivBillingphoneno;

    @BindView(R.id.ivBillingemail)
    ImageView mivBillingemail;
    //
    @BindView(R.id.ivShippingaddress)
    ImageView mivShippingaddress;

    @BindView(R.id.ivShippingname)
    ImageView mivShippingname;

    @BindView(R.id.ivShippingphoneno)
    ImageView mivShippingphoneno;

    @BindView(R.id.ivShippingemail)
    ImageView mivShippingemail;

    @BindView(R.id.llorder)
    LinearLayout llorder;

    @BindView(R.id.tvOrderIdlabel)
    TextViewRegular tvOrderIdlabel;

    @BindView(R.id.tvOrderStatus)
    TextViewRegular tvOrderStatus;


    private Orders orderData = Constant.ORDERDETAIL;
    private OrderDetailAdapter orderDetailAdapter;
    private List<Orders.OrderTrackingData> list = new ArrayList<>();

    String Trackurl;

    @BindView(R.id.rvOrderedContent)
    RecyclerView rvOrderedContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        setScreenLayoutDirection();
        settvTitle(getResources().getString(R.string.my_orders));
        showBackButton();
        setToolbarTheme();
        setColorTheme();
        setData();
    }

    public void setColorTheme() {


        tvOrderIdlabel.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvOrderId.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvTrackID.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvOrderDateAndStatus.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));


        Drawable unwrappedDrawable = tvCancelOrder.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));


         unwrappedDrawable = tvOrderStatus.getBackground();
         wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        //set background and corner
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(8);
        shape.setColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        // now find your view and add background to it
        View view = (LinearLayout) findViewById(R.id.llorder);
        view.setBackground(shape);


        //tvTotalAmountlabel.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvTotal.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvTotalAmountlabel.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        // llorder.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        tvBillingAddresslabel.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvShippingAddresslabel.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        mivBillingaddress.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivBillingname.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivBillingphoneno.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivBillingemail.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivShippingaddress.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivShippingname.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivShippingphoneno.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        mivShippingemail.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    @SuppressLint("SetTextI18n")
    public void setData() {
        String orderId = "#" + orderData.id;
        String upperString = orderData.status.substring(0, 1).toUpperCase() + orderData.status.substring(1);

        if (Constant.IS_ORDER_TRACKING_ACTIVE && orderData.orderTrackingData.size() != 0) {
            for (int i = 0; i < orderData.orderTrackingData.size(); i++) {
                tvTrackMessage1.setVisibility(View.VISIBLE);
                tvTrackMessage2.setVisibility(View.VISIBLE);
                tvTrackMessage1.setText(orderData.orderTrackingData.get(i).trackmessage1);
                tvTrackMessage2.setText(RequestParamUtils.Tracking);
                Trackurl = orderData.orderTrackingData.get(i).ordertrackinglink;
                tvTrackID.setText(orderData.orderTrackingData.get(i).trackmessage2);

                if (orderData.orderTrackingData.get(i).usetrackbutton == true) {
                    tvTrackID.setClickable(true);

                } else {
                    tvTrackID.setClickable(false);
                }
            }


        } else {
            tvTrackMessage1.setVisibility(View.GONE);
            tvTrackMessage2.setVisibility(View.GONE);
        }

//       Locale uk = new Locale("en", "GB");
        Currency pound = Currency.getInstance(orderData.currency);
        String currencySymbol = pound.getSymbol();


        tvOrderDateAndStatus.setText(getString(R.string.order) + " " + orderId + " " + getString(R.string.was_place_on) + " " + Constant.setDate(orderData.dateCreated) + " " + getString(R.string.and_currently) + " " + upperString);

        setProductList(currencySymbol);

        tvOrderStatus.setText(upperString);



        if (orderData.status.toLowerCase().equals("processing")) {
            Log.e("check", "onenter: ");
            Drawable unwrappedDrawable = tvOrderStatus.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.green));


        } else if (orderData.status.toLowerCase().equals("cancelled")) {
            Drawable unwrappedDrawable = tvOrderStatus.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.RED);

        } else if (orderData.status.toLowerCase().equals("completed")) {
            Drawable unwrappedDrawable = tvOrderStatus.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.green));

        } else if (orderData.status.toLowerCase().equals("on-hold")) {
            Drawable unwrappedDrawable = tvOrderStatus.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.green));
        }


        float subtotalPrice = 0;
        for (int i = 0; i < orderData.lineItems.size(); i++) {
            subtotalPrice = subtotalPrice + Float.parseFloat(orderData.lineItems.get(i).total);
        }
        tvSubTotal.setText(currencySymbol + " " + Constant.setDecimal((double) subtotalPrice));
        try {
            tvShippingCharges.setText(currencySymbol + " " + Constant.setDecimal(Double.valueOf((orderData.shippingTotal))));
        } catch (Exception e) {
            tvShippingCharges.setText(currencySymbol + " " + (orderData.shippingTotal));
        }

        try {
            tvTotal.setText(currencySymbol + " " + Constant.setDecimal(Double.valueOf(orderData.total)));
        } catch (Exception e) {
            tvTotal.setText(currencySymbol + " " + orderData.total);
        }
        tvPaymentMethodTitle.setText(orderData.paymentMethodTitle + "");


        tvBillingEmail.setText(orderData.billing.email + "");
        tvShippingEmail.setText(orderData.billing.email + "");

        tvBillingPhone.setText(orderData.billing.phone + "");
        tvShippingPhone.setText(orderData.billing.phone + "");


        //tvBillingCompanyName.setText(orderData.billing.company + "");
        tvBillingName.setText(orderData.billing.firstName + " " + orderData.billing.lastName);
        tvBillingAddress.setText(orderData.billing.address1 + " " + orderData.billing.address2 + "," + orderData.billing.city + " " + orderData.billing.postcode + "");
        // tvBillingAddress2.setText(orderData.billing.address2 + "");
        // tvBillingCityPin.setText(orderData.billing.city + " " + orderData.billing.postcode);

        Locale lcountry = new Locale("", orderData.billing.country);
        String billingCountry = lcountry.getDisplayCountry();
        // tvBillingCountryState.setText(orderData.billing.state + ", " + billingCountry);

        // tvShippingCompanyName.setText(orderData.shipping.company + "");

        tvShippingName.setText(orderData.shipping.firstName + " " + orderData.shipping.lastName);
        tvShippingAddress.setText(orderData.shipping.address1 + " " + orderData.shipping.address2 + "" + orderData.shipping.city + " " + orderData.shipping.postcode + "");

        /* tvShippingAddress2.setText(orderData.shipping.address2 + "");
        tvShippingCityPin.setText(orderData.shipping.city + " " + orderData.shipping.postcode);*/

        Locale lcountryShip = new Locale("", orderData.shipping.country);
        String shippingCountryShip = lcountryShip.getDisplayCountry();
        // tvShippingCountryState.setText(orderData.shipping.state + ", " + shippingCountryShip);

        if (orderData.status.toLowerCase().equals(RequestParamUtils.onHold) || orderData.status.toLowerCase().equals(RequestParamUtils.pending)) {
            tvCancelOrder.setClickable(true);
            tvCancelOrder.setAlpha((float) 1);
        } else {
            tvCancelOrder.setClickable(false);
            tvCancelOrder.setAlpha((float) 0.3);
        }
    }

    @OnClick(R.id.tvTrackID)
    public void tvTrackIdClick() {
        if (!Trackurl.startsWith(RequestParamUtils.UrlStartWith) && !Trackurl.startsWith(RequestParamUtils.UrlStartWithsecure)) {
            Trackurl = RequestParamUtils.UrlStartWith + Trackurl;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Trackurl));
        startActivity(browserIntent);
    }

    public void setProductList(String currencySymbol) {
        orderDetailAdapter = new OrderDetailAdapter(this, this, currencySymbol);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvOrderedContent.setLayoutManager(mLayoutManager);
        rvOrderedContent.setAdapter(orderDetailAdapter);
        rvOrderedContent.setNestedScrollingEnabled(false);
        orderDetailAdapter.addAll(orderData.lineItems);
    }


    @Override
    public void onItemClick(int position, String value, int outerPos) {

    }

    @OnClick(R.id.tvCancelOrder)
    public void setTvCancelOrderClick() {
        cancelOrder();
    }

    public void cancelOrder() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, RequestParamUtils.cancelOrder, this, getlanuage());
            JSONObject object = new JSONObject();
            try {
                object.put(RequestParamUtils.order, orderData.id + "");
                postApi.callPostApi(new URLS().CANCEL_ORDER, object.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {

        if (methodName.equals(RequestParamUtils.cancelOrder)) {
            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    //set call here
                    JSONObject jsonObj = new JSONObject(response);

                    String status = jsonObj.getString("result");
                    if (status.equals("success")) {
                        Toast.makeText(this, R.string.order_is_cancelled, Toast.LENGTH_SHORT).show();
                        tvCancelOrder.setClickable(false);
                        tvCancelOrder.setAlpha((float) 0.3);
                        finish();
                        //TODO:code here for cancelled order
                    } else {
                        Toast.makeText(this, R.string.something_went_wrong_try_after_somtime, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    Toast.makeText(this, R.string.something_went_wrong_try_after_somtime, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
            }
        }
    }

}