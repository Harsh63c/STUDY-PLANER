package com.example

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.MobileAds
import com.example.utils.AppOpenAdManager

class MyApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleEventObserver {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        appOpenAdManager = AppOpenAdManager()
        MobileAds.initialize(this) {
            appOpenAdManager.loadAd(this@MyApplication)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun showAdIfAvailable(activity: Activity, onShowComplete: () -> Unit = {}) {
        appOpenAdManager.showAdIfAvailable(activity, onShowComplete)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            currentActivity?.let {
                // Ignore SplashActivity for automatic warm-start shows
                if (it !is SplashActivity) {
                    appOpenAdManager.showAdIfAvailable(it)
                }
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    
    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }
    
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
    
    override fun onActivityPaused(activity: Activity) {}
    
    override fun onActivityStopped(activity: Activity) {}
    
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
