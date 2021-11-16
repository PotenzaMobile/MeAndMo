package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cipl.meandmo.R;
import com.cipl.meandmo.activity.OrderDetailActivity;
import com.cipl.meandmo.activity.RepaymentActivity;
import com.cipl.meandmo.customview.roundedimageview.RoundedTransformationBuilder;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewMedium;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.interfaces.OnItemClickListner;
import com.cipl.meandmo.model.Orders;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */
public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.RecentViewHolde> {
    private List<Orders> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private final Transformation mTransformation;

    public MyOrderAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        mTransformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    public void addAll(List<Orders> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public RecentViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_order, parent, false);

        return new RecentViewHolde(itemView);
    }
    @Override
    public void onBindViewHolder(RecentViewHolde holder, final int position) {

//        holder.llMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Constant.ORDERDETAIL = list.get(position);
//                Intent intent = new Intent(activity, OrderDetailActivity.class);
//                intent.putExtra(RequestParamUtils.ID, list.get(position).id);
//                activity.startActivity(intent);
//            }
//        });

        Drawable unwrappedDrawable = holder.tvView.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR))));

        holder.tvOrderDateAndId.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
        holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
//        if (list.get(position).orderRepaymentUrl != null && list.get(position).orderRepaymentUrl.equals("")) {
//            holder.tvRepayment.setVisibility(View.GONE);
//        } else {
//            holder.tvRepayment.setVisibility(View.VISIBLE);
//        }
        holder.tvQuantity.setText(list.get(position).lineItems.size() + "");
        holder.tvTotalAmount.setText(list.get(position).currency + " " + list.get(position).total);
        holder.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ORDERDETAIL = list.get(position);
                Intent intent = new Intent(activity, OrderDetailActivity.class);
                intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                activity.startActivity(intent);
            }
        });

        holder.tvRepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ORDERDETAIL = list.get(position);
                Intent intent = new Intent(activity, RepaymentActivity.class);
                intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                intent.putExtra(RequestParamUtils.RepaymentURL, list.get(position).orderRepaymentUrl);
                intent.putExtra(RequestParamUtils.THANKYOU, list.get(position).Thankyou);
                activity.startActivity(intent);
            }
        });

        if (list.get(position).lineItems.get(0).productImage.equals("")) {
            holder.ivImage.setVisibility(View.INVISIBLE);
        } else {
            holder.ivImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(list.get(position).lineItems.get(0).productImage)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .transform(mTransformation)
                    .into(holder.ivImage);
        }


        String title = new String();
        for (int i = 0; i < list.get(position).lineItems.size(); i++) {
            if (i == 0) {
                title = list.get(position).lineItems.get(i).name;
            } else {
                title = title + " & " + list.get(position).lineItems.get(i).name;
            }
        }
//        holder.tvTitle.setText(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvProductName.setText(categoryList.name + "");
            holder.tvTitle.setText(Html.fromHtml(title + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
//            tvProductName.setText(categoryList.name + "");
            holder.tvTitle.setText(Html.fromHtml(title + ""));
        }

        String upperString = list.get(position).status.substring(0, 1).toUpperCase() + list.get(position).status.substring(1);
        holder.tvStatus.setText(upperString);

        String idAndDate = list.get(position).id + "";


        holder.tvOrderDate.setText(" " + Constant.setDate(list.get(position).dateCreated));
        holder.tvOrderDateAndId.setText(idAndDate);
        String statusDesc = new String();
        if (list.get(position).status.toLowerCase().equals(RequestParamUtils.any)) {
            holder.tvStatusDesc.setText(R.string.delivered_soon);
            holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.pending)) {
            holder.tvStatusDesc.setText(R.string.order_is_in_pending_state);
            holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.processing)) {
            holder.tvStatusDesc.setText(R.string.order_is_under_processing);
            holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.onHold)) {
            holder.tvStatusDesc.setText(R.string.order_is_on_hold);
            holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.completed)) {
            holder.tvStatusDesc.setText(R.string.delivered);
            holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.cancelled)) {
            holder.tvStatusDesc.setText(R.string.order_is_cancelled);
            holder.tvStatus.setTextColor(Color.RED);

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.refunded)) {
            holder.tvStatusDesc.setText(R.string.you_are_refunded_for_this_order);
            holder.tvStatus.setTextColor(Color.RED);

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.failed)) {
            holder.tvStatusDesc.setText(R.string.order_is_failed);
            holder.tvStatus.setTextColor(Color.RED);

        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.shipping)) {
            holder.tvStatusDesc.setText(R.string.delivered_soon);
            holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECOND_COLOR)));

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - height_value * 2;
        height = width / 2 + height_value;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class RecentViewHolde extends RecyclerView.ViewHolder {

        @BindView(R.id.tvOrderDateAndId)
        TextViewLight tvOrderDateAndId;

        @BindView(R.id.tvOrderDate)
        TextViewLight tvOrderDate;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvTitle)
        TextViewRegular tvTitle;

        @BindView(R.id.tvTotalAmount)
        TextViewMedium tvTotalAmount;

        @BindView(R.id.tvQuantity)
        TextViewMedium tvQuantity;

        @BindView(R.id.tvView)
        TextViewRegular tvView;

        @BindView(R.id.tvRepayment)
        TextViewRegular tvRepayment;

        @BindView(R.id.tvStatusDesc)
        TextViewRegular tvStatusDesc;

        @BindView(R.id.tvStatus)
        TextViewMedium tvStatus;

        @BindView(R.id.llMain)
        LinearLayout llMain;

        public RecentViewHolde(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}