package com.example.mc_progetto_kotlin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MenuDetailsScreen(menuName: String, onPurchase: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(menuName, style = MaterialTheme.typography.headlineSmall)
        Text("Descrizione lunga del menù selezionato...")
        Button(
            onClick = onPurchase,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Acquista")
        }
    }
}
