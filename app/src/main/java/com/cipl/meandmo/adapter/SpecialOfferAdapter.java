package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.ProductDetailActivity;
import com.cipl.meandmo.customview.roundedimageview.RoundedTransformationBuilder;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.helper.DatabaseHelper;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.javaclasses.AddToCartVariation;
import com.cipl.meandmo.model.CategoryList;
import com.cipl.meandmo.model.Home;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.SpecialOfferViewHolder> implements OnResponseListner {

    private List<Home.Product> list = new ArrayList<>();
    private final Transformation mTransformation;
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private DatabaseHelper databaseHelper;

    public SpecialOfferAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        mTransformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
        databaseHelper = new DatabaseHelper(activity);
        notifyDataSetChanged();

    }

    public void addAll(List<Home.Product> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    public List<Home.Product> getList() {
        return list;
    }


    @Override
    public SpecialOfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_special_offer, parent, false);

        return new SpecialOfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SpecialOfferViewHolder holder, final int position) {
        holder.llMain.getLayoutParams().width = width - (width / 3);
        //Add product in cart if add to cart enable from admin panel
        new AddToCartVariation(activity).addToCart(holder.ivAddToCart, new Gson().toJson(list.get(position)), holder.ll_controller);
        Drawable tvIncrement = holder.tvIncrement.getBackground();
        Drawable rappedDrawable = DrawableCompat.wrap(tvIncrement);
        DrawableCompat.setTint(rappedDrawable, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));


        Drawable tvDecrement = holder.tvDecrement.getBackground();
        Drawable rappedDrawabledes = DrawableCompat.wrap(tvDecrement);
        DrawableCompat.setTint(rappedDrawabledes, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductDetail(list.get(position).id);
            }
        });

        if (list.get(position).image != null) {
            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(list.get(position).image)
                    .error(R.drawable.no_image_available)
                    .fit()
                    .transform(mTransformation)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).title + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).title + ""));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));

        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
        }
        int per = (int) list.get(position).percentage;
        holder.tvOff.setText("" + per + "% off");
        holder.tvPrice.setTextSize(15);

        Drawable unwrappedDrawable = holder.tvOff.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);

        holder.tvProductQuantity.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
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
                    if (quntity > Integer.parseInt(String.valueOf(Math.round((Double) list.get(position).stockQuantity)))) {
                        Toast.makeText(activity, ((BaseActivity) activity).getString(R.string.only) + " " + list.get(position).stockQuantity + " " + ((BaseActivity) activity).getString(R.string.quntity_is_avilable), Toast.LENGTH_SHORT).show();
                    } else {
                        holder.tvProductQuantity.setText(quntity + "");
                        databaseHelper.updateQuantity(quntity, list.get(position).id, String.valueOf(databaseHelper.getProductvarationidFromCartById(list.get(position).id)));
                        list.get(position).setQuantity(quntity);

                        holder.tvProductQuantity.setText(databaseHelper.getProductQuantity(list.get(position).id));
                        // onItemClickListner.onItemClick(position, RequestParamUtils.increment, quntity);
                    }
                } else {
                    holder.tvProductQuantity.setText(quntity + "");

                    Log.e("TAG", "onClick: " + databaseHelper.getProductvarationidFromCartById(list.get(position).id));
                    databaseHelper.updateQuantity(quntity, list.get(position).id, String.valueOf(databaseHelper.getProductvarationidFromCartById(list.get(position).id)));
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
                databaseHelper.updateQuantity(quntity, list.get(position).id, String.valueOf(databaseHelper.getProductvarationidFromCartById(list.get(position).id)));
                list.get(position).setQuantity(quntity);

                holder.tvProductQuantity.setText(databaseHelper.getProductQuantity(list.get(position).id));
                // onItemClickListner.onItemClick(position, RequestParamUtils.decrement, quntity);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (list.size() > 4) {
            return 4;
        }
        return list.size();
    }

    public void getWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void getProductDetail(String groupid) {
        if (Utils.isInternetConnected(activity)) {
            ((BaseActivity) activity).showProgress("");
            PostApi postApi = new PostApi(activity, RequestParamUtils.getProductDetail, this, ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.LANGUAGE, ""));
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.INCLUDE, groupid);
                jsonObject.put(RequestParamUtils.USER_ID, ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.ID,"-"));
                postApi.callPostApi(new URLS().PRODUCT_URL + ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.equals(RequestParamUtils.getProductDetail)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    CategoryList categoryListRider = new Gson().fromJson(
                            jsonArray.get(0).toString(), new TypeToken<CategoryList>() {
                            }.getType());
                    Constant.CATEGORYDETAIL = categoryListRider;
                    if (categoryListRider.type.equals(RequestParamUtils.external)) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                        activity.startActivity(browserIntent);
                    } else {
                        Intent intent = new Intent(activity, ProductDetailActivity.class);
                        activity.startActivity(intent);
                    }


                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                ((BaseActivity) activity).dismissProgress();
            }
        }
    }

    public class SpecialOfferViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvName)
        TextViewBold tvName;

        @BindView(R.id.tvOff)
        TextViewRegular tvOff;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.ivAddToCart)
        ImageView ivAddToCart;

        @BindView(R.id.tvDecrement)
        ImageView tvDecrement;

        @BindView(R.id.tvProductQuantity)
        TextView tvProductQuantity;

        @BindView(R.id.tvIncrement)
        ImageView tvIncrement;

        @BindView(R.id.ll_controller)
        LinearLayout ll_controller;

        public SpecialOfferViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}