package com.example.mc_progetto_kotlin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun DeliveryStatusScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Stato Consegna", style = MaterialTheme.typography.headlineSmall)
        Text("Menù: Pizza Margherita")
        Text("Stato: In Consegna")
        Text("Ora di Consegna Prevista: 12:45")

        // Sezione mappa (placeholder)
        Text("Mappa (da integrare con libreria Google Maps)")
    }
}
