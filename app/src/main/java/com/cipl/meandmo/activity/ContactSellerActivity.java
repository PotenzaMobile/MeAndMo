package com.cipl.meandmo.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.edittext.EditTextRegular;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.model.Customer;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactSellerActivity extends BaseActivity implements OnResponseListner {

    @BindView(R.id.etName)
    EditTextRegular etName;

    @BindView(R.id.etEmail)
    EditTextRegular etEmail;

    @BindView(R.id.etMessage)
    EditTextRegular etMessage;

    @BindView(R.id.tvSend)
    TextViewBold tvSend;

    private String sellerid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_seller);
        ButterKnife.bind(this);
        setScreenLayoutDirection();
        setCustomerData();
        setToolbarTheme();
        setThemeColor();
        String dealername = getIntent().getExtras().getString(RequestParamUtils.Dealer);
//        if (dealername != null) {
//            settvTitle(dealername);
//        } else {
//            settvTitle(RequestParamUtils.Dealer);
//        }

        settvTitle(getResources().getString(R.string.contact_seller));
        showBackButton();
        sellerid = getIntent().getExtras().getString(RequestParamUtils.ID);
    }

    private void setThemeColor() {
        Drawable unwrappedDrawable = tvSend.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor((getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)))));

    }

    public void setCustomerData() {
        String cust = getPreferences().getString(RequestParamUtils.CUSTOMER, "");
        if (!cust.equals("")) {
            Customer customer = new Gson().fromJson(
                    cust, new TypeToken<Customer>() {
                    }.getType());


            etName.setText(customer.firstName + " " + customer.lastName);
            etEmail.setText(customer.email + "");
        }

    }


    @OnClick(R.id.tvSend)
    public void tvSendClick() {
        if (etName.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_name, Toast.LENGTH_SHORT).show();
        } else {
            if (etEmail.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isValidEmail(etEmail.getText().toString())) {
                    if (etMessage.getText().toString().isEmpty()) {
                        Toast.makeText(this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                    } else {
                        contactSeller();
                    }
                } else {
                    Toast.makeText(this, R.string.enter_valid_email_address, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void contactSeller() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(ContactSellerActivity.this, RequestParamUtils.contactSeller, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.name, etName.getText().toString());
                jsonObject.put(RequestParamUtils.email, etEmail.getText().toString());
                jsonObject.put(RequestParamUtils.message, etMessage.getText().toString());
                jsonObject.put(RequestParamUtils.sellerId, sellerid);

                postApi.callPostApi(new URLS().CONTACT_SELLER, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.equals(RequestParamUtils.contactSeller)) {
            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    //set call here
                    JSONObject jsonObj = new JSONObject(response);

                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(this, R.string.message_sent_successfully, Toast.LENGTH_SHORT).show();
                        etName.getText().clear();
                        etEmail.getText().clear();
                        etMessage.getText().clear();
                        finish();
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
