package com.cipl.meandmo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.cipl.meandmo.R;
import com.cipl.meandmo.adapter.BannerViewPagerAdapter;
import com.cipl.meandmo.adapter.CategoryAdapter;
import com.cipl.meandmo.adapter.DynamicItemAdapter;
import com.cipl.meandmo.adapter.HomeTopCategoryAdapter;
import com.cipl.meandmo.adapter.NavigationDrawerAdapter;
import com.cipl.meandmo.adapter.RecentViewAdapter;
import com.cipl.meandmo.adapter.RecentlyAddedAdapter;
import com.cipl.meandmo.adapter.SelectProductAdapter;
import com.cipl.meandmo.adapter.SelectedItemAdapter;
import com.cipl.meandmo.adapter.SixReasonAdapter;
import com.cipl.meandmo.adapter.SpecialOfferAdapter;
import com.cipl.meandmo.adapter.TopRatedProductAdapter;
import com.cipl.meandmo.adapter.VerticalBannerAdapter;
import com.cipl.meandmo.customview.CustomViewPager;
import com.cipl.meandmo.customview.EqualSpacingItemDecoration;
import com.cipl.meandmo.customview.GridSpacingItemDecoration;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.Home;
import com.cipl.meandmo.model.NavigationList;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Config;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;
import com.ciyashop.library.apicall.Ciyashop;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements OnItemClickListner, OnResponseListner {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.drawerListView)
    ListView drawerListView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.rvTopCategory)
    RecyclerView rvTopCategory;

    @BindView(R.id.rvCategory)
    RecyclerView rvCategory;

    @BindView(R.id.rvVerticalBanner)
    CustomViewPager rvVerticalBanner;

    @BindView(R.id.rvSixReason)
    RecyclerView rvSixReason;

    @BindView(R.id.rvRecentOffer)
    RecyclerView rvRecentOffer;

    @BindView(R.id.vpBanner)
    ViewPager vpBanner;

    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;

    @BindView(R.id.ivBack)
    ImageView ivDrawer;

    @BindView(R.id.llMain)
    LinearLayout llMain;

    @BindView(R.id.llTopCategory)
    LinearLayout llTopCategory;

    @BindView(R.id.llBanner)
    LinearLayout llBanner;

    @BindView(R.id.llCategory)
    LinearLayout llCategory;

    @BindView(R.id.llVerticalBanner)
    LinearLayout llVerticalBanner;

    @BindView(R.id.llMainContent)
    LinearLayout llMainContent;

    @BindView(R.id.llSixReason)
    LinearLayout llSixReason;

    @BindView(R.id.llRecentView)
    LinearLayout llRecentView;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.tvReasonNameOne)
    TextViewLight tvReasonNameOne;

    @BindView(R.id.tvReasonNameTwo)
    TextViewBold tvReasonNameTwo;

    @BindView(R.id.ablHome)
    AppBarLayout ablHome;

    @BindView(R.id.svHome)
    NestedScrollView svHome;

    @BindView(R.id.llBottomBar)
    LinearLayout llBottomBar;

    @BindView(R.id.ivNotification)
    ImageView ivNotification;

    @BindView(R.id.llMenus)
    LinearLayout llMenus;

    @BindView(R.id.llmenusOne)
    LinearLayout llmenusOne;

    @BindView(R.id.llmenusTwo)
    LinearLayout llmenusTwo;

    @BindView(R.id.tvRecentNameOne)
    TextViewLight tvRecentNameOne;

    @BindView(R.id.tvRecentNameTwo)
    TextViewBold tvRecentNameTwo;

    @BindView(R.id.crMain)
    CoordinatorLayout crMain;

    private ImageView[] dots;

    private int[] layouts;

    private int currentPosition;

    private BannerViewPagerAdapter bannerViewPagerAdapter;

    private HomeTopCategoryAdapter homeTopCategoryAdapter;

    private CategoryAdapter categoryAdapter;

    private VerticalBannerAdapter verticalBannerAdapter;

    private SixReasonAdapter sixReasonAdapter;

    private RecentViewAdapter recentViewAdapter;

    TopRatedProductAdapter topRatedProductAdapter;

    SelectProductAdapter selectProductAdapter;

    DynamicItemAdapter mAdapter;

    SelectedItemAdapter selectedItemAdapter;

    RecentlyAddedAdapter recentlyAddedAdapter;

    private View listHeaderView;

    private TextViewRegular tvName;

    private boolean ishead = false;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private NavigationDrawerAdapter navigationDrawerAdapter;

    private DatabaseHelper databaseHelper;

    private boolean isAutoScroll = false, isSpecialDeal = false;

    private long mBackPressed;

    private Handler handler;

    Home homeRider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isInfiniteScrollEnable()) {
            finish();
            Intent intent = new Intent(this, InfiniteScrollActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_home);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            ButterKnife.bind(this);
            Config.IS_RTL = getPreferences().getBoolean(Constant.RTL, false);
            setHomecolorTheme(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR));
            setScreenLayoutDirection();
            settvImage();
            ivDrawer.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
            // Get token and Save Notification Token
            String token = FirebaseInstanceId.getInstance().getToken();
            SharedPreferences.Editor pre = getPreferences().edit();
            pre.putString(RequestParamUtils.NOTIFICATION_TOKEN, token);
            pre.commit();
            setBottomBar("home", svHome);
            getHomeData();
            initDrawer();
            swipeView();
            showCart();
            setHomeCategoryData();
            setView();
            categoryData();
            setToolbarTheme();
//            verticalBannerData();
            setSixReasonAdapter();
            setRecentViewAdapter();
