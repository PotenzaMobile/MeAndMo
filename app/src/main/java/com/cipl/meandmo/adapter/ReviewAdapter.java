package com.cipl.meandmo.adapter;

import android.app.Activity;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.ProductReview;
import com.cipl.meandmo.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private List<ProductReview> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;


    public ReviewAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;

    }

    public void addAll(List<ProductReview> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);

        return new ReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {

        if (list.get(position).rating != 0 && list.get(position).rating > 2) {
            Drawable unwrappedDrawable = holder.tvRatting.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, activity.getResources().getColor(R.color.green));

        } else {

            Drawable unwrappedDrawable = holder.tvRatting.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.RED);
        }
        holder.tvRatting.setText(Constant.setDecimalTwo((double) list.get(position).rating));
        holder.tvName.setText(list.get(position).name);
        holder.tvReview.setText(list.get(position).review);
        holder.tvTime.setText(Constant.setDate(list.get(position).dateCreated));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvRatting)
        TextViewRegular tvRatting;

        @BindView(R.id.tvReview)
        TextViewRegular tvReview;

        @BindView(R.id.tvTime)
        TextViewLight tvTime;


        public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}