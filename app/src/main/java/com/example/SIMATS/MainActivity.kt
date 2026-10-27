package com.example.SIMATS

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.SIMATS.navigation.AppNavHost
import com.example.SIMATS.ui.theme.MomoraAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomoraAITheme {
                AppNavHost()
            }
        }
    }
}
