package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Home;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class SixReasonAdapter extends RecyclerView.Adapter<SixReasonAdapter.SpecialOfferViewHolder> {


    private List<Home.FeatureBox> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public SixReasonAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
    }

    public void addAll(List<Home.FeatureBox> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public SpecialOfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_six_reason, parent, false);

        return new SpecialOfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SpecialOfferViewHolder holder, final int position) {
        Picasso.get().load(list.get(position).featureImage).into(holder.ivImage);
        holder.tvDescription.setText(list.get(position).featureContent);
        holder.tvName.setText(list.get(position).featureTitle);

        Drawable unwrappedDrawable = holder.ivImage.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor((((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)))));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SpecialOfferViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvDescription)
        TextViewLight tvDescription;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.llMain)
        LinearLayout llMain;

        public SpecialOfferViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}