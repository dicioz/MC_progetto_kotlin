package com.example.mc_progetto_kotlin.view


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ProfileScreen(onNavigateToMenuList: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var creditCardName by remember { mutableStateOf("") }
    var creditCardNumber by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Profilo Utente", style = MaterialTheme.typography.headlineSmall)

        //outlinedTextField permette di visualizzare e inserire campi di testo
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Cognome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = creditCardName,
            onValueChange = { creditCardName = it },
            label = { Text("Nome sulla Carta") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = creditCardNumber,
            onValueChange = { creditCardNumber = it },
            label = { Text("Numero Carta (16 cifre)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expirationDate,
            onValueChange = { expirationDate = it },
            label = { Text("Data Scadenza (MMYY)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cvv,
            onValueChange = { cvv = it },
            label = { Text("CVV (3 cifre)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onNavigateToMenuList,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Salva e Visualizza Menù")
        }
    }
}
