package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.ProductDetailActivity;
import com.cipl.meandmo.customview.MaterialRatingBar;
import com.cipl.meandmo.customview.roundedimageview.RoundedTransformationBuilder;
import com.cipl.meandmo.customview.swipeview.ViewBinderHelper;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewMedium;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Cart;
import com.cipl.meandmo.model.CspPrice;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class CartAdapter extends RecyclerView.Adapter {


    private List<Cart> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private DatabaseHelper databaseHelper;
    String value;
    private int isBuynow = 0;
    private final Transformation mTransformation;
    private List<CspPrice> listCspPrice = new ArrayList<>();
    CspPrice cspPrice;

    public CartAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
        binderHelper.setOpenOnlyOne(true);
        mTransformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
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
        listCspPrice.clear();
        Log.e("TAG", "onBindViewHolderDhruv: " + list.get(position).getCspPrice());
        /* cspPrice = new Gson().fromJson(list.get(position).getCspPrice(),new TypeToken<CspPrice>() {}.getType());*/
        listCspPrice = new Gson().fromJson("[{\"min_qty\":\"1\",\"price\":\"90.00\",\"price_type\":\"1\",\"role\":\"retail-customer\"},\n" +
                "\t\t{\"min_qty\":\"2\",\"price\":\"50.00\",\"price_type\":\"1\",\"role\":\"retail-customer\"},\n" +
                "\t\t{\"min_qty\":\"3\",\"price\":\"20.00\",\"price_type\":\"1\",\"role\":\"retail-customer\"}]", new TypeToken<ArrayList<CspPrice>>() {
        }.getType());
        listCspPrice.add(new CspPrice("","100","0",""));

        final CartViewHolder holder = (CartViewHolder) h;
        if (list != null && 0 <= position && position < list.size()) {
            // bindview start
            final String data = list.get(position).getCartId() + "";

            Drawable tvIncrement = holder.tvIncrement.getBackground();
            Drawable rappedDrawable = DrawableCompat.wrap(tvIncrement);
            DrawableCompat.setTint(rappedDrawable, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));


            Drawable tvDecrement = holder.tvDecrement.getBackground();
            Drawable rappedDrawabledes = DrawableCompat.wrap(tvDecrement);
            DrawableCompat.setTint(rappedDrawabledes, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));


            //    holder.ivDelete.setColorFilter(activity.getResources().getColor(R.color.red));


            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            holder.tvPrice.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            holder.tvPrice1.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            holder.txtVariation.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
            holder.tvQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

            binderHelper.closeLayout(position + "");
            if (!list.get(position).getCategoryList().averageRating.equals("")) {
                holder.ratingBar.setRating(Float.parseFloat(list.get(position).getCategoryList().averageRating));
            } else {
                holder.ratingBar.setRating(0);
            }
            if (list.get(position).getCategoryList().images.size() > 0) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get().load(list.get(position).getCategoryList().appthumbnail)
                        .error(R.drawable.no_image_available)
                        .fit()
                        .transform(mTransformation)
                        .into(holder.ivImage);
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


            if (listCspPrice.isEmpty()) {
                Log.e("TAG", "onBindViewHolder:1 ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.tvPrice.setText(Html.fromHtml(list.get(position).getCategoryList().priceHtml, Html.FROM_HTML_MODE_COMPACT));

                } else {
                    holder.tvPrice.setText(Html.fromHtml(list.get(position).getCategoryList().priceHtml));
                }

                ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).getCategoryList().priceHtml);

            } else {
                for (int i = 0; i < listCspPrice.size(); i++) {
                    if(i < listCspPrice.size()-1) {
                    Log.e("TAG", "Monil : " + list.get(position).getQuantity() +" ----"+ Integer.parseInt(listCspPrice.get(i).minQty) );

                    Log.e("TAG", "Monil : " + list.get(position).getQuantity() +" ----"+ Integer.parseInt(listCspPrice.get(i+1).minQty) );


                        if (list.get(position).getQuantity() > Integer.parseInt(listCspPrice.get(i).minQty) && list.get(position).getQuantity() <= Integer.parseInt(listCspPrice.get(i + 1).minQty)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                holder.tvPrice.setText(Html.fromHtml(list.get(position).getCategoryList().priceHtml.replace(list.get(position).getCategoryList().price, listCspPrice.get(i).price), Html.FROM_HTML_MODE_COMPACT));

                            } else {
                                holder.tvPrice.setText(Html.fromHtml(list.get(position).getCategoryList().priceHtml.replace(list.get(position).getCategoryList().price, listCspPrice.get(i).price)));
                            }
                            ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).getCategoryList().priceHtml.replace(list.get(position).getCategoryList().salePrice, listCspPrice.get(i).price));
                            Log.e("TAG", "Monil : " + listCspPrice.get(i).price);
                            break;

                        } else {
                            Log.e("TAG", "Monil : Not");
                        }
                    }


                }
            }

            holder.tvPrice.setTextSize(15);


            // holder.tvQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            holder.tvQuantity.setText(list.get(position).getQuantity() + "");

            holder.tvIncrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quntity = Integer.parseInt(holder.tvQuantity.getText().toString());
                    quntity = quntity + 1;

                    if (list.get(position).isManageStock()) {
                        if (quntity > list.get(position).getCategoryList().stockQuantity) {
                            Toast.makeText(activity, ((BaseActivity) activity).getString(R.string.only) + " " + list.get(position).getCategoryList().stockQuantity + " " + ((BaseActivity) activity).getString(R.string.quntity_is_avilable), Toast.LENGTH_SHORT).show();
                        } else {

                            holder.tvQuantity.setText(quntity + "");
                            databaseHelper.updateQuantity(quntity, list.get(position).getProductid(), list.get(position).getVariationid() + "");
                            list.get(position).setQuantity(quntity);
                            onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                        }
                    } else {
                        holder.tvQuantity.setText(quntity + "");
                        databaseHelper.updateQuantity(quntity, list.get(position).getProductid(), list.get(position).getVariationid() + "");
                        list.get(position).setQuantity(quntity);
                        onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                    }
                    notifyItemChanged(position);
                }
            });

            holder.tvDecrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quntity = Integer.parseInt(holder.tvQuantity.getText().toString());
                    quntity = quntity - 1;
                    if (quntity < 1) {
                        quntity = 1;
                    }
                    holder.tvQuantity.setText(quntity + "");
                    databaseHelper.updateQuantity(quntity, list.get(position).getProductid(), list.get(position).getVariationid() + "");
                    list.get(position).setQuantity(quntity);
                    onItemClickListner.onItemClick(position, RequestParamUtils.decrement, quntity);
                    notifyItemChanged(position);
                }
            });

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isBuynow == 0) {
                        if (list.get(position).getCategoryList().type.equals(RequestParamUtils.external)) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getCategoryList().externalUrl));
                            activity.startActivity(browserIntent);
                        } else {
                            Constant.CATEGORYDETAIL = list.get(position).getCategoryList();
                            Intent intent = new Intent(activity, ProductDetailActivity.class);
                            intent.putExtra(RequestParamUtils.ID, list.get(position).getCategoryList().id);
                            activity.startActivity(intent);
                        }

                    }

                }
            });

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

            if (value != null && !value.isEmpty()) {
                holder.txtVariation.setVisibility(View.VISIBLE);
                holder.txtVariation.setText(value + "");
            } else {
                holder.txtVariation.setVisibility(View.GONE);
            }


            holder.llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getCategoryList().type.equals(RequestParamUtils.variable)) {
                        databaseHelper.deleteVariationProductFromCart(list.get(position).getProductid(), list.get(position).getVariationid() + "");
                    } else {
                        databaseHelper.deleteFromCart(list.get(position).getProductid());
                    }
                    list.remove(position);
                    onItemClickListner.onItemClick(position, RequestParamUtils.delete, 0);
                    notifyDataSetChanged();
                }
            });
            //bind view over
        }

    }

    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link Activity #onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    @Override
    public int getItemCount() {
        return list.size();
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

        @BindView(R.id.ll_Delete)
        LinearLayout llDelete;

        @BindView(R.id.llMain)
        LinearLayout llMain;


        public CartViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
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
}