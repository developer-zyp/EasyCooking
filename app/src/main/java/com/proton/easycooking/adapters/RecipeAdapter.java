package com.proton.easycooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proton.easycooking.AdMob;
import com.proton.easycooking.AppTools;
import com.proton.easycooking.R;
import com.proton.easycooking.RecipesDetailActivity;
import com.proton.easycooking.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final Context context;
    Recipe itemRecipe;
    private List<Recipe> recipeList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public RecipeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_home_recipe, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        itemRecipe = recipeList.get(position);

        holder.tvRecipeName.setText(itemRecipe.getRecipeName());
        Glide.with(context).load(itemRecipe.getRecipeImage())
                .placeholder(R.drawable.simple_img1)
                .into(holder.imgRecipeImage);

        holder.cvRecipe.setOnClickListener(v -> {
//            if (mOnItemClickListener != null) {
//                mOnItemClickListener.onItemClick(v, itemRecipe, position);
//            }
            itemRecipe = recipeList.get(position);
            AppTools.itemRecipeDetail = itemRecipe;

            Intent intent = new Intent(context, RecipesDetailActivity.class);
            context.startActivity(intent);
            AdMob.showInterstitialAd(context, "admob");
        });

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Recipe obj, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipeImage;
        TextView tvRecipeName;
        CardView cvRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipeImage = itemView.findViewById(R.id.imageView);
            tvRecipeName = itemView.findViewById(R.id.txtName);
            cvRecipe = itemView.findViewById(R.id.lyt_parent);

        }
    }

}
