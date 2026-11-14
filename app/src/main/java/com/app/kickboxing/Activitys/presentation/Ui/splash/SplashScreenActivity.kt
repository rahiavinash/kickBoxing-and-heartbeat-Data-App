package com.app.kickboxing.Activitys.Ui.Activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        Handler(Looper.getMainLooper()).postDelayed({
            if(Prefs.init().isProfileCompleted && Prefs.init().accessToken != ""){
                Prefs.init().isSelectedFilter="week"
                Prefs.init().recentStat = null
                Prefs.init().koPoint="0"
                val intent = Intent(this, HomesActivity::class.java)
                startActivity(intent)
            }else{
                Prefs.init().accessToken = ""
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            this.finish()
        }, 2000)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val window: Window = this.window
//
//
//// clear FLAG_TRANSLUCENT_STATUS flag:
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//
//
//// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//
//
//// finally change the color
//            window.statusBarColor = ContextCompat.getColor(this, com.app.kickboxing.R.color.colorBagDark)
//        //    window.statusBarColor = ContextCompat.getColor(this, com.hb.dialog.R.color.dialog_white)
//        } else {
//            // For API 21 and 22: Set status bar background to black
//            window.statusBarColor = ContextCompat.getColor(this, com.hb.dialog.R.color.dialog_white)
//        }

    }
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        if (config.fontScale != 1f) {
            config.fontScale = 1f
            res.updateConfiguration(config, res.displayMetrics)
        }
        return res
    }

    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}