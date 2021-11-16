package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.ProductDetailActivity;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.javaclasses.CheckIsVariationAvailable;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.Variation;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.CustomToast;
import com.cipl.meandmo.utils.RequestParamUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class ProductColorAdapter extends RecyclerView.Adapter<ProductColorAdapter.ProductColorViewHolder>  {

    public static int selectedpos;
    AlertDialog alertDialog;
    ProductVariationAdapter productVariationAdapter;
    private List<String> list = new ArrayList<>();
    private List<CategoryList.Attribute> dialogList;
    private List<Variation> variationList = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private CustomToast toast;
    private String type;

    public ProductColorAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        toast = new CustomToast(activity);
    }

    public void setType(String type) {
        this.type = type;
    }


    public void addAll(List<String> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    public void addAllVariationList(List<Variation> variationList, ProductVariationAdapter productVariationAdapter) {
        this.variationList = variationList;
        this.productVariationAdapter = productVariationAdapter;
        showDialog(productVariationAdapter);
        notifyDataSetChanged();
    }

    public void getDialogList(List<CategoryList.Attribute> dialogList) {
        this.dialogList = dialogList;
    }


    @Override
    public ProductColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_color, parent, false);
        return new ProductColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductColorViewHolder holder, final int position) {
        holder.llMain.getLayoutParams().height = width;
        holder.llMain.getLayoutParams().width = width;

        GradientDrawable gd = (GradientDrawable) holder.flTransparent.getBackground();
        gd.setColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_TRANSPARENT, Constant.PRIMARY_COLOR)));
        if (position == selectedpos) {
            holder.flTransparent.setVisibility(View.VISIBLE);
        } else {
            holder.flTransparent.setVisibility(View.GONE);
        }
        holder.llMain.setBackgroundColor(Color.WHITE);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null) {
                    if (type.equals(RequestParamUtils.variable)) {
                        alertDialog.show();
                        productVariationAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        holder.tvName.setText(list.get(position));
        holder.tvName.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_TRANSPARENT, Constant.PRIMARY_COLOR)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void getWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 8;
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void showDialog(final ProductVariationAdapter productVariationAdapter) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);
        RecyclerView rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvProductVariation.setLayoutManager(mLayoutManager);
        rvProductVariation.setAdapter(productVariationAdapter);
        rvProductVariation.setNestedScrollingEnabled(false);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        alertDialog.show();
        tvCancel.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvDone.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
//                showDialog(productVariationAdapter);
//                selectedpos = pos;
                // notifyDataSetChanged();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, dialogList)) {
                    toast.showToast(activity.getResources().getString(R.string.combition));
                } else {
                    onItemClickListner.onItemClick(new CheckIsVariationAvailable().getVariationid(variationList, list), "", 0);
                    toast.cancelToast();
                    alertDialog.dismiss();
                    notifyDataSetChanged();
                }
            }
        });
    }

    public class ProductColorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        FrameLayout llMain;

        @BindView(R.id.flTransparent)
        FrameLayout flTransparent;

        @BindView(R.id.tvName)
        TextViewRegular tvName;


        public ProductColorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}