//            getRecentData();
        }
        Log.e("TAG", "dhruv: " + getPreferences().getString(RequestParamUtils.user_role, ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // TODO: Remove this code after the UrlTable2 has been checked in.
    public void getRecentData() {
        databaseHelper = new DatabaseHelper(HomeActivity.this);
        List<CategoryList> recentList = databaseHelper.getRecentViewList();
        recentViewAdapter.addAll(recentList);
        if (recentList.size() > 0) {
            llRecentView.setVisibility(View.VISIBLE);
        } else {
            llRecentView.setVisibility(View.GONE);
        }
    }

    //TODO: API call to get home data from Backend
    public void getHomeData() {
        if (Utils.isInternetConnected(this)) {

            showProgress("");
            try {
                PostApi postApi = new PostApi(this, RequestParamUtils.getHomeData, this, getlanuage());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.appVersion, URLS.version);
                jsonObject.put(RequestParamUtils.appkey, URLS.PURCHASE_KEY);
                //customization
                /*jsonObject.put("user_id", getPreferences().getString(RequestParamUtils.ID, "-"));*/
                jsonObject.put(RequestParamUtils.USER_ID, "2");
//                Log.e("?lang=fr: ", getPreferences().getString(RequestParamUtils.DefaultLanguage, "") );

                postApi.callPostApi(new URLS().HOME + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Home", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    //TODO:Refresh Data from backend
    public void swipeView() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                llTopCategory.setVisibility(View.GONE);
                getHomeData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.orange,
                R.color.red,
                R.color.blue
        );
    }

    //TODO: set Drawer Data
    public void initDrawer() {
        if (Config.IS_RTL) {
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT;
            drawerListView.setLayoutParams(params);
        } else {
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            drawerListView.setLayoutParams(params);
        }


        LayoutInflater inflater = getLayoutInflater();
        listHeaderView = inflater.inflate(R.layout.nav_header, null, false);
        tvName = (TextViewRegular) listHeaderView.findViewById(R.id.tvName);
        if (!ishead) {
            drawerListView.addHeaderView(listHeaderView);
            ishead = true;
        }
        navigationDrawerAdapter = new NavigationDrawerAdapter(this);
        drawerListView.setAdapter(navigationDrawerAdapter);
        ivDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Config.IS_RTL) {
                    drawer_layout.openDrawer(drawerListView);
                } else {
                    drawer_layout.openDrawer(drawerListView);
                }
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawer_layout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                selectItemFragment(position - 1);
            }
        });
        setBottomBar("home", svHome);
    }

    private void selectItemFragment(int position) {

        if (position == -1) {

        } else {
            if (position < navigationDrawerAdapter.getSeprater()) {
                Intent intent = new Intent(this, CategoryListActivity.class);
                intent.putExtra(RequestParamUtils.CATEGORY, navigationDrawerAdapter.getList().get(position).mainCatId);
                intent.putExtra(RequestParamUtils.IS_WISHLIST_ACTIVE, Constant.IS_WISH_LIST_ACTIVE);
                startActivity(intent);
            } else if (position == navigationDrawerAdapter.getSeprater()) {
                Intent intent = new Intent(this, SearchCategoryListActivity.class);
                startActivity(intent);
            } else {
                selectlocalFragment(navigationDrawerAdapter.getList().get(position).mainCatName);
            }
        }
        drawerListView.setItemChecked(position, true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer_layout.closeDrawer(drawerListView);
            }
        }, 200);
    }

    public void selectlocalFragment(String name) {

        if (name.equals(getResources().getString(R.string.notification))) {
            Intent notificationIntent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(notificationIntent);
        } else if (name.equals(getResources().getString(R.string.my_reward))) {
            Intent rewardIntent = new Intent(HomeActivity.this, RewardsActivity.class);
            startActivity(rewardIntent);
        } else if (name.equals(getResources().getString(R.string.my_cart))) {
            Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);
        } else if (name.equals(getResources().getString(R.string.my_wish_list))) {
            Intent wishListIntent = new Intent(HomeActivity.this, WishListActivity.class);
            startActivity(wishListIntent);
        } else if (name.equals(getResources().getString(R.string.my_account))) {
            Intent accountIntent = new Intent(HomeActivity.this, AccountActivity.class);
            startActivity(accountIntent);
        } else if (name.equals(getResources().getString(R.string.my_orders))) {
            Intent myOrderIntent = new Intent(HomeActivity.this, MyOrderActivity.class);
            startActivity(myOrderIntent);
        }
    }

    //TODO used to set home category data
    public void setHomeCategoryData() {
        homeTopCategoryAdapter = new HomeTopCategoryAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTopCategory.setLayoutManager(mLayoutManager);
        rvTopCategory.setAdapter(homeTopCategoryAdapter);
        rvTopCategory.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rvTopCategory, false);
        rvTopCategory.setHasFixedSize(true);
        rvTopCategory.setItemViewCacheSize(20);
        rvTopCategory.setDrawingCacheEnabled(true);
        rvTopCategory.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }


    //TODO used to set category data
    public void categoryData() {
        categoryAdapter = new CategoryAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCategory.setLayoutManager(mLayoutManager);
        rvCategory.setAdapter(categoryAdapter);
        rvCategory.setNestedScrollingEnabled(false);
        if (!Config.IS_RTL) {
            rvCategory.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize

        }
        rvCategory.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    public void verticalBannerData() {
        verticalBannerAdapter = new VerticalBannerAdapter(HomeActivity.this, this::onItemClick);
        rvVerticalBanner.setAdapter(verticalBannerAdapter);
        rvVerticalBanner.setAnimationEnabled(true);
        rvVerticalBanner.setFadeEnabled(true);
        rvVerticalBanner.setFadeFactor(0.6f);
//        rvVerticalBanner.setCurrentItem(1);
    }

    public void setSixReasonAdapter() {
        sixReasonAdapter = new SixReasonAdapter(this, this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rvSixReason.setLayoutManager(mLayoutManager);
        rvSixReason.setAdapter(sixReasonAdapter);
        rvSixReason.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rvSixReason, false);
        rvSixReason.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rvSixReason.setHasFixedSize(true);
        rvSixReason.setItemViewCacheSize(20);
    }

    public void setRecentViewAdapter() {
        tvRecentNameOne.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvRecentNameTwo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        recentViewAdapter = new RecentViewAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRecentOffer.setLayoutManager(mLayoutManager);
        rvRecentOffer.setAdapter(recentViewAdapter);
        rvRecentOffer.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rvRecentOffer, false);
        rvRecentOffer.setHasFixedSize(true);
        rvRecentOffer.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        rvRecentOffer.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize

    }

    private void setView() {
        bannerViewPagerAdapter = new BannerViewPagerAdapter(this);
        vpBanner.setAdapter(bannerViewPagerAdapter);
        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position, vpBanner.getAdapter().getCount());
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void autoScroll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPosition == bannerViewPagerAdapter.getCount() - 1) {
                            currentPosition = 0;
                        } else {
                            currentPosition = currentPosition + 1;
                        }
                        vpBanner.setCurrentItem(currentPosition);
                        addBottomDots(currentPosition, bannerViewPagerAdapter.getCount());
                        autoScroll();
                    }
                }, 6000);
            }
        }, 1000);
    }

    private void addBottomDots(int currentPage, int lenght) {
        layoutDots.removeAllViews();
        dots = new ImageView[lenght];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.ic_dash));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 15, 0);
            dots[i].setLayoutParams(lp);
            layoutDots.addView(dots[i]);
        }
        if (dots.length > 0 && dots.length >= currentPage) {
            dots[currentPage].setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        }
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {
    }

    @OnClick(R.id.ivSearch)
    public void etSearchClick() {
        Intent intent = new Intent(HomeActivity.this, SearchFromHomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResponse(final String response, String methodName) {
        if (methodName.equals(RequestParamUtils.getHomeData)) {
            if (response != null && response.length() > 0) {
                swipeContainer.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Convert json response into gson and made model class
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    homeRider = gson.fromJson(
                            jsonObject.toString(), new TypeToken<Home>() {
                            }.getType());
                    if (jsonObject.has("pgs_woo_api_add_to_cart_option") && jsonObject.getString("pgs_woo_api_add_to_cart_option").equals("enable")) {
                        Constant.IS_ADD_TO_CART_ACTIVE = true;
                    } else {
                        Constant.IS_ADD_TO_CART_ACTIVE = false;
                    }

                    if (jsonObject.has("pgs_woo_api_catalog_mode_option") && jsonObject.getString("pgs_woo_api_catalog_mode_option").equals("enable")) {
                        Config.IS_CATALOG_MODE_OPTION = true;
                        showCart();
                    } else {
                        Config.IS_CATALOG_MODE_OPTION = false;
                    }

                    if (homeRider.webViewPages != null && !homeRider.webViewPages.isEmpty()) {
                        Constant.WEBVIEWPAGES = new ArrayList<>();
                        Constant.WEBVIEWPAGES.addAll(homeRider.webViewPages);
                    }
                    if (Config.IS_CATALOG_MODE_OPTION) {
                        llCart.setVisibility(View.GONE);
                    } else {
                        llCart.setVisibility(View.VISIBLE);
                    }


                    checkReview(jsonObject);

//                    runOnUiThread(new Runnable() {
//                        @SuppressLint("NewApi")
//                        @Override
//                        public void run() {

                    llMain.setVisibility(View.VISIBLE);
                    llBottomBar.setVisibility(View.VISIBLE);


                    if (homeRider != null) {
                        if (homeRider.isAppValidation != null) {
                            new Ciyashop(HomeActivity.this).setFlag(homeRider.isAppValidation, false);
                        }

                        //set all constant value from the respones
                        setConstantValue();


                        //set  all color from the response into preferences
                        setColorPreferences(homeRider.appColor.primaryColor, homeRider.appColor.secondaryColor, homeRider.appColor.headerColor);


                        //set color into toolbar of home activity
                        setHomecolorTheme(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR));


                        //set theme color from the reponse
                        setThemeIconColor();


                        //set lungage into local
                        setLocale(homeRider.siteLanguage);
                        setText();

                        //CheckOut URLs get
                        if (homeRider.checkoutRedirectUrl != null && homeRider.checkoutRedirectUrl.size() > 0) {
                            setCheckoutURL(homeRider.checkoutRedirectUrl);
                        }

                             /*   if (homeRider.checkoutCancelUrl != null && homeRider.checkoutCancelUrl.size() > 0) {
                                    setCancelUrl(homeRider.checkoutCancelUrl);
                                }*/

                        //set crousol product
                        if (homeRider.productsCarousel != null) {
                            AddNewCorosol();
                        } else {
                            llMenus.removeAllViews();
                            if (homeRider.popularProducts != null) {
                                AddPopularProducts();
                            }

                            if (homeRider.scheduledSaleProducts != null) {
                                AddSpecialDealProducts();
                            }
                        }


//                        set lungage list  from the response
                        if (homeRider.isWpmlActive != null && homeRider.isWpmlActive) {
                            if (homeRider.wpmlLanguages != null) {
                                Constant.LANGUAGELIST = (homeRider.wpmlLanguages);
                            }
                        } else {
                            SharedPreferences.Editor pre = getPreferences().edit();
                            pre.putString(RequestParamUtils.LANGUAGE, "");
                            pre.commit();
                        }

                        //set currency list from resposne
                        setCurrency(response);
                    }

                    for (int i = 0; i < homeRider.allCategories.size(); i++) {
                        if (homeRider.allCategories.get(i).name.equals("Uncategorized")) {
                            homeRider.allCategories.remove(i);
                        }
                    }

                    Constant.MAINCATEGORYLIST.clear();
                    Constant.MAINCATEGORYLIST.addAll(homeRider.allCategories);

                    //set main category list from response
                    setMainCategoryList(homeRider.mainCategory);

                    //set banner slider from response
                    setSliderList(homeRider.mainSlider);

                    //set top banner
                    setCategoryList(homeRider.categoryBanners);

                    //set vertical banner
                    setVerticalBannerList(homeRider.bannerAd);

                    //set feature box or six reason from response
                    if (homeRider.featureBoxStatus != null && homeRider.featureBoxStatus.equals("enable")) {
                        setSixReasonrList(homeRider.featureBox, homeRider.featureBoxHeading);
                    } else {
                        llSixReason.setVisibility(View.GONE);
                    }

                    SharedPreferences.Editor editor = getPreferences().edit();
                    editor.putString(Constant.APPLOGO, homeRider.appLogo);
                    editor.putString(Constant.APPLOGO_LIGHT, homeRider.appLogoLight);
                    editor.commit();
                    settvImage();
                    if (homeRider.notificationIcon != null && homeRider.notificationIcon.contains("https:")) {
                        Picasso.get().load(homeRider.notificationIcon).into(ivNotification);
                    } else {
                        Picasso.get().load("https:" + homeRider.notificationIcon).into(ivNotification);
                    }

                    ivNotification.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));


                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (homeRider.product_banners_cat_value != null && homeRider.product_banners_cat_value.equals("enable")) {
                                AddCustomSection();
                            }
                        }
                    });

                    if (homeRider.isVerified.equals("no")) {
                        getPreferences().edit().putBoolean(RequestParamUtils.VERIFIED, false).commit();
                    } else {
                        getPreferences().edit().putBoolean(RequestParamUtils.VERIFIED, true).commit();
                    }

                    dismissProgress();
                } catch (Exception e) {
                    // dismissProgress();
                    dismissProgress();

                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            }


            getRecentData();

            dismissProgress();
        }
    }

    public void setText() {
        setBottomBar("home", svHome);
    }

