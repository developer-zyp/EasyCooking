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
    private Context context;
    private List<Category> arrayAmount;


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.iv_caloriesAmt);

        }

    }

    public AdapterCaloriesAmt(Context mContext, List<Category> arrayItemAmount) {
        this.context = mContext;
        this.arrayAmount = arrayItemAmount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_amount, parent, false);
        //loadInterstitialAd();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        imgURL = arrayAmount.get(position);

        Glide.with(context).load(imgURL.getCategoryImage())
                .placeholder(R.drawable.simple_img)
                .into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        return arrayAmount.size();
    }


    public void setCaloriesAmtList(List<Category> amtList) {
        arrayAmount = amtList;
        notifyDataSetChanged();
    }
}
