package com.example.mc_progetto_kotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.ui.theme.MC_Progetto_kotlinTheme
import com.example.mc_progetto_kotlin.view.AppContent
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        CommunicationController.init(this) // Inizializza il contesto globale

        lifecycleScope.launch {
            // Se non c'è nessuna pagina salvata, usiamo "menuList" come default
            val lastPage = DataStoreManager.getLastPage() ?: "menuList"
            Log.d("MainActivity", "Last page salvata: $lastPage")
            setContent {
                MC_Progetto_kotlinTheme {
                    AppContent(lastPage)
                }
            }
        }
    }
}
