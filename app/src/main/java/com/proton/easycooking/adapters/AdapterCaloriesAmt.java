package com.proton.easycooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proton.easycooking.R;
import com.proton.easycooking.models.Category;

import java.util.List;

public class AdapterCaloriesAmt extends RecyclerView.Adapter<AdapterCaloriesAmt.ViewHolder> {

    Category imgURL;
    private final Context context;
    private List<Category> itemList;

    public AdapterCaloriesAmt(Context mContext, List<Category> categoryList) {
        this.context = mContext;
        this.itemList = categoryList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        imgURL = itemList.get(position);

        Glide.with(context).load(imgURL.getCategoryImage())
                .placeholder(R.drawable.simple_img1)
                .into(holder.imageView);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_amount, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setCaloriesAmtList(List<Category> amtList) {
        itemList = amtList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.iv_caloriesAmt);
        }
    }
}
