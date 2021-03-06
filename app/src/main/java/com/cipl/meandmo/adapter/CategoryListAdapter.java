package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.ciyashop.library.apicall.GetApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.CartActivity;
import com.cipl.meandmo.activity.ProductDetailActivity;
import com.cipl.meandmo.customview.MaterialRatingBar;
import com.cipl.meandmo.customview.like.animation.SparkButton;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.javaclasses.AddToCartVariation;
import com.cipl.meandmo.javaclasses.AddToWishList;
import com.cipl.meandmo.javaclasses.CheckIsVariationAvailable;
import com.cipl.meandmo.model.Cart;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.Variation;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.CustomToast;
import com.cipl.meandmo.utils.RequestParamUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListHolder> implements OnResponseListner, OnItemClickListner {

    private static final String TAG = "CategoryListAdapter";
    AlertDialog alertDialog;

    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private DatabaseHelper databaseHelper;
    private CustomToast toast;
    private boolean isDialogOpen = false;
    private int VariationPage = 1;
    private List<Variation> variationList = new ArrayList<>();
    private int defaultVariationId;


    public CategoryListAdapter(Activity activity) {
        this.activity = activity;
        toast = new CustomToast(activity);
        databaseHelper = new DatabaseHelper(activity);
    }

    public void addAll(List<CategoryList> list) {
        for (CategoryList item : list) {
            add(item);
        }
    }

    public void add(CategoryList item) {
        this.list.add(item);
        if (list.size() > 1) {
            notifyItemInserted(list.size() - 1);
        } else {
            notifyDataSetChanged();
        }
    }

    public void newList() {
        this.list = new ArrayList<>();
    }

    @Override
    public CategoryListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_category, parent, false);
        return new CategoryListHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final CategoryListHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickProduct(position);
            }
        });

        Drawable unwrappedDrawable = holder.tvDecrement.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        Drawable unwrappedDrawable1 = holder.tvIncrement.getBackground();
        Drawable wrappedDrawable1 = DrawableCompat.wrap(unwrappedDrawable1);
        DrawableCompat.setTint(wrappedDrawable1, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        holder.tvProductQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        //Add product in cart if add to cart enable from admin panel
        new AddToCartVariation(activity).addToCart(holder.ivCart, new Gson().toJson(list.get(position)), holder.ll_controller);
//
        //Add product in wishlist and remove product from wishlist and check wishlist enable or noot
        new AddToWishList(activity).addToWishList(holder.ivWishList, new Gson().toJson(list.get(position)), holder.tvPrice1);


        holder.ivWishList.setActivetint(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
        holder.ivWishList.setColors(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)), Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
        if (databaseHelper.getProductvarationidFromCartById(String.valueOf(list.get(position).id)) != -1) {
            holder.tvProductQuantity.setText(databaseHelper.getProductQuantity(String.valueOf(list.get(position).id)));
        } else {
            holder.tvProductQuantity.setText("1");
        }
        holder.tvIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quntity = Integer.parseInt(holder.tvProductQuantity.getText().toString());
                quntity = quntity + 1;

                if (list.get(position).manageStock) {
                    if (quntity > Integer.parseInt(String.valueOf(Math.round(list.get(position).stockQuantity)))) {
                        Toast.makeText(activity, ((BaseActivity) activity).getString(R.string.only) + " " + list.get(position).stockQuantity + " " + ((BaseActivity) activity).getString(R.string.quntity_is_avilable), Toast.LENGTH_SHORT).show();
                    } else {
                        holder.tvProductQuantity.setText(quntity + "");
                        databaseHelper.updateQuantity(quntity, String.valueOf(list.get(position).id), String.valueOf(databaseHelper.getProductvarationidFromCartById(String.valueOf(list.get(position).id))));
                        list.get(position).setQuantity(quntity);

                        holder.tvProductQuantity.setText(databaseHelper.getProductQuantity(String.valueOf(list.get(position).id)));
                        // onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                    }
                } else {
                    holder.tvProductQuantity.setText(quntity + "");

                    Log.e(TAG, "onClick: " + databaseHelper.getProductvarationidFromCartById(String.valueOf(list.get(position).id)));
                    databaseHelper.updateQuantity(quntity, String.valueOf(list.get(position).id), String.valueOf(databaseHelper.getProductvarationidFromCartById(String.valueOf(list.get(position).id))));
                    list.get(position).setQuantity(quntity);

                    holder.tvProductQuantity.setText(databaseHelper.getProductQuantity(String.valueOf(list.get(position).id)));
                    // onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                }
            }
        });

        holder.tvDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quntity = Integer.parseInt(holder.tvProductQuantity.getText().toString());
                quntity = quntity - 1;
                if (quntity < 1) {
                    quntity = 1;
                }
                holder.tvProductQuantity.setText(quntity + "");
                databaseHelper.updateQuantity(quntity, String.valueOf(list.get(position).id), String.valueOf(databaseHelper.getProductvarationidFromCartById(String.valueOf(list.get(position).id))));
                list.get(position).setQuantity(quntity);

                holder.tvProductQuantity.setText(databaseHelper.getProductQuantity(String.valueOf(list.get(position).id)));
                // onItemClickListner.onItemClick(position, RequestParamUtils.decrement, quntity);
            }
        });
        if (!list.get(position).averageRating.equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }
        if (list.get(position).appthumbnail != null) {

            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(activity)
                    .asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
                    .placeholder(R.drawable.placeholder)
                    .load(list.get(position).appthumbnail)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);

        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).name, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).name));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
        }
        holder.tvPrice.setTextSize(15);
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);

        ViewTreeObserver vto = holder.ivImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.ivImage.getViewTreeObserver().removeOnPreDrawListener(this);