/*    private void setCancelUrl(List<String> checkoutCancelUrl) {
        Constant.CancelUrl.clear();
        Constant.CancelUrl = new ArrayList<>();
        Constant.CancelUrl.addAll(checkoutCancelUrl);
    }*/

    private void setCheckoutURL(List<String> checkoutRedirectUrl) {
        Constant.CheckoutURL.clear();
        Constant.CheckoutURL = new ArrayList<>();
        Constant.CheckoutURL.addAll(checkoutRedirectUrl);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setThemeIconColor() {
        crMain.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        tvRecentNameOne.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvRecentNameTwo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    public void setConstantValue() {

        if (homeRider.pgsAppContactInfo != null) {
            if (homeRider.pgsAppContactInfo.addressLine1 != null) {
                Constant.ADDRESS_LINE1 = homeRider.pgsAppContactInfo.addressLine1;
            }
            if (homeRider.pgsAppContactInfo.addressLine2 != null) {
                Constant.ADDRESS_LINE2 = homeRider.pgsAppContactInfo.addressLine2;
            }
            if (homeRider.pgsAppContactInfo.email != null) {
                Constant.EMAIL = homeRider.pgsAppContactInfo.email;
            }
            if (homeRider.pgsAppContactInfo.phone != null) {
                Constant.PHONE = homeRider.pgsAppContactInfo.phone;

            }
            if (homeRider.pgsAppContactInfo.whatsappNo != null) {
                Constant.WHATSAPP = homeRider.pgsAppContactInfo.whatsappNo;
            }
            if (homeRider.pgsAppContactInfo.whatsappFloatingButton != null) {
                Constant.WHATSAPPENABLE = homeRider.pgsAppContactInfo.whatsappFloatingButton;
                //Constant.WHATSAPPENABLE = "disable";
            }
            if (homeRider.priceFormateOptions.currencyCode != null) {
                Constant.CURRENCYCODE = Html.fromHtml(homeRider.priceFormateOptions.currencyCode).toString();
            }
        }
        if (homeRider.isCurrencySwitcherActive != null) {
            Constant.IS_CURRENCY_SWITCHER_ACTIVE = homeRider.isCurrencySwitcherActive;
        }
        if (homeRider.isGuestCheckoutActive != null) {
            Constant.IS_GUEST_CHECKOUT_ACTIVE = homeRider.isGuestCheckoutActive;
        }
        if (homeRider.isWpmlActive != null) {
            Constant.IS_WPML_ACTIVE = homeRider.isWpmlActive;
        }
        if (homeRider.isOrderTrackingActive != null) {
            Constant.IS_ORDER_TRACKING_ACTIVE = homeRider.isOrderTrackingActive;
        }

        if (homeRider.isRewardPointsActive != null) {
            Constant.IS_REWARD_POINT_ACTIVE = homeRider.isRewardPointsActive;
        }

        if (homeRider.isWishlistActive != null) {
            Constant.IS_WISH_LIST_ACTIVE = homeRider.isWishlistActive;
        }
        if (homeRider.isYithFeaturedVideoActive != null) {
            Constant.IS_YITH_FEATURED_VIDEO_ACTIVE = homeRider.isYithFeaturedVideoActive;
        }

        if (homeRider.pgsAppSocialLinks != null) {
            Constant.SOCIALLINK = homeRider.pgsAppSocialLinks;
        }
        if (homeRider.priceFormateOptions != null) {
            if (homeRider.priceFormateOptions.currencyPos != null) {
                Constant.CURRENCYSYMBOLPOSTION = homeRider.priceFormateOptions.currencyPos;
            }
            if (homeRider.priceFormateOptions.currencySymbol != null) {
                Constant.CURRENCYSYMBOL = Html.fromHtml(homeRider.priceFormateOptions.currencySymbol).toString();
                SharedPreferences.Editor pre = getPreferences().edit();
                pre.putString(Constant.CURRENCYSYMBOLPref, Html.fromHtml(homeRider.priceFormateOptions.currencySymbol).toString());
                pre.commit();
            }
            if (homeRider.priceFormateOptions.decimals != null) {
                Constant.Decimal = homeRider.priceFormateOptions.decimals;
            }
            if (homeRider.priceFormateOptions.decimalSeparator != null) {
                Constant.DECIMALSEPRETER = homeRider.priceFormateOptions.decimalSeparator;
            }
            if (homeRider.priceFormateOptions.thousandSeparator != null) {
                Constant.THOUSANDSSEPRETER = homeRider.priceFormateOptions.thousandSeparator;
            }
        }
        if (homeRider.appLogo != null) {
            Constant.APPLOGO = homeRider.appLogo;
        }
        if (homeRider.appLogoLight != null) {
            Constant.APPLOGO_LIGHT = homeRider.appLogoLight;
        }

        if (getPreferences().getString(RequestParamUtils.LANGUAGE, "").equals("")) {
            if (homeRider.isRtl != null) {
                Config.IS_RTL = homeRider.isRtl;
                getPreferences().edit().putBoolean(Constant.RTL, Config.IS_RTL).commit();
            }
        } else {
            Config.IS_RTL = getPreferences().getBoolean(Constant.RTL, false);
        }

        if (homeRider.pgsWooApiDeliverPincode != null) {
            if (homeRider.pgsWooApiDeliverPincode.status != null && homeRider.pgsWooApiDeliverPincode.status.equals("enable")) {
                Config.WOO_API_DELIVER_PINCODE = true;
            } else {
                Config.WOO_API_DELIVER_PINCODE = false;
            }
            if (homeRider.pgsWooApiDeliverPincode.settingOptions == null) {
                Home.SettingOptions settingOptions = new Home().getSettingOption();
                settingOptions.availableatText = getString(R.string.available_text);
                settingOptions.codAvailableMsg = getString(R.string.cod_available_msg);
                settingOptions.codDataLabel = getString(R.string.cod_data_label);
                settingOptions.codHelpText = getString(R.string.cod_help_text);
                settingOptions.codNotAvailableMsg = getString(R.string.cod_not_available_msg);
                settingOptions.delDataLabel = getString(R.string.del_data_label);
                settingOptions.delHelpText = getString(R.string.del_help_text);
                settingOptions.delSaturday = getString(R.string.del_saturday);
                settingOptions.delSunday = getString(R.string.del_sunday);
                settingOptions.errorMsgBlank = getString(R.string.error_msg_blank);
                settingOptions.errorMsgCheckPincode = getString(R.string.error_msg_check_pincode);
                settingOptions.pincodePlaceholderTxt = getString(R.string.pincode_placeholder_txt);
                Constant.settingOptions = settingOptions;
            } else {
                Constant.settingOptions = homeRider.pgsWooApiDeliverPincode.settingOptions;
            }
        }
    }

    public void setColorPreferences(String primaryColor, String secondaryColor, String HeaderColor) {

        String colorSubString = (secondaryColor.substring(primaryColor.lastIndexOf("#") + 1));
        SharedPreferences.Editor editor = getPreferences().edit();

        if (!primaryColor.equals("")) {
            editor.putString(Constant.APP_COLOR, primaryColor);
            editor.putString(Constant.APP_TRANSPARENT, "#80" + colorSubString);
            editor.putString(Constant.APP_TRANSPARENT_VERY_LIGHT, "#44" + colorSubString);
        } else {
            editor.putString(Constant.APP_COLOR, Constant.PRIMARY_COLOR);
        }
        if (!secondaryColor.equals("")) {
            editor.putString(Constant.SECOND_COLOR, secondaryColor);
        } else {
            editor.putString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR);
        }

        if (HeaderColor != null && !HeaderColor.equals("")) {
            editor.putString(Constant.HEADER_COLOR, HeaderColor);
        } else {
            editor.putString(Constant.HEADER_COLOR, Constant.PRIMARY_COLOR);
        }
        editor.commit();
        showCart();
    }

    public void setMainCategoryList(List<Home.MainCategory> list) {
        if (list != null) {
            List<Home.MainCategory> mainCategoryList = new ArrayList<>();
            if (list.size() > 0) {
                if (list.size() > 4) {
                    for (int i = 0; i <= 4; i++) {
                        mainCategoryList.add(list.get(i));
                    }
                } else {
                    mainCategoryList.addAll(list);
                }
                Home.MainCategory mainCategory = new Home().getInstranceMainCategory();
                mainCategory.mainCatName = getString(R.string.more);
                mainCategoryList.add(mainCategory);
                homeTopCategoryAdapter.addAll(mainCategoryList);
                llTopCategory.setVisibility(View.VISIBLE);
            } else {
                llTopCategory.setVisibility(View.GONE);
            }
            navigationDrawerAdapter.setSepreter(mainCategoryList.size() - 1);
            List<Home.MainCategory> drawerList = new ArrayList<Home.MainCategory>();
            drawerList.addAll(mainCategoryList);
            for (int i = 0; i < NavigationList.getInstance(this).getImageList().size(); i++) {
                Home.MainCategory mainCategory = new Home().getInstranceMainCategory();
                mainCategory.mainCatName = NavigationList.getInstance(this).getTitleList().get(i);
                mainCategory.mainCatImage = NavigationList.getInstance(this).getImageList().get(i) + "";
                mainCategory.mainCatId = i + "";
                drawerList.add(mainCategory);
            }
            navigationDrawerAdapter.addAll(drawerList);
        } else {
            llTopCategory.setVisibility(View.GONE);
            navigationDrawerAdapter.setSepreter(0);
            List<Home.MainCategory> drawerList = new ArrayList<Home.MainCategory>();
            for (int i = 0; i < NavigationList.getInstance(this).getImageList().size(); i++) {
                Home.MainCategory mainCategory = new Home().getInstranceMainCategory();
                mainCategory.mainCatName = NavigationList.getInstance(this).getTitleList().get(i);
                mainCategory.mainCatImage = NavigationList.getInstance(this).getImageList().get(i) + "";
                mainCategory.mainCatId = i + "";
                drawerList.add(mainCategory);
            }
            navigationDrawerAdapter.addAll(drawerList);
        }
    }

    public void setSliderList(List<Home.MainSlider> list) {
        if (list != null) {
            if (list.size() > 0) {
                bannerViewPagerAdapter.addAll(list);
                if (!isAutoScroll) {
                    addBottomDots(0, vpBanner.getAdapter().getCount());
                    autoScroll();
                    isAutoScroll = true;
                }
                llBanner.setVisibility(View.VISIBLE);
            } else {
                llBanner.setVisibility(View.GONE);
            }
        } else {
            llBanner.setVisibility(View.GONE);
        }
    }

    public void setCategoryList(List<Home.CategoryBanner> list) {
        if (list != null) {
            if (list.size() > 0) {
                categoryAdapter.addAll(list);
                llCategory.setVisibility(View.VISIBLE);
            } else {
                llCategory.setVisibility(View.GONE);
            }
        } else {
            llCategory.setVisibility(View.GONE);
        }
    }

    public void setVerticalBannerList(List<Home.BannerAd> list) {
        if (list != null) {
            if (list.size() > 0) {
                verticalBannerData();
                verticalBannerAdapter.addAll(list);
                rvVerticalBanner.setCurrentItem(1);
                llVerticalBanner.setVisibility(View.VISIBLE);
            } else {
                llVerticalBanner.setVisibility(View.GONE);
            }
        } else {
            llVerticalBanner.setVisibility(View.GONE);
        }
    }

    public void setSixReasonrList(List<Home.FeatureBox> list, String title) {
        if (list != null) {
            if (list.size() > 0) {
                sixReasonAdapter.addAll(list);

                if (list.size() == 1) {
                    if (list.get(0).featureContent.equals("")) {
                        llSixReason.setVisibility(View.GONE);
                    } else {
                        llSixReason.setVisibility(View.VISIBLE);
                    }
                } else {
                    llSixReason.setVisibility(View.VISIBLE);
                }
            } else {
                llSixReason.setVisibility(View.GONE);
            }
//            tvSixResonTitle.setText(title);
        } else {
            llSixReason.setVisibility(View.GONE);
        }


        if (title.contains(" ")) {
            String[] array = title.split(" ");
            if (array.length > 0) {
                tvReasonNameOne.setText(array[0].toUpperCase());
                String secondName = array[1];
                for (int i = 2; i < array.length; i++) {

                    secondName = secondName + " " + array[i];

                }

                tvReasonNameTwo.setText(secondName.toUpperCase());
            } else {
                tvReasonNameOne.setText(title);
            }
        } else {
            tvReasonNameOne.setText(title);
        }
        tvReasonNameOne.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvReasonNameTwo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));


    }

    private void setTimer(final TextView tvHour, TextView tvminute, TextView tvSecond, final LinearLayout llSpecialOffer) {
        if (handler != null) {
            handler.removeCallbacks(null);

        } else {
            handler = new Handler();
        }

        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                handler.postDelayed(this, delay);
                String timer = tvHour.getText().toString() + ":" + tvminute.getText().toString() + ":" + tvSecond.getText().toString();
                long time = convertInMilisecond(timer) - 1000;
                if (time == 0) {
                    llSpecialOffer.setVisibility(View.GONE);
                } else {
                    tvHour.setText(TimeUnit.MILLISECONDS.toHours(time) + "");
                    tvminute.setText((TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time))) + "");
                    tvSecond.setText((TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))) + "");

                }
            }
        }, delay);
    }

    private long convertInMilisecond(String time) {

        String[] tokens = time.split(":");
        int secondsToMs = Integer.parseInt(tokens[2]) * 1000;
        int minutesToMs = Integer.parseInt(tokens[1]) * 60000;
        int hoursToMs = Integer.parseInt(tokens[0]) * 3600000;
        long total = secondsToMs + minutesToMs + hoursToMs;
        return total;
    }

    private String convertInTimeFormet(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getRecentData();
        showCart();
        if (Constant.IS_CURRENCY_SET) {
            getHomeData();
            databaseHelper.clearRecentItem();
            databaseHelper.clearCart();
            Constant.IS_CURRENCY_SET = false;
        }
        if (recentViewAdapter != null) {
            recentViewAdapter.notifyDataSetChanged();
        }
        if (topRatedProductAdapter != null) {
            topRatedProductAdapter.notifyDataSetChanged();
        }
        if (selectedItemAdapter != null) {
            selectedItemAdapter.notifyDataSetChanged();
        }
        if (selectProductAdapter != null) {
            selectProductAdapter.notifyDataSetChanged();
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        if (recentlyAddedAdapter != null) {
            recentlyAddedAdapter.notifyDataSetChanged();
        }
    }

    public void AddNewCorosol() {

        llMenus.removeAllViews();
        if (homeRider.productsViewOrders != null && homeRider.productsViewOrders.size() > 0) {
            for (int i = 0; i < homeRider.productsViewOrders.size(); i++) {

                if (homeRider.productsViewOrders.get(i).name.equals(RequestParamUtils.recentProducts)) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AddRecentProducts();
                        }
                    });
                }

                if (homeRider.productsViewOrders.get(i).name.equals(RequestParamUtils.specialDealProducts)) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AddSpecialDealProducts();
                        }
                    });
                }
                if (homeRider.productsViewOrders.get(i).name.equals(RequestParamUtils.featureProducts)) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AddFeatureProducts();
                        }
                    });
                }
                if (homeRider.productsViewOrders.get(i).name.equals(RequestParamUtils.popularProducts)) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AddPopularProducts();
                        }
                    });
                }
                if (homeRider.productsViewOrders.get(i).name.equals(RequestParamUtils.TOPRATEDPRODUCT)) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AddTopRatedProducts();
                        }
                    });
                }
            }
        }
    }

    public void AddRecentProducts() {

        llmenusOne.removeAllViews();

        if (homeRider.productsCarousel.recentProducts != null && homeRider.productsCarousel.recentProducts.status.equals("enable") && homeRider.productsCarousel.recentProducts.products.size() > 0) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dynamic_mostpopuler, null);

            TextView tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);

            RecyclerView rvProducts = (RecyclerView) view.findViewById(R.id.rvProducts);
            recentlyAddedAdapter = new RecentlyAddedAdapter(HomeActivity.this);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

            rvProducts.setLayoutManager(mLayoutManager);
            rvProducts.setNestedScrollingEnabled(false);
            rvProducts.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize
            rvProducts.setAdapter(recentlyAddedAdapter);
            recentlyAddedAdapter.addAll(homeRider.productsCarousel.recentProducts.products);
            String product_name = getResources().getString(R.string.recently_view_product);
            if (!homeRider.productsCarousel.recentProducts.title.isEmpty()) {
                product_name = homeRider.productsCarousel.recentProducts.title;
            }

            if (!Config.IS_RTL) {
                rvProducts.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize

            }
            setViewAllText(product_name, view);

            tvViewAll.setText(getResources().getString(R.string.view_all));

            tvViewAll.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

            tvViewAll.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                    intent.putExtra(RequestParamUtils.POSITION, 0);
                    startActivity(intent);
                }
            });
            llmenusOne.addView(view);
        }
    }

    public void AddFeatureProducts() {
        llmenusTwo.removeAllViews();
        if (homeRider.productsCarousel.featureProducts != null && homeRider.productsCarousel.featureProducts.status.equals("enable") && homeRider.productsCarousel.featureProducts.products.size() > 0) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dynamic_mostpopuler, null);
            TextView tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);
            RecyclerView rvProducts = (RecyclerView) view.findViewById(R.id.rvProducts);
            selectProductAdapter = new SelectProductAdapter(HomeActivity.this);
            selectProductAdapter.addAll(homeRider.productsCarousel.featureProducts.products);
            rvProducts.setHasFixedSize(true);
            rvProducts.setNestedScrollingEnabled(false);

            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvProducts.setLayoutManager(mLayoutManager);
            rvProducts.setAdapter(selectProductAdapter);
            rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));

            String product_name = getResources().getString(R.string.featureProducts);
            if (!homeRider.productsCarousel.featureProducts.title.isEmpty()) {
                product_name = homeRider.productsCarousel.featureProducts.title;
            }


            setViewAllText(product_name, view);

            tvViewAll.setText(getResources().getString(R.string.view_all));
            tvViewAll.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

            tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                    intent.putExtra(RequestParamUtils.FEATURE, true);
                    startActivity(intent);
                }
            });
            llmenusTwo.addView(view);
        }
    }

    public void AddTopRatedProducts() {

        if (homeRider.productsCarousel.topRatedProducts != null && homeRider.productsCarousel.topRatedProducts.status.equals("enable") && homeRider.productsCarousel.topRatedProducts.products.size() > 0) {

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dynamic_mostpopuler, null);
            TextView tvViewAll = view.findViewById(R.id.tvViewAll);
            RecyclerView rvProducts = view.findViewById(R.id.rvProducts);


            topRatedProductAdapter = new TopRatedProductAdapter(HomeActivity.this);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rvProducts.setLayoutManager(mLayoutManager);
            ViewCompat.setNestedScrollingEnabled(rvProducts, false);
            rvProducts.setNestedScrollingEnabled(false);
            rvProducts.setHasFixedSize(true);
            rvProducts.setRecycledViewPool(new RecyclerView.RecycledViewPool());
            rvProducts.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize
            rvProducts.setAdapter(topRatedProductAdapter);
            topRatedProductAdapter.addAll(homeRider.productsCarousel.topRatedProducts.products);


            String product_name = getResources().getString(R.string.featureProducts);
            if (!homeRider.productsCarousel.topRatedProducts.title.isEmpty()) {
                product_name = homeRider.productsCarousel.topRatedProducts.title;
            }


            setViewAllText(product_name, view);

            tvViewAll.setText(getResources().getString(R.string.view_all));
            tvViewAll.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                    intent.putExtra(RequestParamUtils.POSITION, 1);
                    intent.putExtra(RequestParamUtils.ORDER_BY, "rating");
                    startActivity(intent);
                }
            });
            llMenus.addView(view);
        }
    }

    private void AddPopularProducts() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dynamic_mostpopuler, null);
        TextView tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);
        RecyclerView rvProducts = (RecyclerView) view.findViewById(R.id.rvProducts);
        rvProducts.setHasFixedSize(true);
        rvProducts.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(mLayoutManager);
        tvViewAll.setText(getResources().getString(R.string.view_all));
        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        tvViewAll.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        if (homeRider.productsCarousel != null) {
            if (homeRider.productsCarousel.popularProducts != null &&
                    homeRider.productsCarousel.popularProducts.status != null &&
                    homeRider.productsCarousel.popularProducts.status.equals("enable") &&
                    homeRider.productsCarousel.popularProducts.products.size() > 0) {

                mAdapter = new DynamicItemAdapter(HomeActivity.this);
                rvProducts.setAdapter(mAdapter);
                mAdapter.addAll(homeRider.productsCarousel.popularProducts.products);

                String product_name = getResources().getString(R.string.most_popular_poroducts);
                if (!homeRider.productsCarousel.popularProducts.title.isEmpty()) {
                    product_name = homeRider.productsCarousel.popularProducts.title;
                }

                setViewAllText(product_name, view);

                tvViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                        intent.putExtra(RequestParamUtils.ORDER_BY, RequestParamUtils.popularity);
                        intent.putExtra(RequestParamUtils.POSITION, 2);
                        intent.putExtra(RequestParamUtils.IS_WISHLIST_ACTIVE, Constant.IS_WISH_LIST_ACTIVE);
                        startActivity(intent);
                    }
                });
                llMenus.addView(view);
            }

        } else {

            if (homeRider.popularProducts != null && homeRider.popularProducts.size() > 0) {
                mAdapter = new DynamicItemAdapter(HomeActivity.this);

                rvProducts.setAdapter(mAdapter);

                mAdapter.addAll(homeRider.popularProducts);
                String product_name = getResources().getString(R.string.featureProducts);
                setViewAllText(product_name, view);

                tvViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                        intent.putExtra(RequestParamUtils.ORDER_BY, RequestParamUtils.popularity);
                        intent.putExtra(RequestParamUtils.POSITION, 2);
                        intent.putExtra(RequestParamUtils.IS_WISHLIST_ACTIVE, Constant.IS_WISH_LIST_ACTIVE);
                        startActivity(intent);
                    }
                });
                llMenus.addView(view);
            }
        }
    }


    private void AddCustomSection() {

        if (homeRider.product_banners_cat_value != null && homeRider.product_banners_cat_value.equals("enable")) {
            if (homeRider.product_banners_title != null && homeRider.product_banners_title.length() > 0) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dynamic_mostpopuler, null);
                TextView tvViewAll = (TextView) view.findViewById(R.id.tvViewAll);
                RecyclerView rvProducts = (RecyclerView) view.findViewById(R.id.rvProducts);
                rvProducts.setHasFixedSize(true);
                rvProducts.setNestedScrollingEnabled(false);
                GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
                rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                rvProducts.setLayoutManager(mLayoutManager);
                tvViewAll.setVisibility(View.GONE);

                if (homeRider.custom_section != null && homeRider.custom_section.size() > 0) {
                    selectedItemAdapter = new SelectedItemAdapter(HomeActivity.this);
                    rvProducts.setAdapter(selectedItemAdapter);
                    selectedItemAdapter.addAll(homeRider.custom_section);
                    String product_name = getResources().getString(R.string.selected_poroducts);
                    if (!homeRider.product_banners_title.isEmpty()) {
                        product_name = homeRider.product_banners_title;
                    } else {
                        product_name = getResources().getString(R.string.selected_poroducts);
                    }
                    setViewAllText(product_name, view);
                }
                llMenus.addView(view);
            }
        }
    }

    private void setViewAllText(String product_name, View view) {
        TextView tvProductName_one = (TextView) view.findViewById(R.id.tvProductName_one);
        TextView tvProductName_two = (TextView) view.findViewById(R.id.tvProductName_two);
        if (product_name.contains(" ")) {
            String[] array = product_name.split(" ");
            if (array.length > 0) {

                String secondName = array[1];
                for (int i = 2; i < array.length; i++) {

                    secondName = secondName + " " + array[i];


                }

                tvProductName_one.setText(array[0].toUpperCase());
                tvProductName_two.setText(secondName.toUpperCase());
            } else {
                tvProductName_one.setText(product_name);
            }
        } else {
            tvProductName_one.setText(product_name);
        }
        tvProductName_one.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvProductName_two.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

    }

    private void AddSpecialDealProducts() {
        final SpecialOfferAdapter mAdapter;
        String timer = "";
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dynamic_spacialoffer, null);
        LinearLayout llSpecialOffer = (LinearLayout) view.findViewById(R.id.llSpecialOffer);
        ImageView ivTimer = (ImageView) view.findViewById(R.id.ivTimer);
        TextView tvSaleText = (TextView) view.findViewById(R.id.tvSaleText);
        TextView tvHour = (TextView) view.findViewById(R.id.tvHour);
        TextView tvMinute = (TextView) view.findViewById(R.id.tvMinute);
        TextView tvSecond = (TextView) view.findViewById(R.id.tvSecond);
        TextView colonOne = (TextView) view.findViewById(R.id.colonOne);
        TextView colonTwo = (TextView) view.findViewById(R.id.colonTwo);

        TextView tvViewAllSpecialDeal = (TextView) view.findViewById(R.id.tvViewAllSpecialDeal);
        tvViewAllSpecialDeal.setText(getResources().getString(R.string.view_all));
        tvSaleText.setText(getResources().getString(R.string.end_of_the_sale));
        Drawable unwrappedDrawable = tvHour.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        unwrappedDrawable = tvMinute.getBackground();
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        unwrappedDrawable = tvSecond.getBackground();
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        tvSaleText.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        colonOne.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        colonTwo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvViewAllSpecialDeal.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        RecyclerView rvSpecialOffer = (RecyclerView) view.findViewById(R.id.rvSpecialOffer);
        mAdapter = new SpecialOfferAdapter(HomeActivity.this, HomeActivity.this);
        rvSpecialOffer.setHasFixedSize(true);
        rvSpecialOffer.setNestedScrollingEnabled(false);
