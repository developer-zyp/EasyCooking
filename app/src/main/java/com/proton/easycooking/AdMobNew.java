package com.proton.easycooking;

public class AdMobNew {

//    private static InterstitialAd mInterstitialAd;
//    private static RewardedAd mRewardedAd;
//
//    public interface OnAdClosedListener {
//        void onAdClosed(String adClosed);
//    }
//
//    public interface OnEarnedRewardListener {
//        void onEarnedReward(String earnedReward);
//    }
//
//    public static void showBannerAds(Context context, String adNetwork, ViewGroup adViewLayout, AdSize adSize) {
//        if (AppConfig.ENABLE_ADMOB_BANNER_ADS) {
//            if (adNetwork.equals("admob")) {
//                AdView mAdView = new AdView(context);
//                mAdView.setAdSize(adSize);
//                mAdView.setAdUnitId(AppConfig.ADMOB_BANNER_ID);
//                AdRequest.Builder builder = new AdRequest.Builder();
//                mAdView.loadAd(builder.build());
//                adViewLayout.addView(mAdView);
//                mAdView.setAdListener(new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
//                        adViewLayout.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onAdLoaded() {
//                        super.onAdLoaded();
//                        adViewLayout.setVisibility(View.VISIBLE);
//                    }
//                });
//            } else if (adNetwork.equals("facebook")) {
////                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Constant.SAVE_ADS_BANNER_ID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
////                adView.loadAd();
////                adViewLayout.addView(adView);
////                adViewLayout.setGravity(Gravity.CENTER);
//                AppTools.showToast(context, "Facebook Ad");
//            }
//        } else {
//            adViewLayout.setVisibility(View.GONE);
//        }
//    }
//
//    public static void showInterstitialAds(Context context, String AdNetwork, OnAdClosedListener onAdClosedListener) {
//        if (AppConfig.ENABLE_ADMOB_INTERSTITIAL_ADS) {
//            if (AdNetwork.equals("admob")) {
//                AppConfig.INTERSTITIAL_COUNT++;
//                if (AppConfig.INTERSTITIAL_COUNT == AppConfig.INTERSTITIAL_INTERVAL) {
//                    AppConfig.INTERSTITIAL_COUNT = 0;
//                    AppTools.showProgressDialog(context, "Loading ...");
//
//                    AdRequest adRequest = new AdRequest.Builder().build();
//                    InterstitialAd.load(context, AppConfig.ADMOB_INTERSTITIAL_ID, adRequest,
//                            new InterstitialAdLoadCallback() {
//                                @Override
//                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                                    // The mInterstitialAd reference will be null until an ad is loaded.
//                                    //Log.i(TAG, "onAdLoaded");
//                                    mInterstitialAd = interstitialAd;
//                                    AppTools.hideProgressDialog();
//                                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                                        @Override
//                                        public void onAdDismissedFullScreenContent() {
//                                            // Called when fullscreen content is dismissed.
//                                            //Log.d("TAG", "The ad was dismissed.");
//                                            onAdClosedListener.onAdClosed(null);
//                                        }
//
//                                        @Override
//                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                            // Called when fullscreen content failed to show.
//                                            //Log.d("TAG", "The ad failed to show.");
//                                            onAdClosedListener.onAdClosed(null);
//                                        }
//
//                                        @Override
//                                        public void onAdShowedFullScreenContent() {
//                                            // Called when fullscreen content is shown.
//                                            // Make sure to set your reference to null so you don't show it a second time.
//                                            mInterstitialAd = null;
//                                            //Log.d("TAG", "The ad was shown.");
//                                        }
//                                    });
//                                    mInterstitialAd.show((Activity) context);
//                                }
//
//                                @Override
//                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                                    // Handle the error
//                                    mInterstitialAd = null;
//                                    AppTools.hideProgressDialog();
//                                    onAdClosedListener.onAdClosed(null);
//                                    //Log.i(TAG, loadAdError.getMessage());
//                                }
//                            }
//                    );
//
//                } else {
//                    onAdClosedListener.onAdClosed(null);
//                }
//            } else if (AdNetwork.equals("facebook")) {
//                AppConfig.INTERSTITIAL_COUNT++;
////                if (AppConfig.INTERSTITIAL_COUNT == AppConfig.INTERSTITIAL_INTERVAL) {
////                    AppConfig.INTERSTITIAL_COUNT = 0;
////                    AppTools.showProgressDialog(context, "Loading Ad ...");
////                    final com.facebook.ads.InterstitialAd mInterstitialfb = new com.facebook.ads.InterstitialAd(mContext, Constant.SAVE_ADS_FULL_ID);
////                    InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
////                        @Override
////                        public void onInterstitialDisplayed(Ad ad) {
////                        }
////
////                        @Override
////                        public void onInterstitialDismissed(Ad ad) {
////                            Intent intent = new Intent(mContext, PlayListActivity.class);
////                            intent.putExtra("Id", Id);
////                            intent.putExtra("name", Name);
////                            mContext.startActivity(intent);
////                        }
////
////                        @Override
////                        public void onError(Ad ad, AdError adError) {
////                            pDialog.dismiss();
////                            Intent intent = new Intent(mContext, PlayListActivity.class);
////                            intent.putExtra("Id", Id);
////                            intent.putExtra("name", Name);
////                            mContext.startActivity(intent);
////                        }
////
////                        @Override
////                        public void onAdLoaded(Ad ad) {
////                            pDialog.dismiss();
////                            mInterstitialfb.show();
////                        }
////
////                        @Override
////                        public void onAdClicked(Ad ad) {
////                        }
////
////                        @Override
////                        public void onLoggingImpression(Ad ad) {
////                        }
////                    };
////                    com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = mInterstitialfb.buildLoadAdConfig().withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
////                    mInterstitialfb.loadAd(loadAdConfig);
////
////                }
//            }
//
//        } else {
//            onAdClosedListener.onAdClosed(null);
//        }
//
//    }
//
//    public static void showRewardedAds(Context context, String AdNetwork, OnAdClosedListener onAdClosedListener, OnEarnedRewardListener onEarnedRewardListener) {
//        if (AppConfig.ENABLE_ADMOB_REWARDED_ADS) {
//            if (AdNetwork.equals("admob")) {
//                AppConfig.REWARDED_COUNT++;
//                if (AppConfig.REWARDED_COUNT == AppConfig.REWARDED_INTERVAL) {
//                    AppConfig.REWARDED_COUNT = 0;
//                    AppTools.showProgressDialog(context, "Loading ...");
//
//                    AdRequest adRequest = new AdRequest.Builder().build();
//                    RewardedAd.load(context, AppConfig.ADMOB_REWARDED_ID,
//                            adRequest, new RewardedAdLoadCallback() {
//                                @Override
//                                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                                    //Log.d(TAG, "Ad was loaded.");
//                                    AppTools.hideProgressDialog();
//                                    mRewardedAd = rewardedAd;
//                                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                                        @Override
//                                        public void onAdShowedFullScreenContent() {
//                                            // Called when ad is shown.
//                                            //Log.d(TAG, "Ad was shown.");
//                                        }
//
//                                        @Override
//                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                            // Called when ad fails to show.
//                                            //Log.d(TAG, "Ad failed to show.");
//                                            onAdClosedListener.onAdClosed(null);
//                                        }
//
//                                        @Override
//                                        public void onAdDismissedFullScreenContent() {
//                                            // Called when ad is dismissed.
//                                            // Set the ad reference to null so you don't show the ad a second time.
//                                            //Log.d(TAG, "Ad was dismissed.");
//                                            mRewardedAd = null;
//                                            onAdClosedListener.onAdClosed(null);
//                                        }
//                                    });
//                                    mRewardedAd.show((Activity) context, new OnUserEarnedRewardListener() {
//                                        @Override
//                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                                            // Handle the reward.
//                                            int rewardAmount = rewardItem.getAmount();
//                                            String rewardType = rewardItem.getType();
//                                            onEarnedRewardListener.onEarnedReward(null);
////                                            AppTools.showToast(context, "The user earned the reward.\n" +
////                                                    rewardAmount + "," + rewardType);
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                                    // Handle the error.
//                                    //Log.d(TAG, loadAdError.getMessage());
//                                    mRewardedAd = null;
//                                    AppTools.hideProgressDialog();
//                                    onAdClosedListener.onAdClosed(null);
//                                }
//                            }
//
//                    );
//                } else {
//                    onAdClosedListener.onAdClosed(null);
//                }
//            } else if (AdNetwork.equals("facebook")) {
//                AppConfig.REWARDED_COUNT++;
//            }
//
//        } else {
//            onAdClosedListener.onAdClosed(null);
//        }
//
//    }

}
