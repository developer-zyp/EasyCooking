package com.proton.easycooking;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdMob {

    private static InterstitialAd mInterstitialAd = null;
    private static RewardedAd mRewardedAd = null;

    public static void showBannerAds(Context context, String adNetwork, ViewGroup adViewLayout, AdSize adSize) {
        if (AppConfig.ENABLE_ADMOB_BANNER_ADS) {
            if (adNetwork.equals("admob")) {
                final AdView mAdView = new AdView(context);
                mAdView.setAdSize(adSize);
                mAdView.setAdUnitId(AppConfig.ADMOB_BANNER_ID);

                final AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                adViewLayout.addView(mAdView);
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        adViewLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adViewLayout.setVisibility(View.VISIBLE);
                    }
                });
            } else if (adNetwork.equals("facebook")) {
//                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Constant.SAVE_ADS_BANNER_ID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//                adView.loadAd();
//                adViewLayout.addView(adView);
//                adViewLayout.setGravity(Gravity.CENTER);
                AppTools.showToast(context, "FacebookAd");
            }
        } else {
            adViewLayout.setVisibility(View.GONE);
        }

    }

    public static void showInterstitialAd(Context context, String adNetwork) {
        if (AppConfig.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (adNetwork.equals("admob")) {
                Log.i("INTERSTITIAL_COUNT", String.valueOf(AppConfig.INTERSTITIAL_COUNT));
                if (AppConfig.INTERSTITIAL_COUNT == 0) {
                    mInterstitialAd = new InterstitialAd(context);
                    mInterstitialAd.setAdUnitId(AppConfig.ADMOB_INTERSTITIAL_ID);

                    final AdRequest adRequest = new AdRequest.Builder().build();
                    mInterstitialAd.loadAd(adRequest);
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            //mInterstitialAd.loadAd(adRequest);
                        }
                    });

                }

                AppConfig.INTERSTITIAL_COUNT++;
                if (AppConfig.INTERSTITIAL_COUNT == AppConfig.INTERSTITIAL_INTERVAL) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    mInterstitialAd = null;
                    AppConfig.INTERSTITIAL_COUNT = 0;
                }

            } else if (adNetwork.equals("facebook")) {
                AppTools.showToast(context, "FacebookAd");
            }
        }

    }

    public static void showRewardedAd(Context context, String adNetwork, OnAdClosedListener onAdClosedListener) {
        if (AppConfig.ENABLE_ADMOB_REWARDED_ADS) {
            if (adNetwork.equals("admob")) {
                AppConfig.ADMOB_REWARDED_COUNT++;
                Log.i("ADMOB_REWARDED_COUNT", String.valueOf(AppConfig.ADMOB_REWARDED_COUNT));
                if (AppConfig.ADMOB_REWARDED_COUNT == AppConfig.ADMOB_REWARDED_INTERVAL) {
                    AppConfig.ADMOB_REWARDED_COUNT = 0;

                    AppTools.showProgressDialog(context, "Please wait ...");
                    mRewardedAd = null;
                    mRewardedAd = new RewardedAd(context, AppConfig.ADMOB_REWARDED_ID);
                    final AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {
                            // Ad successfully loaded.
                            AppTools.hideProgressDialog();
                            if (mRewardedAd.isLoaded()) {
                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                    @Override
                                    public void onRewardedAdOpened() {
                                        // Ad opened.
                                    }

                                    @Override
                                    public void onRewardedAdClosed() {
                                        // Ad closed.
                                        //onAdClosedListener.onAdClosed("close");
                                    }

                                    @Override
                                    public void onRewardedAdFailedToShow(AdError adError) {
                                        // Ad failed to display.
                                        onAdClosedListener.onAdClosed("close");
                                    }

                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                                        // User earned reward.
                                        onAdClosedListener.onAdClosed("reward");
                                    }
                                };
                                mRewardedAd.show(((Activity) context), adCallback);
                            }
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(LoadAdError adError) {
                            // Ad failed to load.
                            mRewardedAd = null;
                            AppTools.hideProgressDialog();
                            onAdClosedListener.onAdClosed("close");
                        }
                    };
                    mRewardedAd.loadAd(adRequest, adLoadCallback);

                } else {
                    onAdClosedListener.onAdClosed("close");
                }

            } else if (adNetwork.equals("facebook")) {
                AppTools.showToast(context, "FacebookAd");
            }

        } else {
            onAdClosedListener.onAdClosed("close");
        }

    }

    public interface OnAdClosedListener {
        void onAdClosed(String adClosed);
    }

}
