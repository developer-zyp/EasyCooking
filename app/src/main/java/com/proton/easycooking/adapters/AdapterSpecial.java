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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.proton.easycooking.Config;
import com.proton.easycooking.GlobalClass;
import com.proton.easycooking.R;
import com.proton.easycooking.RecipesDetailActivity;
import com.proton.easycooking.models.Recipes;

import java.util.List;

public class AdapterSpecial extends RecyclerView.Adapter<AdapterSpecial.ViewHolder> {

    Recipes itemRecipe;
    private final Context context;
    private List<Recipes> arrayRecipe;

    private InterstitialAd interstitialAd;
    private int counter = 1;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewSeenCount;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);

            this.textViewName = (TextView) view.findViewById(R.id.tv_Name);
            this.imageView = (ImageView) view.findViewById(R.id.iv_Image);
            this.textViewSeenCount = (TextView) view.findViewById(R.id.tv_SeenCount);
            this.cardView = (CardView) view.findViewById(R.id.cv_Special);

        }

    }

    public AdapterSpecial(Context mContext, List<Recipes> arrayItemRecipe) {
        this.context = mContext;
        this.arrayRecipe = arrayItemRecipe;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_special, parent, false);

        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            loadInterstitialAd();
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemRecipe = arrayRecipe.get(position);

        holder.textViewName.setText(itemRecipe.getRecipeName());
        holder.textViewSeenCount.setText(itemRecipe.getRecipeView());

        if(itemRecipe.getRecipeImage().length() > 0){
//            Picasso.get().load(itemRecipe.getRecipeImage())
//                    .placeholder(R.drawable.simple_img1)
//                    .into(holder.imageView);
            Glide.with(context).load(itemRecipe.getRecipeImage())
                    .placeholder(R.drawable.simple_img1)
                    .into(holder.imageView);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemRecipe = arrayRecipe.get(position);
                String recipeId = itemRecipe.getRecipeId();
//                Toast.makeText(context, "Category Id : " + recipeId + "\nName : " +
//                        itemRecipe.getRecipeName(), Toast.LENGTH_LONG).show();

                GlobalClass.recipeId = Integer.parseInt(recipeId);

                Intent intent = new Intent(context, RecipesDetailActivity.class);
                context.startActivity(intent);

                if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
                    showInterstitialAd();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayRecipe.size();
    }

    public void setRecipeList(List<Recipes> recipesList) {
        arrayRecipe = recipesList;
        notifyDataSetChanged();
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getResources().getString(R.string.adMob_interstitial_id));
        final AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(adRequest);
            }
        });

    }

    private void showInterstitialAd() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            if (counter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                interstitialAd.show();
                counter = 1;
            } else {
                counter++;
            }
        }

    }
}
