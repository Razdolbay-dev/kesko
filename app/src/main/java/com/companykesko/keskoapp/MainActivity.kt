package com.companykesko.keskoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.companykesko.keskoapp.ui.MainScreen
import com.companykesko.keskoapp.ui.theme.KESKOAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KESKOAPPTheme {
                MainScreen()
            }
        }
    }
}