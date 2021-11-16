package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.MaterialRatingBar;
import com.cipl.meandmo.customview.swipeview.ViewBinderHelper;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewMedium;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Cart;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nirav Shah on 27/11/2018.
 */

public class CartPaymentAdapter extends RecyclerView.Adapter {

    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    String value;

    private List<Cart> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private DatabaseHelper databaseHelper;
    private int isBuynow = 0;


    public CartPaymentAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
        binderHelper.setOpenOnlyOne(true);
    }

    public void addAll(List<Cart> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    public void isFromBuyNow(int isBuynow) {
        this.isBuynow = isBuynow;
    }

    public List<Cart> getList() {
        return list;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, final int position) {
        final CartViewHolder holder = (CartViewHolder) h;

        holder.llController.setVisibility(View.GONE);
        holder.llDelete.setVisibility(View.GONE);

        if (list != null && 0 <= position && position < list.size()) {
            // bindview start
            final String data = list.get(position).getCartId() + "";

            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            binderHelper.closeLayout(position + "");
            if (!list.get(position).getCategoryList().averageRating.equals("")) {
                holder.ratingBar.setRating(Float.parseFloat(list.get(position).getCategoryList().averageRating));
            } else {
                holder.ratingBar.setRating(0);
            }
            if (list.get(position).getCategoryList().images.size() > 0) {
                holder.ivImage.setVisibility(View.VISIBLE);
                Picasso.get().load(list.get(position).getCategoryList().appthumbnail).into(holder.ivImage);
            } else {
                holder.ivImage.setVisibility(View.INVISIBLE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvProductName.setText(categoryList.name + "");
                holder.tvName.setText(Html.fromHtml(list.get(position).getCategoryList().name + "", Html.FROM_HTML_MODE_LEGACY));
            } else {
//            tvProductName.setText(categoryList.name + "");
                holder.tvName.setText(Html.fromHtml(list.get(position).getCategoryList().name + ""));
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).getCategoryList().priceHtml, Html.FROM_HTML_MODE_COMPACT));

            } else {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).getCategoryList().priceHtml));
            }
            holder.tvPrice.setTextSize(15);

            ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).getCategoryList().priceHtml);

            holder.tvQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            holder.tvQuantity.setText(list.get(position).getQuantity() + "");


            try {
                JSONObject jObject = new JSONObject(list.get(position).getVariation());
                Iterator iter = jObject.keys();
                value = "";
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    if (value.length() == 0) {
                        value = value + key + " : " + jObject.getString(key);
                    } else {
                        value = value + ", " + key + " : " + jObject.getString(key);
                    }
                }
            } catch (Exception e) {
                Log.e("exception is ", e.getMessage());
            }
            holder.txtVariation.setText(value + "");
        }


    }

    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 20;
        height = width - height_value;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.tvName)
        TextViewMedium tvName;
        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;
        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;
        @BindView(R.id.txtVariation)
        TextViewLight txtVariation;
        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;
        @BindView(R.id.tvDecrement)
        ImageView tvDecrement;
        @BindView(R.id.tvQuantity)
        TextViewBold tvQuantity;
        @BindView(R.id.tvIncrement)
        ImageView tvIncrement;
        @BindView(R.id.ll_controller)
        LinearLayout llController;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.tvDelete)
        TextViewRegular tvDelete;
        @BindView(R.id.ll_Delete)
        LinearLayout llDelete;
        @BindView(R.id.llMain)
        LinearLayout llMain;
        @BindView(R.id.swipe_layout)
        LinearLayout swipeLayout;

        public CartViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}