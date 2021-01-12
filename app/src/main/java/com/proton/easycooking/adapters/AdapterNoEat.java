package com.proton.easycooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.proton.easycooking.Config;
import com.proton.easycooking.MainActivity;
import com.proton.easycooking.R;
import com.proton.easycooking.models.NoEatItem;

import java.util.List;

public class AdapterNoEat extends RecyclerView.Adapter<AdapterNoEat.ViewHolder> {

    NoEatItem itemNoEat;
    private Context context;
    private List<NoEatItem> arrayNoEatItem;
    RewardedAd rewardedAd;
    AdView mAdView;
    ImageView imgEmo;
    Button btnSave;


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textItem1;
        TextView textItem2;
        TextView textAction;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);

            this.textItem1 = (TextView) view.findViewById(R.id.tv_Item1);
            this.textItem2 = (TextView) view.findViewById(R.id.tv_Item2);
            this.textAction = (TextView) view.findViewById(R.id.tv_Action);
            this.cardView = (CardView) view.findViewById(R.id.cv_NoEat);

        }

    }

    public AdapterNoEat(Context mContext, List<NoEatItem> arrayNoEatItem) {
        this.context = mContext;
        this.arrayNoEatItem = arrayNoEatItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_noeat, parent, false);

        if (Config.ENABLE_ADMOB_REWARDED_ADS) {
            createAndLoadRewardedAd();
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemNoEat = arrayNoEatItem.get(position);

        holder.textItem1.setText(itemNoEat.getItems().split("&")[0]);
        holder.textItem2.setText(itemNoEat.getItems().split("&")[1]);
        holder.textAction.setText(itemNoEat.getAction());
        //Toast.makeText(context,itemNoEat.getItems(),Toast.LENGTH_SHORT).show();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemNoEat = arrayNoEatItem.get(position);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialog_noeat, null);
                dialogBuilder.setView(dialogView);
                mAdView = (AdView) dialogView.findViewById(R.id.adv_dialog);
                if (Config.ENABLE_ADMOB_BANNER_ADS) {
                    loadAdMobBannerAd();
                }
                imgEmo = (ImageView) dialogView.findViewById(R.id.img_emoji);
                //Toast.makeText(context, itemNoEat.getStatus(), Toast.LENGTH_SHORT).show();
                switch (itemNoEat.getStatus()) {
                    case "1":
                        Glide.with(context)
                                .load(R.drawable.face_dizzy)
                                .into(imgEmo);
                        break;
                    case "2":
                        Glide.with(context)
                                .load(R.drawable.face_woozy)
                                .into(imgEmo);
                        break;
                    case "3":
                        Glide.with(context)
                                .load(R.drawable.face_vomiting)
                                .into(imgEmo);
                        break;
                    case "4":
                        Glide.with(context)
                                .load(R.drawable.face_weary)
                                .into(imgEmo);
                        break;
                    case "5":
                        Glide.with(context)
                                .load(R.drawable.face_zany)
                                .into(imgEmo);
                    case "6":
                        Glide.with(context)
                                .load(R.drawable.face_hot)
                                .into(imgEmo);
                    case "7":
                        Glide.with(context)
                                .load(R.drawable.face_zany)
                                .into(imgEmo);
                        break;
                }

                btnSave = (Button) dialogView.findViewById(R.id.btn_saveme);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Config.ENABLE_ADMOB_REWARDED_ADS) {
                            showRewardedAd();
                            createAndLoadRewardedAd();
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.face_hugging)
                                    .placeholder(R.drawable.simple_img1)
                                    .into(imgEmo);
                            btnSave.setText("Thank You Very Much");
                        }
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayNoEatItem.size();
    }

    public void setNoEatItemList(List<NoEatItem> noEatItemList) {
        arrayNoEatItem = noEatItemList;
        notifyDataSetChanged();
    }

    private void loadAdMobBannerAd() {
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(context, "Thank u", Toast.LENGTH_LONG).show();
                super.onAdClicked();
            }
        });

    }

    public void createAndLoadRewardedAd() {
        rewardedAd = new RewardedAd(context, context.getResources().getString(R.string.adMob_reward_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    public void showRewardedAd() {
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                    Glide.with(context)
                            .load(R.drawable.face_hugging)
                            .placeholder(R.drawable.simple_img1)
                            .into(imgEmo);
                    btnSave.setText("Thank You Very Much");

                    //imgEmo.setImageDrawable(context.getDrawable(R.drawable.hugging_face));

                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(((MainActivity) context), adCallback);
        } else {
            Toast.makeText(context, "Connection fail!\n Please retry.", Toast.LENGTH_LONG).show();
        }
    }
}
