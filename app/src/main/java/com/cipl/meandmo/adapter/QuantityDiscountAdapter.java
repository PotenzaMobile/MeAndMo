package com.cipl.meandmo.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.model.QuantityDiscount;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dhruv rathod on 8/12/2021
 */
public class QuantityDiscountAdapter extends RecyclerView.Adapter<QuantityDiscountAdapter.QuantityDiscountViewHolder> {

    private Activity activity;
    private ArrayList<QuantityDiscount> listItem = new ArrayList<>();

    public QuantityDiscountAdapter(Activity activity, ArrayList<QuantityDiscount> listItem) {
        this.activity = activity;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public QuantityDiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_quantity_discount, parent, false);
        return new QuantityDiscountViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityDiscountViewHolder holder, int position) {
        Log.e("TAG", "Dhruv: " + position);
         /* holder.tv_itemName.setText(listItem.get(position).getQuantity());
        holder.tv_price.setText(listItem.get(position).getDiscount());*/
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class QuantityDiscountViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_itemName)
        TextViewRegular tv_itemName;
        @BindView(R.id.tv_price)
        TextViewRegular tv_price;

        public QuantityDiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
