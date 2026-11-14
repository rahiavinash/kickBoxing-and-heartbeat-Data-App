package com.app.kickboxing.Activitys.presentation.Ui.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.KbxckickboxingclubTheme

class JetpackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setContent {
                KbxckickboxingclubTheme {

            }
        }
    }
}