//        GridLayoutManager mLayoutManager = new GridLayoutManager(HomeActivity.this, 2, GridLayoutManager.HORIZONTAL, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvSpecialOffer.setLayoutManager(mLayoutManager);
        rvSpecialOffer.setAdapter(mAdapter);
        rvRecentOffer.addItemDecoration(new EqualSpacingItemDecoration(dpToPx(10), EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize

//        rvSpecialOffer.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));

        ivTimer.setImageResource(R.drawable.ic_watch);
        ivTimer.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        if (homeRider.productsCarousel != null) {

            if (homeRider.productsCarousel.specialDealProducts.products != null && homeRider.productsCarousel.specialDealProducts.status.equals("enable") && homeRider.productsCarousel.specialDealProducts.products.size() > 0) {
                mAdapter.addAll(homeRider.productsCarousel.specialDealProducts.products);
                if (homeRider.productsCarousel.specialDealProducts.title.length() > 0) {
                    setViewAllText(homeRider.productsCarousel.specialDealProducts.title, view);
                } else {
                    setViewAllText(getResources().getString(R.string.special_offer), view);
                }
                if (homeRider.productsCarousel.specialDealProducts.products.size() > 0) {
                    tvHour.setText(homeRider.productsCarousel.specialDealProducts.products.get(0).dealLife.hours);
                    tvMinute.setText(homeRider.productsCarousel.specialDealProducts.products.get(0).dealLife.minutes);
                    tvSecond.setText(homeRider.productsCarousel.specialDealProducts.products.get(0).dealLife.seconds);
                }
                setTimer(tvHour, tvMinute, tvSecond, llSpecialOffer);
                tvViewAllSpecialDeal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String productid = "";
                        for (int i = 0; i < mAdapter.getList().size(); i++) {
                            if (productid.equals("")) {
                                productid = mAdapter.getList().get(i).id;
                            } else {
                                productid = productid + "," + mAdapter.getList().get(i).id;
                            }
                        }
                        Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                        intent.putExtra(RequestParamUtils.DEAL_OF_DAY, productid);
                        startActivity(intent);
                    }

                });
                llMenus.addView(view);
            }
        } else {

            if (homeRider.scheduledSaleProducts.products != null && homeRider.scheduledSaleProducts.products.size() > 0) {
                mAdapter.addAll(homeRider.scheduledSaleProducts.products);
                setViewAllText(getResources().getString(R.string.special_offer), view);

                if (homeRider.scheduledSaleProducts.products.size() > 0) {
                    tvHour.setText(homeRider.scheduledSaleProducts.products.get(0).dealLife.hours);
                    tvMinute.setText(homeRider.scheduledSaleProducts.products.get(0).dealLife.minutes);
                    tvSecond.setText(homeRider.scheduledSaleProducts.products.get(0).dealLife.seconds);
                }

                setTimer(tvHour, tvMinute, tvSecond, llSpecialOffer);

                tvViewAllSpecialDeal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String productid = "";
                        for (int i = 0; i < mAdapter.getList().size(); i++) {
                            if (productid.equals("")) {
                                productid = mAdapter.getList().get(i).id;
                            } else {
                                productid = productid + "," + mAdapter.getList().get(i).id;
                            }
                        }
                        Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                        intent.putExtra(RequestParamUtils.DEAL_OF_DAY, productid);
                        startActivity(intent);
                    }
                });
                llMenus.addView(view);
            }
        }
    }

    public void setCurrency(String response) {
        if (Constant.IS_CURRENCY_SWITCHER_ACTIVE) {
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject currency_switcher = jsonObj.getJSONObject(RequestParamUtils.currencySwitcher);
                Constant.CurrencyList = new ArrayList<>();
                JSONArray namearray = currency_switcher.names();  //<<< get all keys in JSONArray
                for (int i = 0; i < namearray.length(); i++) {
                    JSONObject c = currency_switcher.getJSONObject(namearray.get(i).toString());
                    String name = c.getString(RequestParamUtils.name);
                    String symbol = c.getString(RequestParamUtils.symbol);
                    JSONObject obj = new JSONObject();
                    obj.put(RequestParamUtils.NAME, name);
                    obj.put(RequestParamUtils.SYMBOL, symbol);
                    // adding contact to contact list
                    Constant.CurrencyList.add(String.valueOf(obj));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Snackbar.make(llMain, getResources().getString(R.string.exitformapp), Snackbar.LENGTH_LONG).show();
        }
        mBackPressed = System.currentTimeMillis();
    }
}


