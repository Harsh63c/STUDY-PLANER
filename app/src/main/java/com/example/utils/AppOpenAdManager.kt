package com.example.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenAdManager {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var loadTime: Long = 0

    // Real App Open Ad unit ID
    private val AD_UNIT_ID = "ca-app-pub-9582239220681999/9353490221"

    fun loadAd(context: Context, onAdLoaded: () -> Unit = {}) {
        if (isLoadingAd || isAdAvailable()) {
            if (isAdAvailable()) onAdLoaded()
            return
        }
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AD_UNIT_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d("AppOpenAdManager", "Ad Loaded")
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d("AppOpenAdManager", "Failed to load ad: ${loadAdError.message}")
                }
            }
        )
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showAdIfAvailable(activity: Activity, onShowComplete: () -> Unit = {}) {
        if (isShowingAd) {
            Log.d("AppOpenAdManager", "The app open ad is already showing.")
            onShowComplete()
            return
        }

        if (!isAdAvailable()) {
            Log.d("AppOpenAdManager", "The app open ad is not ready yet.")
            onShowComplete()
            loadAd(activity)
            return
        }

        Log.d("AppOpenAdManager", "Will show ad.")

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                Log.d("AppOpenAdManager", "Ad dismissed fullscreen content.")
                onShowComplete()
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                Log.d("AppOpenAdManager", "Ad failed to show fullscreen content: ${adError.message}")
                onShowComplete()
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                Log.d("AppOpenAdManager", "Ad showed fullscreen content.")
            }
        }

        appOpenAd?.show(activity)
    }
}
