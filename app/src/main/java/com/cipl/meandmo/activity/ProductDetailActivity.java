package com.cipl.meandmo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.cipl.meandmo.R;
import com.cipl.meandmo.adapter.GroupProductAdapter;
import com.cipl.meandmo.adapter.ProductColorAdapter;
import com.cipl.meandmo.adapter.ProductImageViewPagerAdapter;
import com.cipl.meandmo.adapter.ProductVariationAdapter;
import com.cipl.meandmo.adapter.QuantityDiscountAdapter;
import com.cipl.meandmo.adapter.RelatedProductAdapter;
import com.cipl.meandmo.adapter.ReviewAdapter;
import com.cipl.meandmo.customview.EqualSpacingItemDecoration;
import com.cipl.meandmo.customview.MaterialRatingBar;
import com.cipl.meandmo.customview.edittext.EditTextRegular;
import com.cipl.meandmo.customview.like.animation.SparkButton;
import com.cipl.meandmo.customview.progressbar.RoundCornerProgressBar;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewMedium;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.javaclasses.CheckIsVariationAvailable;
import com.cipl.meandmo.model.Cart;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.ProductReview;
import com.cipl.meandmo.model.QuantityDiscount;
import com.cipl.meandmo.model.Variation;
import com.cipl.meandmo.model.WishList;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Config;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.CustomToast;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;
import com.ciyashop.library.apicall.GetApi;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductDetailActivity extends BaseActivity implements OnItemClickListner, OnResponseListner {

    private static final String TAG = ProductDetailActivity.class.getSimpleName();
    public static HashMap<Integer, String> combination = new HashMap<>();
    private static TextViewRegular tvPrice;
    private static TextViewRegular tvPrice1;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    AlertDialog alertDialog;
    List<CategoryList.Image> imageList = new ArrayList<>();
    List<CategoryList> categoryLists = new ArrayList<>();

    @BindView(R.id.vpProductImages)
    ViewPager vpProductImages;

    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;

    @BindView(R.id.ivWishList)
    SparkButton ivWishList;

    @BindView(R.id.ivShare)
    ImageView ivShare;

    @BindView(R.id.tvProductName)
    TextViewBold tvProductName;

    @BindView(R.id.ratingBar)
    MaterialRatingBar ratingBar;

    @BindView(R.id.tvDiscount)
    TextViewRegular tvDiscount;

    @BindView(R.id.tvAvailibilitytext)
    TextViewLight tvAvailibilitytext;

    @BindView(R.id.tvAvailibility)
    TextViewBold tvAvailibility;

    @BindView(R.id.tvReward)
    TextViewRegular tvReward;

    @BindView(R.id.llContent)
    LinearLayout llContent;

    @BindView(R.id.tvDeliveryOptions)
    TextViewMedium tvDeliveryOptions;

    @BindView(R.id.etPincode)
    EditTextRegular etPincode;

    @BindView(R.id.tvCheck)
    TextViewBold tvCheck;

    @BindView(R.id.tvDeliverable)
    TextViewRegular tvDeliverable;

    @BindView(R.id.llPincode)
    LinearLayout llPincode;

    @BindView(R.id.rvVariation)
    RecyclerView rvVariation;

    @BindView(R.id.llProductVariation)
    LinearLayout llProductVariation;

    @BindView(R.id.tv_Info)
    TextViewMedium tvInfo;

    @BindView(R.id.wv_info)
    WebView wv_info;

    @BindView(R.id.llInfo)
    LinearLayout llInfo;

    @BindView(R.id.tvSellerName)
    TextViewMedium tvSellerName;

    @BindView(R.id.llSoldBy)
    LinearLayout llSoldBy;

    @BindView(R.id.tvSellerContent)
    HtmlTextView tvSellerContent;

    @BindView(R.id.tvSellerMore)
    TextViewLight tvSellerMore;

    @BindView(R.id.ivMoreSeller)
    ImageView ivMoreSeller;

    @BindView(R.id.tvContactSeller)
    TextViewRegular tvContactSeller;

    @BindView(R.id.tvViewStore)
    TextViewRegular tvViewStore;

    @BindView(R.id.llIsSeller)
    LinearLayout llIsSeller;

    @BindView(R.id.rvGroupProduct)
    RecyclerView rvGroupProduct;

    @BindView(R.id.tvQuickOverViewTitle)
    TextViewMedium tvQuickOverViewTitle;

    @BindView(R.id.tvDescription)
    TextViewLight tvDescription;

    @BindView(R.id.tvMoreQuickOverview)
    TextViewMedium tvMoreQuickOverview;

    @BindView(R.id.ivQuickOverViewMore)
    ImageView ivQuickOverViewMore;

    @BindView(R.id.llQuickOverView)
    LinearLayout llQuickOverView;

    @BindView(R.id.tvProductDetailTitle)
    TextViewMedium tvProductDetailTitle;

    @BindView(R.id.tvProductDescription)
    HtmlTextView tvProductDescription;

    @BindView(R.id.tvMoreDetail)
    TextViewLight tvMoreDetail;

    @BindView(R.id.ivDetailMore)
    ImageView ivDetailMore;

    @BindView(R.id.llProductDescription)
    LinearLayout llProductDescription;

    @BindView(R.id.tvRatingTitle)
    TextViewMedium tvRatingTitle;

    @BindView(R.id.tvAverageRatting)
    TextViewBold tvAverageRatting;

    @BindView(R.id.tvFive)
    TextViewMedium tvFive;

    @BindView(R.id.rattingFive)
    RoundCornerProgressBar rattingFive;

    @BindView(R.id.tvFour)
    TextViewMedium tvFour;

    @BindView(R.id.rattingFour)
    RoundCornerProgressBar rattingFour;

    @BindView(R.id.tvThree)
    TextViewMedium tvThree;

    @BindView(R.id.rattingThree)
    RoundCornerProgressBar rattingThree;

    @BindView(R.id.tvTwo)
    TextViewMedium tvTwo;

    @BindView(R.id.rattingTwo)
    RoundCornerProgressBar rattingTwo;

    @BindView(R.id.tvOne)
    TextViewMedium tvOne;

    @BindView(R.id.rattingOne)
    RoundCornerProgressBar rattingOne;

    @BindView(R.id.tvRateReview)
    TextViewRegular tvRateReview;

    @BindView(R.id.rvReview)
    RecyclerView rvReview;

    @BindView(R.id.tvCheckAllReview)
    TextViewMedium tvCheckAllReview;

    @BindView(R.id.ivReview)
    ImageView ivReview;

    @BindView(R.id.llReview)
    LinearLayout llReview;

    @BindView(R.id.llratting)
    LinearLayout llratting;

    @BindView(R.id.tvProductName_one)
    TextViewLight tvProductNameOne;

    @BindView(R.id.tvProductName_two)
    TextViewBold tvProductNameTwo;

    @BindView(R.id.rvRelatedProduct)
    RecyclerView rvRelatedProduct;

    @BindView(R.id.llRelatedItem)
    LinearLayout llRelatedItem;

    @BindView(R.id.nsScroll)
    NestedScrollView nsScroll;

    @BindView(R.id.tvCart)
    TextViewBold tvCart;

    @BindView(R.id.tvBuyNow)
    TextViewBold tvBuyNow;

    @BindView(R.id.llAddToCart)
    LinearLayout llAddToCart;

    @BindView(R.id.llOutOfStock)
    LinearLayout llOutOfStock;

    @BindView(R.id.llMain)
    LinearLayout llMain;

    @BindView(R.id.ivWhatsappDrag)
    ImageView ivWhatsappDrag;

    @BindView(R.id.crMain)
    CoordinatorLayout crMain;

    @BindView(R.id.rvColor)
    RecyclerView rvColor;

    @BindView(R.id.tvColor)
    TextViewRegular tvColor;

    @BindView(R.id.llDialog)
    LinearLayout llDialog;

    @BindView(R.id.llColor)
    LinearLayout llColor;

    @BindView(R.id.tvSellerInfoTitle)
    TextViewMedium tvSellerInfoTitle;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ivProgress)
    ImageView ivProgress;

    @BindView(R.id.flAddToCart)
    FrameLayout flAddToCart;

    @BindView(R.id.flBuyNow)
    FrameLayout flBuyNow;

    @BindView(R.id.rv_quantityDiscount)
    RecyclerView rv_quantityDiscount;

    @BindView(R.id.ll_quantityDiscount)
    LinearLayout ll_quantityDiscount;


    @BindView(R.id.ll_controller)
    LinearLayout ll_controller;

    @BindView(R.id.tvDecrement)
    ImageView tvDecrement;

    @BindView(R.id.tvProductQuantity)
    TextViewBold tvProductQuantity;

    @BindView(R.id.tvIncrement)
    ImageView tvIncrement;

    @BindView(R.id.ivBuyNow)
    ImageView ivBuyNow;

    private boolean isDialogOpen = false;
    private boolean isDeepLinking = false;
    private TextView[] dots;
    private int[] layouts;
    private ProductImageViewPagerAdapter productImageViewPagerAdapter;
    private int currentPosition;
    private ProductColorAdapter productColorAdapter;
    private RelatedProductAdapter relatedProductAdapter;
    private ReviewAdapter reviewAdapter;
    private GroupProductAdapter groupProductAdapter;
    private ProductVariationAdapter productVariationAdapter;
    private CategoryList categoryList = Constant.CATEGORYDETAIL;
    private List<Variation> variationList;
    private int page = 1;
    private DatabaseHelper databaseHelper;
    private CustomToast toast;
    private float fiveRate, fourRate, threeRate, twoRate, oneRate;
    private float avgRatting;
    private int pos;
    private int defaultVariationId;
    private int VariationPage = 1;
    private boolean isFirstLoad = true;
    private RecyclerView rvProductVariation;
    private QuantityDiscountAdapter quantityDiscountAdapter;

    String UserRole = "";
    ArrayList<QuantityDiscount> listQuantity = new ArrayList<>();

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        getIntentData();
        ivWishList.setActivetint(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
        ivWishList.setColors(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)), Color.parseColor(getPreferences().getString(Constant.APP_TRANSPARENT, Constant.SECOND_COLOR)));
        UserRole = getPreferences().getString(RequestParamUtils.user_role, "");
        if (!UserRole.isEmpty() && UserRole.equals("retail-customer")) {
            ll_quantityDiscount.setVisibility(View.VISIBLE);
        } else {
            ll_quantityDiscount.setVisibility(View.GONE);
        }
        setScreenLayoutDirection();
        indexNote(Constant.CATEGORYDETAIL);
        List<CategoryList.Attribute> attributes = new ArrayList<>();

        if (Constant.CATEGORYDETAIL != null && Constant.CATEGORYDETAIL.attributes != null && Constant.CATEGORYDETAIL.attributes.size() > 0) {
            for (int i = 0; i < Constant.CATEGORYDETAIL.attributes.size(); i++) {
                if (Constant.CATEGORYDETAIL.attributes.get(i).variation) {
                    attributes.add(Constant.CATEGORYDETAIL.attributes.get(i));
                }
            }
        }
        categoryList.attributes = attributes;
        Constant.CATEGORYDETAIL = categoryList;
        tvPrice = findViewById(R.id.tvPrice);
        tvPrice1 = findViewById(R.id.tvPrice1);
        databaseHelper = new DatabaseHelper(this);
        String product = new Gson().toJson(categoryList);
        databaseHelper.addTorecentView(product, categoryList.id + "");
        toast = new CustomToast(this);
        if (Constant.IS_WISH_LIST_ACTIVE) {
            ivWishList.setVisibility(View.VISIBLE);
            if (databaseHelper.getWishlistProduct(categoryList.id + "")) {
                ivWishList.setChecked(true);
            } else {
                ivWishList.setChecked(false);
            }
        } else {
            ivWishList.setVisibility(View.GONE);
        }
        setQuntityDiscount();
        setData();
        setColorTheme();
        Intent intent = getIntent();
        if (intent.hasExtra(RequestParamUtils.fromdeeplink)) {
            isDeepLinking = intent.getBooleanExtra(RequestParamUtils.fromdeeplink, true);
        } else {
            isDeepLinking = false;
        }
        nsScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        if (!categoryList.relatedIds.isEmpty()) {
                            if (isFirstLoad == true) {
                                getRelatedProduct();
                                isFirstLoad = false;
                                llRelatedItem.setVisibility(View.VISIBLE);
                            }
                        } else {
                            llRelatedItem.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        if (Config.WOO_API_DELIVER_PINCODE) {
            etPincode.setHint(Constant.settingOptions.pincodePlaceholderTxt);
            llPincode.setVisibility(View.VISIBLE);
            tvDeliverable.setVisibility(View.VISIBLE);
        } else {
            llPincode.setVisibility(View.GONE);
            tvDeliverable.setVisibility(View.GONE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initCollapsingToolbar();


        setVisible();
        tvIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quntity = Integer.parseInt(tvProductQuantity.getText().toString());
                quntity = quntity + 1;


                if (categoryList.manageStock) {
                    if (quntity > categoryList.stockQuantity) {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.only) + " " + categoryList.stockQuantity + " " + getString(R.string.quntity_is_avilable), Toast.LENGTH_SHORT).show();
                    } else {
                        tvProductQuantity.setText(quntity + "");
                        databaseHelper.updateQuantity(quntity, categoryList.id + "", String.valueOf(databaseHelper.getProductvarationidFromCartById(categoryList.id + "")));
                        categoryList.setQuantity(quntity);

                        tvProductQuantity.setText(databaseHelper.getProductQuantity(categoryList.id + ""));
                        // onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                    }
                } else {
                    tvProductQuantity.setText(quntity + "");

                    Log.e(TAG, "onClick: " + databaseHelper.getProductvarationidFromCartById(categoryList.id + ""));
                    databaseHelper.updateQuantity(quntity, categoryList.id + "", String.valueOf(databaseHelper.getProductvarationidFromCartById(categoryList.id + "")));
                    categoryList.setQuantity(quntity);

                    tvProductQuantity.setText(databaseHelper.getProductQuantity(String.valueOf(categoryList.id + "")));
                    // onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                }

            }
        });

        tvDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int quntity = Integer.parseInt(tvProductQuantity.getText().toString());
                quntity = quntity - 1;
                if (quntity < 1) {
                    quntity = 1;
                }
                tvProductQuantity.setText(quntity + "");
                databaseHelper.updateQuantity(quntity, categoryList.id + "", String.valueOf(databaseHelper.getProductvarationidFromCartById(categoryList.id + "")));
                categoryList.setQuantity(quntity);

                tvProductQuantity.setText(databaseHelper.getProductQuantity(categoryList.id + ""));
                // onItemClickListner.onItemClick(position, RequestParamUtils.decrement, quntity);
            }
        });

    }

    private void setVisible() {
        tvIncrement.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvDecrement.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvProductQuantity.setTextColor(Color.parseColor(getPreferences().getString(Constant.PRIMARY_COLOR, Constant.PRIMARY_COLOR)));

        if(databaseHelper.getProductFromCartById(categoryList.id + "") != null){
            tvProductQuantity.setText(databaseHelper.getProductQuantity(categoryList.id + ""));
        }else {
            tvProductQuantity.setText("1");
        }

        if (databaseHelper.getProductFromCartById(categoryList.id + "") != null) {

            tvBuyNow.setVisibility(View.INVISIBLE);
            ivBuyNow.setVisibility(View.GONE);
            ll_controller.setVisibility(View.VISIBLE);

        } else {
            tvBuyNow.setVisibility(View.VISIBLE);
            ivBuyNow.setVisibility(View.VISIBLE);
            ll_controller.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onResume() {
        //Log.e(TAG, "onResume: called"+ databaseHelper.getVariationProductFromCart(getCartVariationProduct()));
        setVisible();
        if(categoryList.type.equals("variable")){
            if(databaseHelper.getVariationProductQuantityFromCart(getCartVariationProduct()) != " " && !databaseHelper.getVariationProductQuantityFromCart(getCartVariationProduct()).isEmpty()){

                tvProductQuantity.setText(databaseHelper.getVariationProductQuantityFromCart(getCartVariationProduct()));
            }

            //check for variation is availabe in cart
            if( databaseHelper.getVariationProductFromCart(getCartVariationProduct())){

            }else {

                ll_controller.setVisibility(View.INVISIBLE);
                tvBuyNow.setVisibility(View.VISIBLE);

            }

        }


        super.onResume();

    }
    private void setQuntityDiscount() {
        quantityDiscountAdapter = new QuantityDiscountAdapter(ProductDetailActivity.this, listQuantity);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_quantityDiscount.setLayoutManager(mLayoutManager);
        rv_quantityDiscount.setAdapter(quantityDiscountAdapter);


        listQuantity.add(new QuantityDiscount("1 and more", "₹80.00"));
        listQuantity.add(new QuantityDiscount("5 and more", "₹50.00"));
        listQuantity.add(new QuantityDiscount("10 and more", "₹20.00"));
        quantityDiscountAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initCollapsingToolbar() {
        collapsing_toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.PRIMARY_COLOR)));
        collapsing_toolbar.setTitle(tvProductName.getText().toString());
        collapsing_toolbar.setContentScrimColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        collapsing_toolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent_white));
        collapsing_toolbar.setCollapsedTitleTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        collapsing_toolbar.setStatusBarScrimColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));

        Drawable drawable = toolbar.getNavigationIcon();
        drawable.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)), PorterDuff.Mode.SRC_ATOP);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                Drawable drawable = toolbar.getNavigationIcon();
                drawable.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                mCurrentState = State.IDLE;
                if (i == 0) {
                    if (mCurrentState != State.EXPANDED) {
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.TRANSPARENT);
                        }
                    }
                } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                    if (mCurrentState != State.COLLAPSED) {
                        toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                        }
                        drawable = toolbar.getNavigationIcon();
                        drawable.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        drawable = toolbar.getNavigationIcon();
                        drawable.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                    }
                    mCurrentState = State.COLLAPSED;
                } else {
                    if (mCurrentState != State.IDLE) {
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.TRANSPARENT);
                        }
                    }
                }
            }
        });
    }

    public void variationPopupOrInPage() {

        if (Config.IS_VARIATION_POPUP_SHOW) {
            showDialog();
            if (variationList.size() == 0) {
                llProductVariation.setVisibility(View.GONE);
                llColor.setVisibility(View.GONE);
            } else {
                llProductVariation.setVisibility(View.GONE);
                llColor.setVisibility(View.VISIBLE);
            }
            productColorAdapter.addAllVariationList(variationList, productVariationAdapter);
            productColorAdapter.addAll(categoryList.attributes.get(0).options);

        } else {
            if (variationList.size() == 0) {
                llProductVariation.setVisibility(View.GONE);
                llColor.setVisibility(View.GONE);
            } else {
                llColor.setVisibility(View.GONE);
                llProductVariation.setVisibility(View.VISIBLE);
            }
            setVariation(rvVariation);
            productVariationAdapter.notifyDataSetChanged();
            changePrice();
        }
    }

    //TODO:For deeplinking
    public void getIntentData() {

        Intent intent = getIntent();
        if (intent.hasExtra(RequestParamUtils.fromdeeplink)) {
            isDeepLinking = intent.getBooleanExtra(RequestParamUtils.fromdeeplink, true);
        } else {
            isDeepLinking = false;
        }

//        if(Constant.IS_RTL) {
//            tvAvailibilitytext.setText(":" + getResources().getString(R.string.availability));
//        }else {
//            tvAvailibilitytext.setText( getResources().getString(R.string.availability)+":");
//        }
    }

    private void setColorTheme() {
        tvMoreQuickOverview.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvMoreDetail.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvFive.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvFour.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvThree.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvTwo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvOne.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvSellerInfoTitle.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ll_controller.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvBuyNow.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        String htmlPrice = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlPrice = String.valueOf(Html.fromHtml(categoryList.priceHtml + "", Html.FROM_HTML_MODE_COMPACT));
        } else {
            htmlPrice = (Html.fromHtml(categoryList.priceHtml) + "");
        }
        if (Config.IS_CATALOG_MODE_OPTION) {
            flBuyNow.setVisibility(View.GONE);
            flAddToCart.setVisibility(View.GONE);
        } else if (htmlPrice.equals("") && categoryList.price.equals("")) {
            flBuyNow.setVisibility(View.GONE);
            flAddToCart.setVisibility(View.GONE);
        } else {
            flBuyNow.setVisibility(View.VISIBLE);
            flAddToCart.setVisibility(View.VISIBLE);
        }

