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
import com.proton.easycooking.BuildConfig;
import com.proton.easycooking.R;
import com.proton.easycooking.RecipesDetailActivity;
import com.proton.easycooking.models.Recipe;

import java.util.List;

public class AdapterSpecial extends RecyclerView.Adapter<AdapterSpecial.ViewHolder> {

    private final Context context;
    private Recipe itemRecipe;
    private List<Recipe> recipeList;

    public AdapterSpecial(Context mContext, List<Recipe> recipeList) {
        this.context = mContext;
        this.recipeList = recipeList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        itemRecipe = recipeList.get(position);

        holder.textViewName.setText(itemRecipe.getRecipeName());
        holder.textViewFavCount.setText(itemRecipe.getRecipeFav());
        holder.textViewViewCount.setText(itemRecipe.getRecipeView());

        if (BuildConfig.DEBUG) {
            String id = itemRecipe.getRecipeView() + " ID:" + itemRecipe.getRecipeId();
            holder.textViewViewCount.setText(id);
        }

        if (itemRecipe.getRecipeImage().length() > 0) {
            Glide.with(context).load(itemRecipe.getRecipeImage())
                    .placeholder(R.drawable.simple_img1)
                    .into(holder.imageView);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemRecipe = recipeList.get(position);
                String recipeId = itemRecipe.getRecipeId();
//                Toast.makeText(context, "Category Id : " + recipeId + "\nName : " +
//                        itemRecipe.getRecipeName(), Toast.LENGTH_LONG).show();

                AppTools.itemRecipeDetail = itemRecipe;

                Intent intent = new Intent(context, RecipesDetailActivity.class);
                context.startActivity(intent);
                AdMob.showInterstitialAd(context, "admob");
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_special, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewFavCount;
        TextView textViewViewCount;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.tv_Name);
            this.imageView = (ImageView) view.findViewById(R.id.iv_Image);
            this.textViewFavCount = (TextView) view.findViewById(R.id.tv_FavCount);
            this.textViewViewCount = (TextView) view.findViewById(R.id.tv_ViewCount);
            this.cardView = (CardView) view.findViewById(R.id.cv_Special);

        }
    }

}
