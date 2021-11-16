package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.CategoryListActivity;
import com.cipl.meandmo.customview.roundedimageview.RoundedTransformationBuilder;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Home;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


public class VerticalBannerAdapter extends PagerAdapter {
    private List<Home.BannerAd> list = new ArrayList<>();
    private final Transformation mTransformation;
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public VerticalBannerAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        mTransformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    public void addAll(List<Home.BannerAd> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_vertical_banner, container, false);
        container.addView(view);
        LinearLayout llMain = view.findViewById(R.id.llMain);

        ImageView ivBanner = view.findViewById(R.id.ivBanner);
//        llMain.getLayoutParams().height = height;
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CategoryListActivity.class);
                intent.putExtra(RequestParamUtils.CATEGORY, list.get(position).bannerAdCatId);
                intent.putExtra(RequestParamUtils.IS_WISHLIST_ACTIVE, Constant.IS_WISH_LIST_ACTIVE);
                activity.startActivity(intent);
            }
        });

        if (list.get(position).bannerAdImageUrl != null && !list.get(position).bannerAdImageUrl.equals("")) {
            ivBanner.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(list.get(position).bannerAdImageUrl)
                    .error(R.drawable.no_image_available)
                    .fit()
                    .transform(mTransformation)
                    .into(ivBanner);
        } else {
            ivBanner.setBackgroundResource(R.drawable.no_image_available);
        }
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public void getWidthAndHeight() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = width / 2;
    }

}