//        GradientDrawable gd = (GradientDrawable) llratting.getBackground();
//        gd.setStroke(5, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ivDetailMore.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ivQuickOverViewMore.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvSellerMore.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ivMoreSeller.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvReward.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvInfo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvCheck.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvDeliveryOptions.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvQuickOverViewTitle.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvProductDetailTitle.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvRatingTitle.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvProductNameOne.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvProductNameTwo.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        // tvContactSeller.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        Drawable unwrappedDrawable = tvContactSeller.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        unwrappedDrawable = tvReward.getBackground();
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.APP_TRANSPARENT_VERY_LIGHT, Constant.SECONDARY_COLOR))));

        Drawable background = tvRateReview.getBackground();
        Drawable drawables = DrawableCompat.wrap(background);
        DrawableCompat.setTint(drawables, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        Drawable unwrappedDrawables = tvViewStore.getBackground();
        Drawable wrappedDrawables = DrawableCompat.wrap(unwrappedDrawables);
        DrawableCompat.setTint(wrappedDrawables, getResources().getColor(R.color.black));
//        tvViewStore.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    @OnClick(R.id.tvRateReview)
    public void RateReviewClick() {
        String customerId = getPreferences().getString(RequestParamUtils.ID, "");
        if (tvRateReview.getVisibility() == View.VISIBLE) {
            if (getPreferences().getBoolean(RequestParamUtils.Review_Varification, false)) {
                if (customerId.equals("")) {
                    Intent intent = new Intent(ProductDetailActivity.this, LogInActivity.class);
                    startActivity(intent);
                } else {
                    verificationReview(customerId);
                }
            } else {
                int img = 0;
                Intent i = new Intent(this, RateAndReviewActivity.class);
                i.putExtra(RequestParamUtils.PRODUCT_NAME, tvProductName.getText().toString());
                i.putExtra(RequestParamUtils.PRODUCT_ID, String.valueOf(categoryList.id + ""));
                if (categoryList.images.size() > 0 || categoryList.images.size() == 1) {
                    i.putExtra(RequestParamUtils.IMAGEURL, categoryList.images.get(0).src);
                }
                startActivity(i);
            }
        }
    }

    @OnClick(R.id.tvCheck)
    public void CheckPinClick() {

        String pincode = etPincode.getText().toString();

        if (pincode.isEmpty()) {
            Toast.makeText(this, Constant.settingOptions.errorMsgBlank, Toast.LENGTH_LONG).show();
        } else {
            if (Utils.isInternetConnected(this)) {
                showProgress("");
                try {
                    PostApi postApi = new PostApi(this, RequestParamUtils.PincodeView, this, getlanuage());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(RequestParamUtils.PRODUCT_ID, categoryList.id);
                    jsonObject.put(RequestParamUtils.PincodeCheck, pincode);
                    postApi.callPostApi(new URLS().DELIVER_PINCODE, jsonObject.toString());
                } catch (Exception e) {
                    Log.e("verificationReview", e.getMessage());
                }
            } else {
                Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void verificationReview(String userId) {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            try {
                PostApi postApi = new PostApi(this, RequestParamUtils.VERIFY_REVIEW, this, getlanuage());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, userId);
                jsonObject.put(RequestParamUtils.PRODUCT_ID, categoryList.id);
//                Log.e("?lang=fr: ", getPreferences().getString(RequestParamUtils.DefaultLanguage, "") );
                postApi.callPostApi(new URLS().VERIFY_REVIEW + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("verificationReview", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }

    private void setData() {

//        GlideDrawableImageViewTarget ivSmily = new GlideDrawableImageViewTarget(ivProgress);
        Glide.with(this).load(R.raw.loader).into(ivProgress);
        if (categoryList.additionInfoHtml != null && !categoryList.additionInfoHtml.equals("")) {

            wv_info.loadData(categoryList.additionInfoHtml, "text/html; charset=UTF-8", null);

        } else {
            llInfo.setVisibility(View.GONE);
        }

        if (categoryList != null && categoryList.rewardMessage != null && !categoryList.rewardMessage.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvReward.setText(Html.fromHtml(categoryList.rewardMessage, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvReward.setText(Html.fromHtml(categoryList.rewardMessage));
            }
            tvReward.setVisibility(View.VISIBLE);
        } else {
            tvReward.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvProductName.setText(categoryList.name + "");
            tvProductName.setText(Html.fromHtml(categoryList.name + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
//            tvProductName.setText(categoryList.name + "");
            tvProductName.setText(Html.fromHtml(categoryList.name + ""));


        }
        //        if (price == null) {
//            price = categoryList.priceHtml;
//        }

//        Utils.setPrice(categoryList.priceHtml,tvPrice,tvPrice1,this);

        setPrice(categoryList.priceHtml);

//        if (categoryList != null && categoryList.averageRating != null && categoryList.averageRating.equals("")) {
//            tvRatting.setText("0");
//
//        } else {
//            if (categoryList.averageRating != null && !categoryList.averageRating.equals("0")) {
//                tvRatting.setText(Constant.setDecimalOne(Double.parseDouble(categoryList.averageRating)));
//            } else {
//                tvRatting.setText("0");
//            }
//        }

        if (categoryList.inStock) {
            tvAvailibility.setText(R.string.in_stock);
            tvAvailibility.setTextColor(getResources().getColor(R.color.green));
            llAddToCart.setVisibility(View.VISIBLE);
            llOutOfStock.setVisibility(View.GONE);
        } else {
            tvAvailibility.setText(R.string.out_of_stock);
            tvAvailibility.setTextColor(Color.RED);
            tvBuyNow.setAlpha((float) 0.6);
            tvBuyNow.setClickable(false);
            tvCart.setAlpha((float) 0.6);
            tvCart.setClickable(false);
            llAddToCart.setVisibility(View.GONE);
            llOutOfStock.setVisibility(View.VISIBLE);


        }


        setSellerInformation();
        setProductDescription();
        imageList = categoryList.images;

        if (categoryList.attributes.size() > 0) {

            setColorData();
            String text = categoryList.attributes.get(0).name.substring(0, 1).toUpperCase() + categoryList.attributes.get(0).name.substring(1).toLowerCase();
            tvColor.setText(categoryList.attributes.get(0).options.size() + " " + text);

        } else {

        }
        if (!categoryList.shortDescription.equals("")) {

            llQuickOverView.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(categoryList.shortDescription, Html.FROM_HTML_MODE_COMPACT));

            } else {
                tvDescription.setText(Html.fromHtml(categoryList.shortDescription));
            }
        } else {
            llQuickOverView.setVisibility(View.GONE);
        }
        //showBackButton();
//        showCart();
        // settvImage();
        setReviewData();
        setListRecycleView();
//        hideSearchNotification();
        if (categoryList.type.equals(RequestParamUtils.variable)) {
            getVariation();
        } else if (categoryList.type.equals(RequestParamUtils.simple)) {
            if (categoryList.featuredVideo != null && Constant.IS_YITH_FEATURED_VIDEO_ACTIVE
                    && categoryList.featuredVideo.imageUrl != null && categoryList.featuredVideo.videoId != null) {
                CategoryList.Image images = new CategoryList().getImageInstance();
                images.src = categoryList.featuredVideo.imageUrl;
                images.url = categoryList.featuredVideo.url;
                images.type = "Video";

                categoryList.images.add(0, images);
            }


            if (databaseHelper.getProductFromCartById(categoryList.id + "") != null) {
                tvCart.setText(getResources().getString(R.string.go_to_cart));
            } else {
                tvCart.setText(getResources().getString(R.string.add_to_Cart));
            }
            getReview();

        } else if (categoryList.type.equals(RequestParamUtils.grouped)) {
            if (categoryList.featuredVideo != null && categoryList.featuredVideo.url != null &&
                    categoryList.featuredVideo.imageUrl != null && Constant.IS_YITH_FEATURED_VIDEO_ACTIVE) {
                CategoryList.Image images = new CategoryList().getImageInstance();
                images.src = categoryList.featuredVideo.imageUrl;
                images.url = categoryList.featuredVideo.url;
                images.type = "Video";

                categoryList.images.add(0, images);
            }

            if (databaseHelper.getProductFromCartById(categoryList.id + "") != null) {
                tvCart.setText(getResources().getString(R.string.go_to_cart));
            } else {
                tvCart.setText(getResources().getString(R.string.add_to_Cart));
            }
            String groupis = "";
            for (int i = 0; i < categoryList.groupedProducts.size(); i++) {
                if (groupis.equals("")) {
                    groupis = groupis + categoryList.groupedProducts.get(i);
                } else {
                    groupis = groupis + "," + categoryList.groupedProducts.get(i);
                }
            }
            getGroupProducts(groupis);
            setRvGroupProduct();
        } else if (categoryList.type.equals(RequestParamUtils.external)) {
            finish();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryList.externalUrl));
            startActivity(browserIntent);
        }
        setVpProductImages();

        if (getPreferences().getBoolean(RequestParamUtils.Enable_Review, false)) {
            tvRateReview.setVisibility(View.VISIBLE);
        } else {
            tvRateReview.setVisibility(View.GONE);
        }
    }

    public void setPrice(String price) {
        if (price == null) {
            price = categoryList.priceHtml;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvPrice.setText(Html.fromHtml(price, Html.FROM_HTML_MODE_COMPACT));

        } else {
            tvPrice.setText(Html.fromHtml(price + " ") + "");
        }
        tvPrice.setTextSize(15);
        setPrice(tvPrice, tvPrice1, price);

        if (!categoryList.type.equals("variable")) {
            showDiscount(tvDiscount, categoryList.salePrice, categoryList.regularPrice);
        } else {
            showDiscount(tvDiscount, CheckIsVariationAvailable.salePrice + "", CheckIsVariationAvailable.regularPrice + "");
        }
    }

    private void setProductDescription() {
        if (categoryList.description != null && !categoryList.description.equals("")) {
            llProductDescription.setVisibility(View.VISIBLE);
            tvProductDescription.setHtml(categoryList.description,
                    new HtmlHttpImageGetter(tvProductDescription));
        }
    }


    private void setSellerInformation() {
        if (categoryList.sellerInfo != null && categoryList.sellerInfo.isSeller) {
            llIsSeller.setVisibility(View.VISIBLE);

            if (categoryList.sellerInfo.contactSeller) {
                tvContactSeller.setClickable(true);
                tvContactSeller.setVisibility(View.VISIBLE);
            } else {
                tvContactSeller.setClickable(false);
                tvContactSeller.setVisibility(View.INVISIBLE);
            }
        } else {
            llIsSeller.setVisibility(View.GONE);
        }
        if (categoryList != null && categoryList.sellerInfo != null) {
            if (categoryList.sellerInfo.soldBy) {
                llSoldBy.setVisibility(View.VISIBLE);
                tvSellerName.setText(categoryList.sellerInfo.storeName == null ? "" : categoryList.sellerInfo.storeName);
            } else {
                llSoldBy.setVisibility(View.GONE);
            }

            if (categoryList.sellerInfo.storeTnc == null || categoryList.sellerInfo.storeTnc.equals("")) {
                tvSellerMore.setVisibility(View.GONE);
                tvSellerContent.setVisibility(View.GONE);
                ivMoreSeller.setVisibility(View.GONE);
            } else {
                tvSellerMore.setVisibility(View.VISIBLE);
                tvSellerContent.setVisibility(View.VISIBLE);
                ivMoreSeller.setVisibility(View.VISIBLE);

                tvSellerContent.setHtml(categoryList.sellerInfo.storeTnc,
                        new HtmlHttpImageGetter(tvSellerContent));

            }
        }
    }

    @OnClick(R.id.tvCheckAllReview)
    public void tvCheckAllReviewClick() {
        Intent i = new Intent(this, CheckAllActivity.class);
        startActivity(i);
    }

    public void getVariation() {
        if (VariationPage == 1) {
            variationList = new ArrayList<>();
        }
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            GetApi getApi = new GetApi(this, RequestParamUtils.getVariation, this, getlanuage());

            String strURl = new URLS().WOO_MAIN_URL + new URLS().WOO_PRODUCT_URL + "/" + categoryList.id + "/" + new URLS().WOO_VARIATION;

            if (getPreferences().getString(RequestParamUtils.CurrencyText, "").equals("")) {
                strURl = strURl + "?page=" + VariationPage;
            } else {
                strURl = strURl + getPreferences().getString(RequestParamUtils.CurrencyText, "") + "&page=" + VariationPage;
            }
            getApi.callGetApi(strURl);
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void getReview() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            GetApi getApi = new GetApi(this, RequestParamUtils.getReview, this, getlanuage());
            getApi.setisDialogShow(false);
            getApi.callGetApi(new URLS().WOO_MAIN_URL + new URLS().WOO_PRODUCT_URL + "/" + categoryList.id + "/" + new URLS().WOO_REVIEWS);
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void getRelatedProduct() {
        if (!categoryList.relatedIds.isEmpty()) {
            if (Utils.isInternetConnected(this)) {
                PostApi postApi = new PostApi(ProductDetailActivity.this, RequestParamUtils.relatedProduct, this, getlanuage());
                Log.e("RelatedPoduct ", "get RelatedPoduct");
                try {
                    JSONObject jsonObject = new JSONObject();
                    String relatedid = "";
                    for (int i = 0; i < categoryList.relatedIds.size(); i++) {
                        if (relatedid.equals("")) {
                            relatedid = relatedid + categoryList.relatedIds.get(i);
                        } else {
                            relatedid = relatedid + "," + categoryList.relatedIds.get(i);
                        }
                    }
                    jsonObject.put(RequestParamUtils.INCLUDE, relatedid);
                    jsonObject.put(RequestParamUtils.PAGE, page);
                    jsonObject.put(RequestParamUtils.USER_ID, getPreferences().getString(RequestParamUtils.ID,"-"));
                    postApi.callPostApi(new URLS().PRODUCT_URL + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
                } catch (Exception e) {
                    Log.e("Json Exception", e.getMessage());
                }
            } else {
                Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
            }
        } else {
            llRelatedItem.setVisibility(View.GONE);
        }
    }

    public void getGroupProducts(String groupid) {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(ProductDetailActivity.this, RequestParamUtils.getGroupProducts, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.INCLUDE, groupid);
                jsonObject.put(RequestParamUtils.PAGE, page);
                jsonObject.put(RequestParamUtils.USER_ID, getPreferences().getString(RequestParamUtils.ID,"-"));
                postApi.callPostApi(new URLS().PRODUCT_URL, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void setListRecycleView() {
        relatedProductAdapter = new RelatedProductAdapter(this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRelatedProduct.setLayoutManager(mLayoutManager);
        rvRelatedProduct.setAdapter(relatedProductAdapter);
        ViewCompat.setNestedScrollingEnabled(rvRelatedProduct, false);
        rvRelatedProduct.setHasFixedSize(true);
        rvRelatedProduct.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        rvRelatedProduct.addItemDecoration(new EqualSpacingItemDecoration(10, EqualSpacingItemDecoration.HORIZONTAL)); // 16px. In practice, you'll want to use getDimensionPixelSize
    }

    @Override
    public void onResponse(String response, String methodName) {

        dismissProgress();

        if (methodName.equals(RequestParamUtils.getVariation)) {

            JSONArray jsonArray = null;
            if (response != null && response.length() > 0) {
                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jsonResponse = jsonArray.get(i).toString();
                        Variation variationRider = new Gson().fromJson(
                                jsonResponse, new TypeToken<Variation>() {
                                }.getType());


                        variationList.add(variationRider);
                    }
                    if (jsonArray.length() == 10) {
                        //more product available
                        VariationPage++;
                        getVariation();
                    } else {
                        variationPopupOrInPage();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                if (jsonArray == null || jsonArray.length() != 10) {
                    getReview();
                    getDefaultVariationId();
                }
            }
        } else if (methodName.equals(RequestParamUtils.getGroupProducts)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<CategoryList> categoryLists = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jsonResponse = jsonArray.get(i).toString();
                        CategoryList categoryListRider = new Gson().fromJson(
                                jsonResponse, new TypeToken<CategoryList>() {
                                }.getType());
                        categoryLists.add(categoryListRider);
//                        if (categoryListRider.type.equals("simple")) {
//                            isGroupProductAddToCart(categoryListRider.id + "");
//                        }


                    }
                    isGroupProductAddToCart();
                    groupProductAdapter.addAll(categoryLists);
                    if (categoryLists.size() > 0) {
                        rvGroupProduct.setVisibility(View.VISIBLE);
                    } else {
                        rvGroupProduct.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            }
            getReview();
        } else if (methodName.equals(RequestParamUtils.getReview)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<ProductReview> reviewList = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String jsonResponse = jsonArray.get(i).toString();
                            ProductReview productReviewRider = new Gson().fromJson(
                                    jsonResponse, new TypeToken<ProductReview>() {
                                    }.getType());
                            reviewList.add(productReviewRider);

                            if (productReviewRider.rating == 5) {
                                fiveRate = fiveRate + 1;
                            } else if (productReviewRider.rating == 4) {
                                fourRate = fourRate + 1;
                            } else if (productReviewRider.rating == 3) {
                                threeRate = threeRate + 1;
                            } else if (productReviewRider.rating == 2) {
                                twoRate = twoRate + 1;
                            } else if (productReviewRider.rating == 1) {
                                oneRate = oneRate + 1;
                            }

                        }
                        setRate(reviewList.size());
                    }
                    if (reviewList.size() > 3) {

                        List<ProductReview> reviewLists = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            reviewLists.add(reviewList.get(i));
                        }
                        reviewAdapter.addAll(reviewLists);
                    } else {
                        reviewAdapter.addAll(reviewList);
                    }

                    if (reviewList.size() < 3) {
                        tvCheckAllReview.setVisibility(View.GONE);
                        ivReview.setVisibility(View.GONE);
                    } else {
                        tvCheckAllReview.setVisibility(View.VISIBLE);
                        ivReview.setVisibility(View.VISIBLE);
                    }
                    dismissProgress();
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            } else {
                tvCheckAllReview.setVisibility(View.GONE);
                ivReview.setVisibility(View.GONE);
            }
        } else if (methodName.equals(RequestParamUtils.relatedProduct)) {
            if (response != null && response.length() > 0) {
                JSONArray jsonArray = null;
                categoryLists.clear();
                try {
                    jsonArray = new JSONArray(response);
                    categoryLists = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jsonResponse = jsonArray.get(i).toString();
                        CategoryList categoryListRider = new Gson().fromJson(
                                jsonResponse, new TypeToken<CategoryList>() {
                                }.getType());
                        categoryLists.add(categoryListRider);

                    }
                    relatedProductAdapter.addAll(categoryLists);
                    dismissProgress();
                    llRelatedItem.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                llRelatedItem.setVisibility(View.GONE);
            }

            ivProgress.setVisibility(View.GONE);
        } else if (methodName.equals(RequestParamUtils.removeWishList) || methodName.equals(RequestParamUtils.addWishList)) {
            dismissProgress();
        } else if (methodName.equals(RequestParamUtils.addToAbandondCart)) {
            Log.e("Response is ", response);
        } else if (methodName.equals(RequestParamUtils.VERIFY_REVIEW)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("status") && jsonObject.getString("status").equals("success")) {
                    boolean isOwner = jsonObject.getBoolean("owner");
                    if (isOwner) {
                        int img = 0;
                        Intent i = new Intent(this, RateAndReviewActivity.class);
                        i.putExtra(RequestParamUtils.PRODUCT_NAME, tvProductName.getText().toString());
                        i.putExtra(RequestParamUtils.PRODUCT_ID, String.valueOf(categoryList.id + ""));
                        if (categoryList.images.size() > 0 || categoryList.images.size() == 1) {
                            i.putExtra(RequestParamUtils.IMAGEURL, categoryList.images.get(0).src);
                        }
                        startActivity(i);
                    } else {
                        String message = getResources().getString(R.string.review_verify);
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String message = getResources().getString(R.string.review_verify);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("Exception =", e.getMessage());
            }

        } else if (methodName.equals(RequestParamUtils.PincodeView)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                tvDeliverable.setVisibility(View.VISIBLE);
                if (jsonObject.has("status") && jsonObject.getString("status").equals("success")) {
                    tvDeliverable.setText(Constant.settingOptions.availableatText + " " + etPincode.getText().toString());
                } else {
                    tvDeliverable.setText(Constant.settingOptions.codNotAvailableMsg + " " + etPincode.getText().toString());
                }
            } catch (Exception e) {
                Log.e(TAG, "onResponse: " + e.getMessage());
            }
        }
    }

    private void isGroupProductAddToCart() {
        for (int i = 0; i < categoryList.groupedProducts.size(); i++) {
            if (databaseHelper.getProductFromCartById(categoryList.groupedProducts.get(i) + "") != null) {
                tvCart.setText(getResources().getString(R.string.go_to_cart));
            } else {
                tvCart.setText(getResources().getString(R.string.add_to_Cart));
                break;
            }
        }
//        if (databaseHelper.getProductFromCartById(id + "") != null) {
//            tvCart.setText(getResources().getString(R.string.go_to_cart));
//            return true;
//        }
//        return false;
    }

    private void setRate(int totalReview) {
        tvAverageRatting.setText(Constant.setDecimalTwo(Double.valueOf(categoryList.averageRating)));
        ratingBar.setRating(Float.parseFloat(categoryList.averageRating));
        rattingFive.setProgress((fiveRate / totalReview) * 100);
        rattingFour.setProgress((fourRate / totalReview) * 100);
        rattingThree.setProgress((threeRate / totalReview) * 100);
        rattingTwo.setProgress((twoRate / totalReview) * 100);
        rattingOne.setProgress((oneRate / totalReview) * 100);
    }

    private void setVpProductImages() {
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).src != null && imageList.get(i).src.contains(RequestParamUtils.placeholder)) {

                if (imageList.get(i).type == null || !imageList.get(i).type.equals("Video")) {
                    if (imageList.size() > 1) {
                        imageList.remove(imageList.get(i));
                    }
                }
            }
        }

        addBottomDots(0, imageList.size());
        if (productImageViewPagerAdapter == null) {
            productImageViewPagerAdapter = new ProductImageViewPagerAdapter(this, categoryList.id);
            vpProductImages.setAdapter(productImageViewPagerAdapter);
            productImageViewPagerAdapter.addAll(imageList);
            vpProductImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    addBottomDots(position, imageList.size());
                    currentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            vpProductImages.setCurrentItem(0);
            productImageViewPagerAdapter.addAll(imageList);
        }


    }

    private void addBottomDots(int currentPage, int length) {
        layoutDots.removeAllViews();
        dots = new TextView[length];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.gray));
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    public void setRvGroupProduct() {
        groupProductAdapter = new GroupProductAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvGroupProduct.setLayoutManager(mLayoutManager);
        rvGroupProduct.setAdapter(groupProductAdapter);
        rvGroupProduct.setNestedScrollingEnabled(false);
    }


    public void setColorData() {
        productColorAdapter = new ProductColorAdapter(ProductDetailActivity.this, ProductDetailActivity.this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(mLayoutManager);
        rvColor.setAdapter(productColorAdapter);
        rvColor.setNestedScrollingEnabled(false);
        productColorAdapter.addAll(categoryList.attributes.get(0).options);
        productColorAdapter.setType(categoryList.type);
        productColorAdapter.getDialogList(categoryList.attributes);
    }

    public void setReviewData() {
        reviewAdapter = new ReviewAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvReview.setLayoutManager(mLayoutManager);
        rvReview.setAdapter(reviewAdapter);
        rvReview.setNestedScrollingEnabled(false);
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {
        if (outerPos != 11459060) {
            changePrice();
        } else if (outerPos == 11459060) {

            boolean isallProductAdded = false;

            for (int i = 0; i < groupProductAdapter.getList().size(); i++) {
                if (databaseHelper.getProductFromCartById(groupProductAdapter.getList().get(i).id + "") != null) {
                    isallProductAdded = true;
                } else {
                    isallProductAdded = false;
                    break;
                }
            }
            if (isallProductAdded) {
                tvCart.setText(getResources().getString(R.string.go_to_cart));
            } else {
                tvCart.setText(getResources().getString(R.string.add_to_Cart));
            }
            if (groupProductAdapter != null) {
                groupProductAdapter.notifyDataSetChanged();
            }

        }


//        changePrice();

    }

    @OnClick(R.id.tvMoreQuickOverview)
    public void tvMoreQuickOverviewClick() {
        Intent intent = new Intent(this, ProductQuickDetailActivity.class);
        intent.putExtra(RequestParamUtils.title, getString(R.string.quick_overviews));
        intent.putExtra(RequestParamUtils.name, categoryList.name + "");
        intent.putExtra(RequestParamUtils.description, categoryList.shortDescription + "");
        if (categoryList.images.size() > 0) {
            intent.putExtra(RequestParamUtils.image, categoryList.images.get(0).src);
        } else {
            intent.putExtra(RequestParamUtils.image, "");
        }
        startActivity(intent);
    }

    @OnClick(R.id.tvMoreDetail)
    public void tvMoreDetailClick() {
        Intent intent = new Intent(this, ProductQuickDetailActivity.class);
        intent.putExtra(RequestParamUtils.title, getString(R.string.detail));
        intent.putExtra(RequestParamUtils.name, categoryList.name + "");
        intent.putExtra(RequestParamUtils.description, categoryList.description + "");
        if (categoryList.images.size() > 0) {
            intent.putExtra(RequestParamUtils.image, categoryList.images.get(0).src);
        } else {
            intent.putExtra(RequestParamUtils.image, "");
        }
        startActivity(intent);
    }

    @OnClick(R.id.tvSellerMore)
    public void tvSellerMoreClick() {
        Intent intent = new Intent(this, SellerMoreInfoActivity.class);
        intent.putExtra(RequestParamUtils.data, categoryList.sellerInfo.storeTnc);
        intent.putExtra(RequestParamUtils.Dealer, categoryList.sellerInfo.storeName);
        startActivity(intent);

    }

    @OnClick(R.id.tvBuyNow)
    public void tvBuyNowClick() {

        if (categoryList.type.equals(RequestParamUtils.variable)) {

            isDialogOpen = true;

            if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, categoryList.attributes)) {
                toast.showToast(getString(R.string.variation_not_available));
                toast.showRedbg();
            } else {
                if (getCartVariationProduct() != null) {
                    Cart cart = getCartVariationProduct();

                    if (!databaseHelper.getVariationProductFromCart(cart)) {
                        databaseHelper.addVariationProductToCart(getCartVariationProduct());
                        //  showCart();
                        toast.showToast(getString(R.string.item_added_to_your_cart));
                        toast.showBlackbg();
                    }
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    intent.putExtra(RequestParamUtils.buynow, 0);
                    startActivity(intent);


                } else {
                    toast.showToast(getString(R.string.variation_not_available));
                    toast.showRedbg();
                }
            }
        } else if (categoryList.type.equals(RequestParamUtils.simple)) {
            Cart cart = new Cart();
            cart.setQuantity(1);
            cart.setProduct(new Gson().toJson(categoryList));
            cart.setCspPrice(new Gson().toJson(categoryList.cspPrices));
            cart.setVariationid(0);
            cart.setProductid(categoryList.id + "");
            cart.setManageStock(categoryList.manageStock);
            cart.setStockQuantity(categoryList.stockQuantity);

            addToCart(cart, categoryList.id + "");
        } else if (categoryList.type.equals(RequestParamUtils.grouped)) {

            for (int i = 0; i < groupProductAdapter.getList().size(); i++) {
                if (groupProductAdapter.getList().get(i).type.equals(RequestParamUtils.simple)) {
                    JSONObject object = new JSONObject();
                    try {
                        for (int j = 0; j < groupProductAdapter.getList().get(i).attributes.size(); j++) {
                            object.put(groupProductAdapter.getList().get(i).attributes.get(j).name, groupProductAdapter.getList().get(i).attributes.get(j).options.get(0));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Cart cart = new Cart();
                    cart.setQuantity(1);
                    cart.setVariation(object.toString());
                    cart.setProduct(new Gson().toJson(groupProductAdapter.getList().get(i)));
                    cart.setCspPrice(new Gson().toJson(groupProductAdapter.getList().get(i).cspPrices));
                    cart.setVariationid(0);
                    cart.setProductid(groupProductAdapter.getList().get(i).id + "");

                    addToCart(cart, categoryList.id + "");
                }
            }
        }
    }

    @OnClick(R.id.tvCart)
    public void tvCartClick() {
        if (categoryList.type.equals(RequestParamUtils.variable)) {

            isDialogOpen = true;
            if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, categoryList.attributes)) {
                toast = new CustomToast(this);
                toast.showToast(getString(R.string.variation_not_available));
                toast.showRedbg();
            } else {
                if (getCartVariationProduct() != null) {
                    Cart cart = getCartVariationProduct();
                    if (databaseHelper.getVariationProductFromCart(cart)) {
                        Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                        intent.putExtra(RequestParamUtils.buynow, 0);
                        startActivity(intent);
                    } else {
                        databaseHelper.addVariationProductToCart(getCartVariationProduct());
                        // showCart();
                        toast.showToast(getString(R.string.item_added_to_your_cart));
                        toast.showBlackbg();
                        tvCart.setText(getResources().getString(R.string.go_to_cart));
                    }
                } else {
                    toast.showToast(getString(R.string.variation_not_available));
                    toast.showRedbg();
                }
//                }
            }
        } else if (categoryList.type.equals(RequestParamUtils.simple)) {
            Cart cart = new Cart();
            cart.setQuantity(1);
            cart.setProduct(new Gson().toJson(categoryList));
            cart.setCspPrice(new Gson().toJson(categoryList.cspPrices));
            cart.setVariationid(0);
            cart.setProductid(categoryList.id + "");
            cart.setBuyNow(0);
            cart.setManageStock(categoryList.manageStock);
            cart.setStockQuantity(categoryList.stockQuantity);

            if (databaseHelper.getProductFromCartById(categoryList.id + "") != null) {
                databaseHelper.addToCart(cart);
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                intent.putExtra(RequestParamUtils.buynow, 0);
                startActivity(intent);
            } else {
                databaseHelper.addToCart(cart);
                // showCart();
                toast.showToast(getString(R.string.item_added_to_your_cart));
                toast.showBlackbg();
                tvCart.setText(getResources().getString(R.string.go_to_cart));
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
            Log.e(TAG, "tvCartClick: " + value);

            try {
                logAddedToCartEvent(String.valueOf(categoryList.id), categoryList.name, Constant.CURRENCYSYMBOL, Double.parseDouble(value));
            } catch (Exception e) {

            }
        } else if (categoryList.type.equals(RequestParamUtils.grouped)) {
            for (int i = 0; i < groupProductAdapter.getList().size(); i++) {
                if (groupProductAdapter.getList().get(i).type.equals(RequestParamUtils.simple)) {
                    JSONObject object = new JSONObject();
                    try {
                        for (int j = 0; j < groupProductAdapter.getList().get(i).attributes.size(); j++) {
                            object.put(groupProductAdapter.getList().get(i).attributes.get(j).name, groupProductAdapter.getList().get(i).attributes.get(j).options.get(0));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Cart cart = new Cart();
                    cart.setQuantity(1);
                    cart.setVariation(object.toString());
                    cart.setProduct(new Gson().toJson(groupProductAdapter.getList().get(i)));
                    cart.setCspPrice(new Gson().toJson(groupProductAdapter.getList().get(i).cspPrices));
                    cart.setVariationid(0);
                    cart.setProductid(groupProductAdapter.getList().get(i).id + "");
                    cart.setBuyNow(0);
                    cart.setManageStock(categoryList.manageStock);
                    cart.setStockQuantity(groupProductAdapter.getList().get(i).stockQuantity);

                    if (databaseHelper.getProductFromCartById(groupProductAdapter.getList().get(i).id + "") != null) {
                        databaseHelper.addToCart(cart);
                        if (i == groupProductAdapter.getItemCount() - 1) {
                            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                            intent.putExtra(RequestParamUtils.buynow, 0);
                            startActivity(intent);
                        }
                    } else {
                        databaseHelper.addToCart(cart);
                        //  showCart();
                        toast.showToast(getString(R.string.item_added_to_your_cart));
                        toast.showBlackbg();
                        tvCart.setText(getResources().getString(R.string.go_to_cart));
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
                    Log.e(TAG, "tvCartClick: " + value);

                    try {
                        logAddedToCartEvent(String.valueOf(groupProductAdapter.getList().get(i).id), groupProductAdapter.getList().get(i).name, Constant.CURRENCYSYMBOL, Double.parseDouble(value));
                    } catch (Exception e) {

                    }
                }
            }
        }
        if (databaseHelper.getProductFromCartById(categoryList.id + "") != null) {

            tvBuyNow.setVisibility(View.INVISIBLE);
            ivBuyNow.setVisibility(View.INVISIBLE);
            ll_controller.setVisibility(View.VISIBLE);

        } else {
            tvBuyNow.setVisibility(View.VISIBLE);
            ivBuyNow.setVisibility(View.VISIBLE);
            ll_controller.setVisibility(View.INVISIBLE);
        }
    }

    public Cart getCartVariationProduct() {
        Log.e("getCartVariation", "called");
        String appTumbnail = categoryList.appthumbnail;
        boolean ismanageStock = categoryList.manageStock;
        String htmlPrice = categoryList.priceHtml;
        List<String> list = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < combination.size(); i++) {
                String value = combination.get(i);
                String[] valuearray = new String[0];
                if (value != null && value.contains("->")) {
                    valuearray = value.split("->");
                }
                if (valuearray.length > 0) {
                    object.put(valuearray[0], valuearray[1]);
                }
                list.add(combination.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Cart cart = new Cart();
        cart.setQuantity(1);
        cart.setVariation(object.toString());
        setVariationPriceAndOtherDetailToList(ismanageStock);
        cart.setVariationid(new CheckIsVariationAvailable().getVariationid(variationList, list));
        cart.setProductid(categoryList.id + "");
        cart.setBuyNow(0);
        cart.setManageStock(categoryList.manageStock);
        cart.setStockQuantity(CheckIsVariationAvailable.stockQuantity);
        setImagesByVariation(cart.getVariationid());

        cart.setProduct(new Gson().toJson(categoryList));
        cart.setCspPrice(new Gson().toJson(categoryList.cspPrices));
        setOriginalPrice(htmlPrice, appTumbnail, ismanageStock);
        return cart;

    }

    public void getDefaultVariationId() {
        Log.e("default variation id ", "called");
        List<String> list = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < combination.size(); i++) {
                String value = combination.get(i);
                String[] valuearray = new String[0];
                if (value.contains("->")) {
                    valuearray = value.split("->");
                }
                if (valuearray.length > 0) {
                    object.put(valuearray[0], valuearray[1]);
                }
                list.add(combination.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        defaultVariationId = new CheckIsVariationAvailable().getVariationid(variationList, list);

        setImagesByVariation(defaultVariationId);
    }

    public void setImagesByVariation(int variationId) {

        if (variationId != defaultVariationId) {
            if (CheckIsVariationAvailable.imageSrc != null && !CheckIsVariationAvailable.imageSrc.contains(RequestParamUtils.placeholder)) {
                imageList = new ArrayList<>();
                addSelectedImage();
                productImageViewPagerAdapter = new ProductImageViewPagerAdapter(this, categoryList.id);
                vpProductImages.setAdapter(productImageViewPagerAdapter);
                addBottomDots(0, imageList.size());
                productImageViewPagerAdapter.addAll(imageList);
            } else {
                imageList = new ArrayList<>();
                imageList.addAll(categoryList.images);
                setVpProductImages();
            }
        } else {
            if (CheckIsVariationAvailable.imageSrc != null && !CheckIsVariationAvailable.imageSrc.contains(RequestParamUtils.placeholder)) {
                imageList = new ArrayList<>();
                addSelectedImage();
                imageList.addAll(categoryList.images);
                setVpProductImages();
            }
        }
    }

    public void addSelectedImage() {

        CategoryList.Image image = new CategoryList().getImageInstance();
        if (categoryList.featuredVideo != null && categoryList.featuredVideo.imageUrl != null &&
                Constant.IS_YITH_FEATURED_VIDEO_ACTIVE) {
            image.src = categoryList.featuredVideo.imageUrl;
            image.url = categoryList.featuredVideo.url;
            image.type = "Video";
            imageList.add(image);
        }

        image = new CategoryList().getImageInstance();
        image.src = CheckIsVariationAvailable.imageSrc;
        imageList.add(image);
    }


    @OnClick(R.id.tvContactSeller)
    public void tvContactSellerClick() {
        Intent intent = new Intent(this, ContactSellerActivity.class);
        intent.putExtra(RequestParamUtils.ID, categoryList.sellerInfo.sellerId);
        intent.putExtra(RequestParamUtils.Dealer, categoryList.sellerInfo.storeName);
        startActivity(intent);
    }

    @OnClick(R.id.tvViewStore)
    public void tvViewStoreClick() {
        sellerRedirection();
    }

    @OnClick(R.id.tvSellerName)
    public void tvSellerNameClick() {
        sellerRedirection();
    }

    public void sellerRedirection() {
        Intent intent = new Intent(this, SellerInfoActivity.class);
        intent.putExtra(RequestParamUtils.ID, categoryList.sellerInfo.sellerId);
        startActivity(intent);
    }

    @OnClick(R.id.ivShare)
    public void ivShareClick() {
        showProgress("");

        if (Constant.DeepLinkDomain != null && !Constant.DeepLinkDomain.isEmpty()) {
            String strdescription = "";
            if (categoryList.shortDescription != null && categoryList.shortDescription.length() > 0) {
                if (categoryList.shortDescription.length() >= 300) {
                    strdescription = categoryList.shortDescription;
                    strdescription = strdescription.substring(0, 300);
                } else {
                    strdescription = categoryList.shortDescription;
                }

            } else {
                strdescription = "";
            }

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(categoryList.permalink + "#" + categoryList.id))
                    .setDomainUriPrefix(Constant.DeepLinkDomain)
                    // Open links with this app on Android
                    .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder()
                                    .setMinimumVersion(Constant.PlaystoreMinimumVersion)
                                    .build())
                    // Open links with com.example.ios on iOS
                    .setIosParameters(
                            new DynamicLink.IosParameters.Builder(Constant.DynamicLinkIosParameters)
                                    .setAppStoreId(Constant.AppleAppStoreId)
                                    .setMinimumVersion(Constant.AppleAppVersion)
                                    .build())
                    .setSocialMetaTagParameters(
                            new DynamicLink.SocialMetaTagParameters.Builder()
                                    .setTitle(categoryList.name)
                                    .setDescription(strdescription)
                                    .setImageUrl(Uri.parse(imageList.get(0).src))
                                    .build())
                    .buildDynamicLink();

            Uri dynamicLinkUri = dynamicLink.getUri();

            Log.e(TAG, "ivShareClick: " + dynamicLinkUri);


            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(dynamicLinkUri)
                    .setDomainUriPrefix(Constant.DeepLinkDomain)
                    // Set parameters
                    // ...
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            dismissProgress();
                            try {
                                Uri shortLink = task.getResult().getShortLink();
                                Uri flowChartLink = task.getResult().getPreviewLink();
                                Log.e(TAG, "shortLink: " + shortLink.toString());
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                Log.e(TAG, "categoryList Id: " + shortLink + "#" + categoryList.id);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString() + "#" + categoryList.id);
                                // shareIntent.putExtra(Intent.EXTRA_TEXT, categoryList.permalink);
                                startActivity(Intent.createChooser(shareIntent, "Share link using"));

                                Log.e(TAG, "shortLink: " + shortLink);
                            } catch (Exception e) {
                                Log.e("Exception is ", e.getMessage());
                                dismissProgress();
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, categoryList.permalink);
                                startActivity(Intent.createChooser(shareIntent, "Share link using"));
                            }
                        }
                    });
            Log.e("Dynemic link is ", dynamicLink.getUri().toString());
        } else {
            dismissProgress();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, categoryList.permalink);
            startActivity(Intent.createChooser(shareIntent, "Share link using"));
        }
    }


    @OnClick(R.id.llDialog)
    public void lLDialogClick() {

        if (categoryList.type.equals(RequestParamUtils.variable)) {
            if (alertDialog != null) {
                alertDialog.show();
            }
            productColorAdapter.notifyDataSetChanged();
//            productColorAdapter.addAllVariationList(variationList);
        } else if (categoryList.type.equals(RequestParamUtils.simple)) {
            alertDialog.show();
        }
    }


    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);

        rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);
        setVariation(rvProductVariation);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        tvCancel.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvDone.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null) {
                    alertDialog.show();
                }
                if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, categoryList.attributes)) {
                    toast.showToast(getString(R.string.combition_doesnt_exist));
                } else {
                    toast.cancelToast();
                    alertDialog.dismiss();
                    if (databaseHelper.getVariationProductFromCart(getCartVariationProduct())) {
                        tvCart.setText(getResources().getString(R.string.go_to_cart));
                    } else {
                        tvCart.setText(getResources().getString(R.string.add_to_Cart));
                    }
                    changePrice();
                }
            }
        });
        changePrice();
    }

    public void setVariation(RecyclerView rvVariation) {
        productVariationAdapter = new ProductVariationAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvVariation.setLayoutManager(mLayoutManager);
        rvVariation.setAdapter(productVariationAdapter);
        rvVariation.setNestedScrollingEnabled(false);
        productVariationAdapter.addAll(categoryList.attributes);
        productVariationAdapter.addAllVariationList(variationList);
    }


    public void addToCart(Cart cart, String id) {
        //   showCart();
        cart.setBuyNow(0);
        databaseHelper.addToCart(cart);
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra(RequestParamUtils.ID, categoryList.id + "");
        intent.putExtra(RequestParamUtils.buynow, 0);
        startActivity(intent);
    }


    public void changePrice() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < combination.size(); i++) {
            list.add(combination.get(i));
        }
        new CheckIsVariationAvailable().getVariationid(variationList, list);
        if (CheckIsVariationAvailable.pricehtml != null) {
            setPrice(CheckIsVariationAvailable.pricehtml);
        }
        if (categoryList.inStock) {
            if (categoryList.type.equals(RequestParamUtils.variable)) {
                if (categoryList.manageStock == true) {
                    if (CheckIsVariationAvailable.inStock && CheckIsVariationAvailable.stockQuantity != 0) {
                        tvAvailibility.setText(R.string.in_stock);
                        tvAvailibility.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
                        tvBuyNow.setClickable(true);
                        tvBuyNow.setAlpha((float) 1.0);
                        tvCart.setClickable(true);
                        tvCart.setAlpha((float) 1.0);
                    } else {
                        tvAvailibility.setText(R.string.out_of_stock);
                        tvAvailibility.setTextColor(Color.RED);
                        tvBuyNow.setAlpha((float) 0.6);
                        tvBuyNow.setClickable(false);
                        tvCart.setAlpha((float) 0.6);
                        tvCart.setClickable(false);
                    }
                } else {

                    if (CheckIsVariationAvailable.inStock) {
                        tvAvailibility.setText(R.string.in_stock);
                        tvAvailibility.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
                        tvBuyNow.setClickable(true);
                        tvBuyNow.setAlpha((float) 1.0);
                        tvCart.setClickable(true);
                        tvCart.setAlpha((float) 1.0);
                    } else {
                        tvAvailibility.setText(R.string.out_of_stock);
                        tvAvailibility.setTextColor(Color.RED);
                        tvBuyNow.setAlpha((float) 0.6);
                        tvBuyNow.setClickable(false);
                        tvCart.setAlpha((float) 0.6);
                        tvCart.setClickable(false);
                    }
                }
            } else {
                tvAvailibility.setText(R.string.in_stock);
                tvAvailibility.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
            }


        } else {
            tvAvailibility.setText(R.string.out_of_stock);
            tvAvailibility.setTextColor(Color.RED);
            tvBuyNow.setAlpha((float) 0.6);
            tvBuyNow.setClickable(false);
            tvCart.setAlpha((float) 0.6);
            tvCart.setClickable(false);
        }

        if (databaseHelper.getVariationProductFromCart(getCartVariationProduct())) {
            tvCart.setText(getResources().getString(R.string.go_to_cart));
        } else {
            tvCart.setText(getResources().getString(R.string.add_to_Cart));
        }
