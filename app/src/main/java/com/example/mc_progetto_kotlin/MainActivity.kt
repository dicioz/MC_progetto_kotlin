package com.example.mc_progetto_kotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.ui.theme.MC_Progetto_kotlinTheme
import com.example.mc_progetto_kotlin.view.AppContent
import com.example.mc_progetto_kotlin.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        CommunicationController.init(this) // Inizializza il contesto globale
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            MC_Progetto_kotlinTheme {
                mainViewModel.initializeUser()
                AppContent()

            }
        }
    }
}
