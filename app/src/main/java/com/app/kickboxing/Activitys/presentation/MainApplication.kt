package com.app.kickboxing.Activitys.presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.app.kickboxing.Activitys.Network.SocketIOImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
 var socket=SocketIOImpl()
    companion object{
        lateinit var instance:MainApplication
        fun get():MainApplication = instance
    }
    private var mCurrentActivity: Activity? = null

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                MainApplication.AppStateManager.setAppForegroundState(true)
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                if (activity.isFinishing.not()) {
                    AppStateManager.setAppForegroundState(false)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })

    }


    override fun onTerminate() {
        super.onTerminate()
        disconnectSocket()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        disconnectSocket()
    }
    private fun disconnectSocket() {
        socket.disconnect()
    }


    object AppStateManager {
        private var isAppInForeground = false

        fun setAppForegroundState(isForeground: Boolean) {
            isAppInForeground = isForeground
        }

        fun isAppInForeground(): Boolean = isAppInForeground
    }

}