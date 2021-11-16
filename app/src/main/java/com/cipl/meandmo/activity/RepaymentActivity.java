package com.cipl.meandmo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.multidex.BuildConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.cipl.meandmo.R;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RepaymentActivity extends BaseActivity implements OnResponseListner {


    private static final String TAG = "RepaymentActivity";
    @BindView(R.id.wvRepayment)
    WebView wvRepayment;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    String url, thank_you_url, home_url, track_url, thank_you_again;
    private DatabaseHelper databaseHelper;
    private boolean isfirstLoad = false;
    private int buyNow;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment);

        databaseHelper = new DatabaseHelper(this);
        ButterKnife.bind(this);
        settvImage();
        hideSearchNotification();
        setToolbarTheme();
        showBackButton();
        setScreenLayoutDirection();
        url = getIntent().getExtras().getString(RequestParamUtils.RepaymentURL);
        thank_you_again = getIntent().getExtras().getString(RequestParamUtils.THANKYOUExtra);
        thank_you_url = getIntent().getExtras().getString(RequestParamUtils.THANKYOU);

        buyNow = getIntent().getExtras().getInt(RequestParamUtils.buynow);
        wvRepayment.getSettings().setLoadsImagesAutomatically(true);
        wvRepayment.getSettings().setJavaScriptEnabled(true);
        wvRepayment.getSettings().setDomStorageEnabled(true);
        wvRepayment.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        wvRepayment.setWebViewClient(new WebViewClient());
        wvRepayment.setWebChromeClient(new WebChromeClient());
        wvRepayment.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wvRepayment.setWebViewClient(new RepaymentActivity.myWebClient());
        try {
            JSONObject jsonObject = new JSONObject();
            String customerId = getPreferences().getString(RequestParamUtils.ID, "");
            jsonObject.put(RequestParamUtils.REPAY, "yes");
            jsonObject.put(RequestParamUtils.FROMAPP, "yes");
            jsonObject.put(RequestParamUtils.user_id, customerId);

            String postData = jsonObject.toString();
            wvRepayment.postUrl(url, postData.getBytes());
            showProgress("");
            setToolbarTheme();

        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }

        //Initiated Checkout Facebook
    }

    @OnClick(R.id.ivBack)
    public void ivBackClick() {
        if (wvRepayment.canGoBack()) {
            wvRepayment.goBack();
        } else {

            wvRepayment.clearCache(true);
            wvRepayment.clearHistory();
            logout();
        }
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            Log.e("Responce", "----------------" + url);


            if (url.contains(thank_you_url)) {

                SharedPreferences.Editor pre = getPreferences().edit();
                pre.putString(RequestParamUtils.ABANDONED, "");
                pre.putString(RequestParamUtils.ABANDONEDTIME, "");
                pre.commit();


                Intent intent = new Intent(RepaymentActivity.this, ThankYouActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                wvRepayment.clearCache(true);
                wvRepayment.clearHistory();

            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            // progress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
            dismissProgress();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvRepayment.canGoBack()) {
            wvRepayment.goBack();
            return true;
        } else {
            wvRepayment.clearCache(true);
            wvRepayment.clearHistory();

            logout();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void logout() {
        if (Utils.isInternetConnected(this)) {
//            showProgress("");

            PostApi postApi = new PostApi(this, "logout", this, getlanuage());
            postApi.callPostApi(new URLS().LOGOUT, "");
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onResponse(final String response, String methodName) {
        dismissProgress();
        if (methodName.equals(RequestParamUtils.logout)) {


            if (response != null && response.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {
                        finish();
                    } else {

                    }
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }

}
