package com.example.mc_progetto_kotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mc_progetto_kotlin.ui.theme.MC_Progetto_kotlinTheme
import com.example.mc_progetto_kotlin.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val _viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)

        // Inizializzazione dell'utente tramite ViewModel
        _viewModel.initializeUser()

        setContent {
            MC_Progetto_kotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d("MainActivity", "setContent")
                    MainAppNavHost()
                }
            }
        }
    }
}
