package com.cipl.meandmo.activity;

import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.cipl.meandmo.R;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends BaseActivity {

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private SellerInfoActivity.State mCurrentState = SellerInfoActivity.State.IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_two);
        ButterKnife.bind(this);
        initCollapsingToolbar();
    }

    private void initCollapsingToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

                if (i == 0) {
                    if (mCurrentState != SellerInfoActivity.State.EXPANDED) {
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.TRANSPARENT);
                        }
                    }
                    mCurrentState = SellerInfoActivity.State.EXPANDED;
                } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                    if (mCurrentState != SellerInfoActivity.State.COLLAPSED) {
                        toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                        }
                    }
                    mCurrentState = SellerInfoActivity.State.COLLAPSED;
                } else {
                    if (mCurrentState != SellerInfoActivity.State.IDLE) {
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.TRANSPARENT);
                        }
                    }
                    mCurrentState = SellerInfoActivity.State.IDLE;
                }
            }
        });
    }

}