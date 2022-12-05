package com.proton.easycooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.proton.easycooking.AdMob;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.R;
import com.proton.easycooking.RecipesDetailActivity;
import com.proton.easycooking.models.Recipe;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.ViewHolder> {

    private final Context context;
    private List<Recipe> mSliderItems = new ArrayList<>();
    private Recipe mItem;
    private RecipeAdapter.OnItemClickListener mOnItemClickListener;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_home_slider, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mItem = mSliderItems.get(position);

        holder.tvSliderName.setText(mItem.getRecipeName());
        Glide.with(context).load(mItem.getRecipeImage())
                .placeholder(R.drawable.simple_img1)
                .into(holder.imgSliderImage);

        holder.layoutSlider.setOnClickListener(v -> {
//            if (mOnItemClickListener != null) {
//                mOnItemClickListener.onItemClick(v, mItem, position);
//            }
            mItem = mSliderItems.get(position);
            AppTools.itemRecipeDetail = mItem;

            Intent intent = new Intent(context, RecipesDetailActivity.class);
            context.startActivity(intent);
            AdMob.showInterstitialAd(context, "admob");
        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public void setRecipeList(List<Recipe> itemList) {
        this.mSliderItems = itemList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(final RecipeAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Recipe obj, int position);
    }

    public static class ViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imgSliderImage;
        TextView tvSliderName;
        RelativeLayout layoutSlider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSliderImage = itemView.findViewById(R.id.slider_item_image);
            tvSliderName = itemView.findViewById(R.id.slider_item_name);
            layoutSlider = itemView.findViewById(R.id.layout_slider);

        }
    }
}
