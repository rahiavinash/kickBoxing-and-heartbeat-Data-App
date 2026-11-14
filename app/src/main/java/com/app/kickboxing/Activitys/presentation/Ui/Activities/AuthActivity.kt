package com.app.kickboxing.Activitys.presentation.Ui.Activities

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.kickboxing.Activitys.utils.AlertManager
import com.app.kickboxing.R
import com.app.kickboxing.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAuthBinding
    lateinit var navController: NavController
    private lateinit var alertManager: AlertManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT

        setContentView(binding.root)
        val navHostController =
            supportFragmentManager.findFragmentById(R.id.authFragmentContainerView) as NavHostFragment
        val isLogout = intent.getBooleanExtra("EXTRA_IS_LOGOUT_DUE_TO_SINGLE_SESSION", false)
        alertManager = AlertManager(binding.root as ViewGroup, this)
        navController = navHostController.navController
        if (isLogout){
            Handler().post{
                showErrorAlert("Your account has been logged in from another device.")
            }
        }
    }

    fun showSuccessAlert(message: String){
        alertManager.showSuccess(message)
    }

    fun showErrorAlert(message: String){
        alertManager.showError(message)
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