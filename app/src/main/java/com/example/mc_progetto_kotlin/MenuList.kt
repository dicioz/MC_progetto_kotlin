package com.example.mc_progetto_kotlin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
fun MenuListScreen(onMenuSelected: (String) -> Unit) {
    val menuList = listOf(
        MenuItem("Pizza Margherita", "Un classico italiano", 8.50, "15-20 min"),
        MenuItem("Pasta al Pesto", "Deliziosa pasta fresca", 10.00, "20-25 min"),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(menuList) { menu ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { onMenuSelected(menu.name) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(menu.name, style = MaterialTheme.typography.headlineSmall)
                        Text(menu.description, style = MaterialTheme.typography.bodySmall)
                        Text("Costo: €${menu.price}")
                        Text("Consegna: ${menu.deliveryTime}")
                    }
                }
            }
        }
    }
}

data class MenuItem(
    val name: String,
    val description: String,
    val price: Double,
    val deliveryTime: String
)