//on change variation
        if (databaseHelper.getVariationProductFromCart(getCartVariationProduct())) {

            if(databaseHelper.getProductFromCartById(categoryList.id + "") != null){
                tvProductQuantity.setText(databaseHelper.getProductQuantity(categoryList.id + ""));
            }else {
                tvProductQuantity.setText("1");
            }
            tvCart.setText(getResources().getString(R.string.go_to_cart));
            ll_controller.setVisibility(View.VISIBLE);
            tvBuyNow.setVisibility(View.INVISIBLE);
            ivBuyNow.setVisibility(View.INVISIBLE);
            //setVisible();
        } else {


            tvCart.setText(getResources().getString(R.string.add_to_Cart));
            ll_controller.setVisibility(View.INVISIBLE);
            tvBuyNow.setVisibility(View.VISIBLE);
            ivBuyNow.setVisibility(View.VISIBLE);
            // setVisible();
        }




        if(databaseHelper.getVariationProductQuantityFromCart(getCartVariationProduct()) != " " && !databaseHelper.getVariationProductQuantityFromCart(getCartVariationProduct()).isEmpty()){

            tvProductQuantity.setText(databaseHelper.getVariationProductQuantityFromCart(getCartVariationProduct()));
        }

    }


    @OnClick(R.id.ivWishList)
    public void ivWishListClick() {
        if (databaseHelper.getWishlistProduct(categoryList.id + "")) {
            ivWishList.setChecked(false);
            String userid = getPreferences().getString(RequestParamUtils.ID, "");
            if (!userid.equals("")) {
                removeWishList(true, userid, categoryList.id + "");
            }
            databaseHelper.deleteFromWishList(categoryList.id + "");
        } else {
            ivWishList.setChecked(true);
            ivWishList.playAnimation();
            WishList wishList = new WishList();
            wishList.setProduct(new Gson().toJson(categoryList));
            wishList.setCspPrice(new Gson().toJson(categoryList.cspPrices));
            wishList.setProductid(categoryList.id + "");
            databaseHelper.addToWishList(wishList);
            String userid = getPreferences().getString(RequestParamUtils.ID, "");
            if (!userid.equals("")) {
                removeWishList(true, userid, categoryList.id + "");
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
                logAddedToWishlistEvent(String.valueOf(categoryList.id), categoryList.name, Constant.CURRENCYSYMBOL, Double.parseDouble(value));
            } catch (Exception e) {

            }

        }
    }

    public void removeWishList(boolean isDialogShow, String userid, String productid) {
        if (Utils.isInternetConnected(this)) {
            if (isDialogShow) {
                showProgress("");
            }

            PostApi postApi = new PostApi(ProductDetailActivity.this, RequestParamUtils.removeWishList, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, userid);
                jsonObject.put(RequestParamUtils.PRODUCT_ID, productid);
                postApi.callPostApi(new URLS().REMOVE_FROM_WISHLIST, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        showCart();

        if (!categoryList.type.equals("grouped")) {
            if (databaseHelper.getProductFromCartById(categoryList.id + "") != null) {
                tvCart.setText(getResources().getString(R.string.go_to_cart));
            } else {
                tvCart.setText(getResources().getString(R.string.add_to_Cart));
            }
        } else {
            boolean isallProductAdded = false;
            for (int i = 0; i < groupProductAdapter.getList().size(); i++) {
                if (databaseHelper.getProductFromCartById(groupProductAdapter.getList().get(i).id + "") != null) {
                    isallProductAdded = true;
                } else {
                    isallProductAdded = false;
                    break;
                }
            }

            if (isallProductAdded) {
                tvCart.setText(getResources().getString(R.string.go_to_cart));
            } else {
                tvCart.setText(getResources().getString(R.string.add_to_Cart));
            }
            if (groupProductAdapter != null) {
                groupProductAdapter.notifyDataSetChanged();
            }
        }

        Constant.CATEGORYDETAIL = categoryList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Backpressed();
    }

    public void Backpressed() {
        if (isDeepLinking) {
            Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    public void setVariationPriceAndOtherDetailToList(boolean ismanage) {

        if (CheckIsVariationAvailable.pricehtml != null) {
            categoryList.priceHtml = CheckIsVariationAvailable.pricehtml;
            categoryList.price = CheckIsVariationAvailable.price + "";
            categoryList.taxPrice = CheckIsVariationAvailable.taxPrice + "";
        }
        if (CheckIsVariationAvailable.imageSrc != null && !CheckIsVariationAvailable.imageSrc.contains(RequestParamUtils.placeholder)) {
            categoryList.appthumbnail = CheckIsVariationAvailable.imageSrc;
        }
        if (!ismanage) {
            categoryList.manageStock = CheckIsVariationAvailable.isManageStock;
        }
    }

    public void setOriginalPrice(String priceHtml, String appthumbnail, boolean ismanage) {
        categoryList.priceHtml = priceHtml;
        categoryList.appthumbnail = appthumbnail;
        categoryList.manageStock = ismanage;
//        categoryList.price = CheckIsVariationAvailable.price + "";
//        categoryList.taxPrice = CheckIsVariationAvailable.taxPrice + "";
    }
}



