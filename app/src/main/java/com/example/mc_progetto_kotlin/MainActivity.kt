package com.example.mc_progetto_kotlin

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.LocationPermissionHandler
import com.example.mc_progetto_kotlin.ui.theme.MC_Progetto_kotlinTheme
import com.example.mc_progetto_kotlin.viewmodel.MainViewModel
import kotlin.coroutines.coroutineContext

class MainActivity : ComponentActivity() {
    private val _viewModel: MainViewModel by viewModels()
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user") //come l'asyncStorage di react, creo lo storage per salvare il sid

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)

        // Inizializzazione dell'utente tramite ViewModel
        _viewModel.initializeUser()
        setContent {
            //richiede utilizzo posizione
            Log.d("MainActivity", "Permessi richeisti")
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
