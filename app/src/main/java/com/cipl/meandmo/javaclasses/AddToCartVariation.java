package com.cipl.meandmo.javaclasses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cipl.meandmo.activity.HomeActivity;
import com.ciyashop.library.apicall.GetApi;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.CartActivity;
import com.cipl.meandmo.activity.ProductDetailActivity;
import com.cipl.meandmo.adapter.GroupProductAdapter;
import com.cipl.meandmo.adapter.ProductVariationAdapter;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Cart;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.Variation;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Config;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.CustomToast;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddToCartVariation implements OnResponseListner, OnItemClickListner {

    private static final String TAG = "AddToCartVariation";
    private Activity activity;
    private int VariationPage = 1;
    private List<Variation> variationList = new ArrayList<>();
    private int defaultVariationId;
    private DatabaseHelper databaseHelper;
    private CustomToast toast;
    private int page = 1;
    AlertDialog alertDialog;
    private CategoryList list;
    TextViewRegular tvDone;
    ImageView tvAddToCart;
    LinearLayout llControlle;
    HomeActivity homeActivity;


    public AddToCartVariation(Activity activity) {
        this.activity = activity;
        this.databaseHelper = new DatabaseHelper(activity);
        this.toast = new CustomToast(activity);
    }

    public void addToCart(final ImageView tvAddToCart, String detail, LinearLayout ll_controller) {

        this.tvAddToCart = tvAddToCart;
        Drawable unwrappedDrawable = tvAddToCart.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        this.list = new Gson().fromJson(detail, new TypeToken<CategoryList>() {
        }.getType());
        String htmlPrice = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlPrice = String.valueOf(Html.fromHtml(list.priceHtml + "", Html.FROM_HTML_MODE_COMPACT));
        } else {
            htmlPrice = (Html.fromHtml(list.priceHtml) + "");
        }

        if (Constant.IS_ADD_TO_CART_ACTIVE) {
            if (Config.IS_CATALOG_MODE_OPTION) {
                tvAddToCart.setVisibility(View.GONE);
            } else if (htmlPrice.equals("") && list.price.equals("")) {
                tvAddToCart.setVisibility(View.GONE);
            } else {
                tvAddToCart.setVisibility(View.VISIBLE);
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
                if (list.type.equals(RequestParamUtils.grouped)) {
                    if (databaseHelper != null) {
                        for (int i = 0; i < list.groupedProducts.size(); i++) {
                            if (databaseHelper.getProductFromCartById(list.groupedProducts.get(i) + "") != null) {
                                tvAddToCart.setImageResource(R.drawable.ic_check);
                                ll_controller.setVisibility(View.VISIBLE);
                            } else {
                                tvAddToCart.setImageResource(R.drawable.ic_cart_white);
                                ll_controller.setVisibility(View.INVISIBLE);
                                break;
                            }
                        }
                    }
                } else if (list.type.equals(RequestParamUtils.simple)) {
                    if (databaseHelper != null) {
                        if (databaseHelper.getProductFromCartById(list.id + "") != null) {
                            tvAddToCart.setImageResource(R.drawable.ic_check);
                            ll_controller.setVisibility(View.VISIBLE);
                        } else {
                            tvAddToCart.setImageResource(R.drawable.ic_cart_white);
                            ll_controller.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
//                    tvAddToCart.setText(activity.getResources().getString(R.string.add_to_Cart));
                }
                if (!list.inStock) {
                    tvAddToCart.setClickable(false);
                    unwrappedDrawable = tvAddToCart.getBackground();
                    wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.RED);
                } else {
                    unwrappedDrawable = tvAddToCart.getBackground();
                    wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
                }
                tvAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.inStock) {
                            if (list.type.equals("variable")) {
                                callApi();
                            } else if (list.type.equals("simple")) {
                                tvAddToCart.setImageResource(R.drawable.ic_check);
                                ll_controller.setVisibility(View.VISIBLE);
                                Cart cart = new Cart();
                                cart.setQuantity(1);
                                cart.setVariation("{}");
                                cart.setProduct(new Gson().toJson(list));
                                cart.setCspPrice(new Gson().toJson(list.cspPrices));
                                cart.setVariationid(0);
                                cart.setProductid(list.id + "");
                                cart.setBuyNow(0);
                                cart.setManageStock(list.manageStock);

                                if (databaseHelper.getProductFromCartById(list.id + "") != null) {
                                    databaseHelper.addToCart(cart);
                                    Intent intent = new Intent(activity, CartActivity.class);
                                    intent.putExtra("buynow", 0);
                                    activity.startActivity(intent);
                                } else {
                                    databaseHelper.addToCart(cart);
                                    ((BaseActivity) activity).showCart();
                                    toast = new CustomToast(activity);
                                    toast.showToast(activity.getString(R.string.item_added_to_your_cart));
                                    toast.showBlackbg();
                                }
                            } else if (list.type.equals(RequestParamUtils.grouped)) {
                                if (databaseHelper.getProductFromCartById(list.id + "") != null) {
                                    Intent intent = new Intent(activity, CartActivity.class);
                                    intent.putExtra(RequestParamUtils.buynow, 0);
                                    activity.startActivity(intent);
                                } else {
                                    String groupid = "";
                                    for (int i = 0; i < list.groupedProducts.size(); i++) {
                                        if (groupid.equals("")) {
                                            groupid = groupid + list.groupedProducts.get(i);
                                        } else {
                                            groupid = groupid + "," + list.groupedProducts.get(i);
                                        }
                                    }
                                    getGroupProducts(groupid);
                                }
                            }
                        } else {
                            toast = new CustomToast(activity);
                            toast.showToast(activity.getString(R.string.out_of_stock));
                            toast.showBlackbg();
                        }
                    }
                });
            }
        } else {
            tvAddToCart.setVisibility(View.GONE);

        }

    }

    public void showGroupProduct(List<CategoryList> groupProductList) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.group_product_view, null);
        dialogBuilder.setView(dialogView);

        RecyclerView rvGroupProduct = dialogView.findViewById(R.id.rvGroupProduct);
        GroupProductAdapter groupProductAdapter = new GroupProductAdapter(activity, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvGroupProduct.setLayoutManager(mLayoutManager);
        rvGroupProduct.setAdapter(groupProductAdapter);
        groupProductAdapter.addAll(groupProductList);
        rvGroupProduct.setNestedScrollingEnabled(false);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
    }


    public void callApi() {

        ((BaseActivity) activity).showProgress("");
        if (VariationPage == 1) {
            variationList = new ArrayList<>();
        }
        GetApi getApi = new GetApi(activity, "getVariation_", this, ((BaseActivity) activity).getlanuage());
        getApi.callGetApi(new URLS().WOO_MAIN_URL + new URLS().WOO_PRODUCT_URL + "/" + list.id + "/" + new URLS().WOO_VARIATION + "?page=" + VariationPage);

    }

    public void showDialog() {
        RecyclerView rvProductVariation;
        ProductVariationAdapter productVariationAdapter;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);

        rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);

        productVariationAdapter = new ProductVariationAdapter(activity, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvProductVariation.setLayoutManager(mLayoutManager);
        rvProductVariation.setAdapter(productVariationAdapter);
        rvProductVariation.setNestedScrollingEnabled(false);
        productVariationAdapter.addAll(list.attributes);
        productVariationAdapter.addAllVariationList(variationList);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        alertDialog.show();
        tvCancel.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvDone.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
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
                if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, list.attributes)) {
                    toast = new CustomToast(activity);
                    toast.showToast(activity.getString(R.string.combition_doesnt_exist));
                } else {
                    toast.cancelToast();
                    alertDialog.dismiss();
                    if (databaseHelper.getVariationProductFromCart(getCartVariationProduct())) {
                        //tvCart.setText(getResources().getString(R.string.go_to_cart));
                    } else {
                        //tvCart.setText(getResources().getString(R.string.add_to_Cart));
                    }
                    //changePrice();
                    if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, list.attributes)) {
                        toast = new CustomToast(activity);
                        toast.showToast(activity.getString(R.string.variation_not_available));
                        toast.showRedbg();
                    } else {
                        if (getCartVariationProduct() != null) {
                            Cart cart = getCartVariationProduct();
                            if (databaseHelper.getVariationProductFromCart(cart)) {
                                Intent intent = new Intent(activity, CartActivity.class);
                                intent.putExtra("buynow", 0);
                                activity.startActivity(intent);
                            } else {
                                databaseHelper.addVariationProductToCart(getCartVariationProduct());
                                ((BaseActivity) activity).showCart();
//                                toast.showBlackbg();
                                toast = new CustomToast(activity);
                                toast.showToast(activity.getString(R.string.item_added_to_your_cart));
                            }
                        } else {
                            toast = new CustomToast(activity);
                            toast.showRedbg();
                            toast.showToast(activity.getString(R.string.variation_not_available));
                        }
                    }
                }
            }
        });
        alertDialog.show();
    }

    public void getDefaultVariationId() {
        Log.e("default variation id ", "called");
        List<String> list = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < ProductDetailActivity.combination.size(); i++) {
                String value = ProductDetailActivity.combination.get(i);
                String[] valuearray = new String[0];
                if (value.contains("->")) {
                    valuearray = value.split("->");
                }
                if (valuearray.length > 0) {
                    object.put(valuearray[0], valuearray[1]);
                }
                list.add(ProductDetailActivity.combination.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        defaultVariationId = new CheckIsVariationAvailable().getVariationid(variationList, list);
        CategoryList.Image image = new CategoryList().getImageInstance();
        image.src = CheckIsVariationAvailable.imageSrc;
    }

    public Cart getCartVariationProduct() {
        Log.e("getCartVariation", "called");
        List<String> lists = new ArrayList<>();
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < ProductDetailActivity.combination.size(); i++) {
                String value = ProductDetailActivity.combination.get(i);
                String[] valuearray = new String[0];
                if (value.contains("->")) {
                    valuearray = value.split("->");
                }
                if (valuearray.length > 0) {
                    object.put(valuearray[0], valuearray[1]);
                }
                lists.add(ProductDetailActivity.combination.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Cart cart = new Cart();
        cart.setQuantity(1);
        cart.setVariation(object.toString());
        list.priceHtml = CheckIsVariationAvailable.pricehtml;
        list.price = CheckIsVariationAvailable.price + "";


        //list.images.set(0,"")
        if (CheckIsVariationAvailable.imageSrc != null && !CheckIsVariationAvailable.imageSrc.contains(RequestParamUtils.placeholder)) {
            list.appthumbnail = CheckIsVariationAvailable.imageSrc;
        }
        if (!list.manageStock) {
            list.manageStock = CheckIsVariationAvailable.isManageStock;
        }
        list.images.clear();

        cart.setVariationid(new CheckIsVariationAvailable().getVariationid(variationList, lists));
        cart.setProductid(list.id + "");
        cart.setBuyNow(0);
        cart.setManageStock(list.manageStock);
        cart.setStockQuantity(CheckIsVariationAvailable.stockQuantity);
        cart.setProduct(new Gson().toJson(list));
        cart.setCspPrice(new Gson().toJson(list.cspPrices));

        if (cart.getVariationid() != defaultVariationId) {

            CategoryList.Image image = new CategoryList().getImageInstance();
            image.src = CheckIsVariationAvailable.imageSrc;
            list.images.add(image);

        } else {
            CategoryList.Image image = new CategoryList().getImageInstance();
            image.src = CheckIsVariationAvailable.imageSrc;
            list.images.add(image);

        }
        cart.setProduct(new Gson().toJson(list));
        cart.setProduct(new Gson().toJson(list.cspPrices));
        return cart;

    }

    public void getGroupProducts(String groupid) {
        if (Utils.isInternetConnected(activity)) {
            ((BaseActivity) activity).showProgress("");
            PostApi postApi = new PostApi(activity, RequestParamUtils.getGroupProducts, this, ((BaseActivity) activity).getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.INCLUDE, groupid);
                jsonObject.put(RequestParamUtils.PAGE, page);
                jsonObject.put(RequestParamUtils.USER_ID, ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.ID,"-"));
                postApi.callPostApi(new URLS().PRODUCT_URL, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.contains("getVariation_")) {
            ((BaseActivity) activity).dismissProgress();
            String currentString = methodName;
            Log.e(TAG, "onResponse: " + response);

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
                        callApi();
                    } else {
                        showDialog();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                if (jsonArray == null || jsonArray.length() != 10) {
                    getDefaultVariationId();
                }
            }

        } else if (methodName.equals(RequestParamUtils.getGroupProducts)) {
            ((BaseActivity) activity).dismissProgress();
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


//                        if (categoryListRider.type.equals(RequestParamUtils.simple)) {
//                            JSONObject object = new JSONObject();
//                            try {
//                                for (int j = 0; j < categoryListRider.attributes.size(); j++) {
//                                    object.put(categoryListRider.attributes.get(j).name, categoryListRider.attributes.get(j).options.get(0));
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            Cart cart = new Cart();
//                            cart.setQuantity(1);
//                            cart.setVariation(object.toString());
//                            cart.setProduct(new Gson().toJson(categoryListRider));
//                            cart.setVariationid(0);
//                            cart.setProductid(categoryListRider.id + "");
//                            cart.setBuyNow(0);
//                            cart.setManageStock(categoryListRider.manageStock);
//                            cart.setStockQuantity(categoryListRider.stockQuantity);
//                            if (databaseHelper.getProductFromCartById(categoryListRider.id + "") != null) {
//                                databaseHelper.addToCart(cart);
//                                if (i == jsonArray.length() - 1) {
//                                    Intent intent = new Intent(activity, CartActivity.class);
//                                    intent.putExtra(RequestParamUtils.buynow, 0);
//                                    activity.startActivity(intent);
//                                }
//
//                            } else {
//                                databaseHelper.addToCart(cart);
//                                ((BaseActivity) activity).showCart();
//                                toast.showToast(activity.getString(R.string.item_added_to_your_cart));
//                                toast.showBlackbg();
//                                if (tvAddToCart != null) {
//                                    tvAddToCart.setText(activity.getResources().getString(R.string.go_to_cart));
//                                }
//                            }
//
//
//                        }
                    }
                    showGroupProduct(categoryLists);

                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onItemClick(int position, String value, int outerpos) {
        Log.e(TAG, "On Item Click");
        Drawable unwrappedDrawable = tvAddToCart.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (outerpos == 11459060) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            for (int i = 0; i < list.groupedProducts.size(); i++) {
                if (databaseHelper.getProductFromCartById(list.groupedProducts.get(i) + "") != null) {
                    DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
                } else {
                    DrawableCompat.setTint(wrappedDrawable, (Color.parseColor("#333333")));
                    break;
                }
            }
        } else {
            if (getCartVariationProduct() != null) {
                Cart cart = getCartVariationProduct();

                if (databaseHelper.getVariationProductFromCart(cart)) {
                    tvDone.setText(activity.getString(R.string.go_to_cart));
                } else {
                    tvDone.setText(activity.getString(R.string.done));
                }
            } else {
                tvDone.setText(activity.getString(R.string.done));
            }
        }


    }

}

