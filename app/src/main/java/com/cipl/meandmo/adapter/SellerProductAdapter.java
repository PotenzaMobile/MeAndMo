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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.google.gson.Gson;
import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.ProductDetailActivity;
import com.cipl.meandmo.customview.MaterialRatingBar;
import com.cipl.meandmo.customview.like.animation.SparkButton;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.javaclasses.AddToCartVariation;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.WishList;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.CategoryGridHolder> {


    private static final String TAG = "SellerProductAdapter";
    AlertDialog alertDialog;

    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private DatabaseHelper databaseHelper;


    public SellerProductAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
    }


    public void addAll(List<CategoryList> list) {

        for (CategoryList item : list) {
            add(item);
        }
//        this.list = list;
//        notifyDataSetChanged();
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
    public CategoryGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_category, parent, false);

        return new CategoryGridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryGridHolder holder, final int position) {
        holder.llSale.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClickProduct(position);
            }
        });

        if (!list.get(position).type.contains(RequestParamUtils.variable) && list.get(position).onSale == true) {
            ((BaseActivity) activity).showDiscount(holder.tvDiscount, list.get(position).salePrice, list.get(position).regularPrice);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }
        Drawable unwrappedDrawable = holder.tvDecrement.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        Drawable unwrappedDrawable1 = holder.tvIncrement.getBackground();
        Drawable wrappedDrawable1 = DrawableCompat.wrap(unwrappedDrawable1);
        DrawableCompat.setTint(wrappedDrawable1, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        holder.tvProductQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        //Add product in cart if add to cart enable from admin panel
        new AddToCartVariation(activity).addToCart(holder.ivAddToCart, new Gson().toJson(list.get(position)), holder.ll_controller);
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

            Glide.with(activity)
                    .asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.no_image_available)
                    .load(list.get(position).appthumbnail)
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


        holder.ivWishList.setActivetint(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
        holder.ivWishList.setColors(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)), Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));


        if (Constant.IS_WISH_LIST_ACTIVE) {
            holder.ivWishList.setVisibility(View.VISIBLE);

            if (databaseHelper.getWishlistProduct(list.get(position).id + "")) {
                holder.ivWishList.setChecked(true);
            } else {
                holder.ivWishList.setChecked(false);
            }
        } else {
            holder.ivWishList.setVisibility(View.GONE);
        }


        holder.ivWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.getWishlistProduct(list.get(position).id + "")) {
                    holder.ivWishList.setChecked(false);
                    onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.delete, 0);
                    databaseHelper.deleteFromWishList(list.get(position).id + "");
                } else {
                    holder.ivWishList.setChecked(true);
                    holder.ivWishList.playAnimation();
                    WishList wishList = new WishList();
                    wishList.setProduct(new Gson().toJson(list.get(position)));
                    wishList.setCspPrice(new Gson().toJson(list.get(position).cspPrices));
                    wishList.setProductid(list.get(position).id + "");
                    databaseHelper.addToWishList(wishList);
                    onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.insert, 0);

                    String value = holder.tvPrice1.getText().toString();
                    if (value.contains(Constant.CURRENCYSYMBOL)) {
                        value = value.replaceAll(Constant.CURRENCYSYMBOL, "");
                    }
                    if (value.contains(Constant.CURRENCYSYMBOL)) {
                        value = value.replace(Constant.CURRENCYSYMBOL, "");
                    }
                    value = value.replaceAll("\\s", "");
                    value = value.replaceAll(",", "");
                    try {
                        ((BaseActivity) activity).logAddedToWishlistEvent(String.valueOf(list.get(position).id), list.get(position).name, Constant.CURRENCYSYMBOL, Double.parseDouble(value));
                    } catch (Exception e) {

                    }

                }
            }
        });


        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClickProduct(position);
            }
        });

        ViewTreeObserver vto = holder.ivImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                holder.ivImage.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.e("Height: " + holder.ivImage.getMeasuredHeight(), " Width: " + holder.ivImage.getMeasuredWidth());
                return true;
            }
        });


    }


    @Override
    public void onViewRecycled(CategoryGridHolder holder) {
        super.onViewRecycled(holder);

        Picasso.get()
                .cancelRequest(holder.ivImage);

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

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public class CategoryGridHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvDiscount)
        TextViewRegular tvDiscount;

        @BindView(R.id.ivWishList)
        SparkButton ivWishList;

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.ll_content)
        LinearLayout llContent;

        @BindView(R.id.ivAddToCart)
        ImageView ivAddToCart;

        @BindView(R.id.flImage)
        FrameLayout flImage;

        @BindView(R.id.tvSale)
        TextViewLight tvSale;

        @BindView(R.id.llSale)
        FrameLayout llSale;

        @BindView(R.id.main)
        LinearLayout main;
        @BindView(R.id.tvDecrement)
        ImageView tvDecrement;

        @BindView(R.id.tvProductQuantity)
        TextView tvProductQuantity;

        @BindView(R.id.tvIncrement)
        ImageView tvIncrement;

        @BindView(R.id.ll_controller)
        LinearLayout ll_controller;
        public CategoryGridHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}