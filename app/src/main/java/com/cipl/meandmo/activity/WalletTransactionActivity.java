package com.cipl.meandmo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipl.meandmo.R;
import com.cipl.meandmo.adapter.WalletTransectionAdapter;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.WalletTransaction;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Config;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletTransactionActivity extends BaseActivity implements OnResponseListner, OnItemClickListner {

    @BindView(R.id.rvWalletTransection)
    RecyclerView rvWalletTransection;

    @BindView(R.id.shimmer_view_wallet)
    ShimmerFrameLayout shimmer_view_wallet;

    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;

    @BindView(R.id.llEmptyWallet)
    LinearLayout llEmptyWallet;

    @BindView(R.id.tvEmptyTitle)
    TextViewBold tvEmptyTitle;

    @BindView(R.id.icAddTransection)
    ImageView icAddTransection;

    //Todo : global variable
    private String customerId;
    WalletTransectionAdapter walletTransectionAdapter;
    List<WalletTransaction.Transaction> list = new ArrayList<>();
    private String  THANKYOU, THANKYOUMAIN;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_teansection);
        ButterKnife.bind(this);
        setToolbarTheme();
        setEmptyColor();
        setScreenLayoutDirection();
        settvTitle(getResources().getString(R.string.my_wallet));
        hideSearchNotification();
        showBackButton();
        customerId = getPreferences().getString(RequestParamUtils.ID, "");
    }

    public void setToolbarTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
    }

    public void setEmptyColor() {
        TextViewRegular tvContinueShopping = findViewById(R.id.tvContinueShopping);
     //   ImageView ivGo = findViewById(R.id.ivGo);
        tvContinueShopping.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(5, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvContinueShopping.setBackground(gradientDrawable);
      //  ivGo.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    public void getWalletTransectionData() {
        if (Utils.isInternetConnected(this)) {
            //showProgress("");
            if (Config.SHIMMER_VIEW) {
                shimmer_view_wallet.startShimmerAnimation();
                shimmer_view_wallet.setVisibility(View.VISIBLE);
            } else {
                shimmer_view_wallet.setVisibility(View.GONE);
                showProgress("");
            }
            try {
                PostApi postApi = new PostApi(this, RequestParamUtils.Wallet, this, getlanuage());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, customerId);
                postApi.callPostApi(new URLS().WALLET + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("wallettransection", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    public void setWalletTransectionData() {
        walletTransectionAdapter = new WalletTransectionAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvWalletTransection.setLayoutManager(mLayoutManager);
        rvWalletTransection.setAdapter(walletTransectionAdapter);
        rvWalletTransection.setNestedScrollingEnabled(false);
    }

    public void showEmpty() {
        llEmpty.setVisibility(View.VISIBLE);
        tvEmptyTitle.setText(R.string.no_notification_yet);
    }

    @Override
    public void onResponse(final String response, String methodName) {
        if (methodName.equals(RequestParamUtils.Wallet)) {
            if (response != null && response.length() > 0) {

                if (Config.SHIMMER_VIEW) {
                    shimmer_view_wallet.stopShimmerAnimation();
                    shimmer_view_wallet.setVisibility(View.GONE);
                } else {
                    dismissProgress();
                }
                if (response != null && response.length() > 0) {
                    try {
                        final WalletTransaction walletTransaction = new Gson().fromJson(
                                response, new TypeToken<WalletTransaction>() {
                                }.getType());

                        if (walletTransaction.status.equals("success")) {
                            llEmpty.setVisibility(View.GONE);
                            list = new ArrayList<>();
                            Log.e("---------------", "onResponse: "+walletTransaction.getTransactions().size());
                            list.addAll(walletTransaction.getTransactions());
                            if(walletTransaction.getTransactions().size() > 0){
                                llEmptyWallet.setVisibility(View.GONE);

                            }else {
                                llEmptyWallet.setVisibility(View.VISIBLE);
                            }

                            walletTransectionAdapter.addAll(list);
                            URL = walletTransaction.getTopupPage();
                            THANKYOUMAIN = walletTransaction.getThankyou();
                            THANKYOU =  walletTransaction.getThankyouEndpoint();
                            if (!THANKYOUMAIN.isEmpty()) {
                                Constant.CheckoutURL.add(THANKYOUMAIN);
                            }
                            if (!THANKYOU.isEmpty()) {
                                Constant.CheckoutURL.add(THANKYOU);
                            }


                        } else {
                            showEmpty();
                        }
                    } catch (Exception e) {
                        Log.e("error", e.getMessage());
                    }
                } else {
                    showEmpty();
                }
            }
        }
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {
    }

    @OnClick(R.id.icAddTransection)
    public void onViewClicked() {
        Intent i = new Intent(WalletTransactionActivity.this, WebviewWalletAddActivity.class);
        i.putExtra(RequestParamUtils.TRANSECTION_URL,URL);
        startActivity(i) ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWalletTransectionData();
        setWalletTransectionData();
    }
}