//                Log.e("Height: " + holder.ivImage.getMeasuredHeight(), " Width: " + holder.ivImage.getMeasuredWidth());
                return true;
            }
        });

        if (!list.get(position).type.contains(RequestParamUtils.variable) && list.get(position).onSale == true) {
            ((BaseActivity) activity).showDiscount(holder.tvDiscount, list.get(position).salePrice, list.get(position).regularPrice);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }
    }

    public void ClickProduct(int position) {
        if (list.get(position).type.equals(RequestParamUtils.external)) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
            activity.startActivity(browserIntent);
        } else {
            Constant.CATEGORYDETAIL = list.get(position);
            Intent intent = new Intent(activity, ProductDetailActivity.class);
            activity.startActivity(intent);
        }
    }

    public void callApi(int position) {

        ((BaseActivity) activity).showProgress("");
        if (VariationPage == 1) {
            variationList = new ArrayList<>();
        }
        GetApi getApi = new GetApi(activity, "getVariation_" + position, this, ((BaseActivity) activity).getlanuage());
        getApi.callGetApi(new URLS().WOO_MAIN_URL + new URLS().WOO_PRODUCT_URL + "/" + list.get(position).id + "/" + new URLS().WOO_VARIATION + "?page=" + VariationPage);

    }

    @Override
    public void onResponse(String response, String methodName) {
        ((BaseActivity) activity).dismissProgress();

        String currentString = methodName;
        String[] separated = currentString.split("_");
        String str = separated[0];
        String strPositon = separated[1];
        int positions = Integer.parseInt(strPositon);

        Log.e(TAG, "onResponse: " + positions);

        if (methodName.contains("getVariation_")) {

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
                        callApi(positions);
                    } else {
                        showDialog(positions);
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                if (jsonArray == null || jsonArray.length() != 10) {
                    getDefaultVariationId();
                }
            }

        }
    }


    public void showDialog(final int position) {
        RecyclerView rvProductVariation;
        ProductVariationAdapter productVariationAdapter;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);

        rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);

        productVariationAdapter = new ProductVariationAdapter(activity, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvProductVariation.setLayoutManager(mLayoutManager);
        rvProductVariation.setAdapter(productVariationAdapter);
        rvProductVariation.setNestedScrollingEnabled(false);
        productVariationAdapter.addAll(list.get(position).attributes);
        productVariationAdapter.addAllVariationList(variationList);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        alertDialog.show();
        tvCancel.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
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
                if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, list.get(position).attributes)) {
                    toast.showToast(activity.getString(R.string.combition_doesnt_exist));
                } else {
                    toast.cancelToast();
                    alertDialog.dismiss();
                    if (databaseHelper.getVariationProductFromCart(getCartVariationProduct(position))) {
                        //tvCart.setText(getResources().getString(R.string.go_to_cart));
                    } else {
                        //tvCart.setText(getResources().getString(R.string.add_to_Cart));
                    }
                    //changePrice();

                    if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, list.get(position).attributes)) {
                        toast.showToast(activity.getString(R.string.variation_not_available));
                        toast.showRedbg();
                    } else {
                        if (getCartVariationProduct(position) != null) {
                            Cart cart = getCartVariationProduct(position);

                            if (databaseHelper.getVariationProductFromCart(cart)) {
                                Intent intent = new Intent(activity, CartActivity.class);
                                intent.putExtra("buynow", 0);
                                activity.startActivity(intent);
                            } else {
                                databaseHelper.addVariationProductToCart(getCartVariationProduct(position));
                                ((BaseActivity) activity).showCart();
                                toast = new CustomToast(activity);
//                                toast.showBlackbg();
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


    public Cart getCartVariationProduct(int position) {
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
        list.get(position).priceHtml = CheckIsVariationAvailable.pricehtml;
        list.get(position).price = CheckIsVariationAvailable.price + "";

        if (CheckIsVariationAvailable.imageSrc != null && !CheckIsVariationAvailable.imageSrc.contains(RequestParamUtils.placeholder)) {
            list.get(position).appthumbnail = CheckIsVariationAvailable.imageSrc;
        }
        if (!list.get(position).manageStock) {
            list.get(position).manageStock = CheckIsVariationAvailable.isManageStock;
        }

        //list.get(position).images.set(0,"")
        list.get(position).images.clear();

        cart.setVariationid(new CheckIsVariationAvailable().getVariationid(variationList, lists));
        cart.setProductid(list.get(position).id + "");
        cart.setBuyNow(0);
        cart.setManageStock(list.get(position).manageStock);
        cart.setStockQuantity(CheckIsVariationAvailable.stockQuantity);
        cart.setProduct(new Gson().toJson(list.get(position)));
        cart.setCspPrice(new Gson().toJson(list.get(position).cspPrices));

        if (cart.getVariationid() != defaultVariationId) {

            CategoryList.Image image = new CategoryList().getImageInstance();
            image.src = CheckIsVariationAvailable.imageSrc;
            list.get(position).images.add(image);

        } else {
            CategoryList.Image image = new CategoryList().getImageInstance();
            image.src = CheckIsVariationAvailable.imageSrc;
            list.get(position).images.add(image);

        }
        cart.setProduct(new Gson().toJson(list.get(position)));
        cart.setCspPrice(new Gson().toJson(list.get(position).cspPrices));
        return cart;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onItemClick(int position, String value, int outerpos) {

    }

    public class CategoryListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvName)
        TextViewBold tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.tvDiscount)
        TextViewRegular tvDiscount;

        @BindView(R.id.ivWishList)
        SparkButton ivWishList;

        @BindView(R.id.ivCart)
        ImageView ivCart;

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.tvDecrement)
        ImageView tvDecrement;

        @BindView(R.id.tvProductQuantity)
        TextView tvProductQuantity;

        @BindView(R.id.tvIncrement)
        ImageView tvIncrement;

        @BindView(R.id.ll_controller)
        LinearLayout ll_controller;

        public CategoryListHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}