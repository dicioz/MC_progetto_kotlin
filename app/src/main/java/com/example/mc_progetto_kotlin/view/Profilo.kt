package com.example.mc_progetto_kotlin.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.viewmodel.Profile
import com.example.mc_progetto_kotlin.viewmodel.ProfileGET
import com.example.mc_progetto_kotlin.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController) {

    val keyboardController = LocalSoftwareKeyboardController.current
    var sid by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }  // Stato per mostrare l'alert
    // Ottieni il ViewModel
    val profileViewModel: ProfileViewModel = viewModel()

    // Ottieni i dati del profilo attuale
    val profile by profileViewModel.profile.collectAsState()

    // Variabili per i campi del profilo che verranno aggiornate
    var nome by remember { mutableStateOf(profile.firstName) }
    var cognome by remember { mutableStateOf(profile.lastName) }
    var nomeCarta by remember { mutableStateOf(profile.cardFullName) }
    var numeroCarta by remember { mutableStateOf(profile.cardNumber) }
    var dataScadenza by remember { mutableStateOf("${profile.cardExpireMonth}/${profile.cardExpireYear}") }
    var cvv by remember { mutableStateOf(profile.cardCVV) }
    Log.d("ProfileScreen", profile.toString())

    // Recupera il sid quando il composable viene caricato
    LaunchedEffect(profile) {
        sid = DataStoreManager.getSid()
        nome = profile.firstName
        cognome = profile.lastName
        nomeCarta = profile.cardFullName
        numeroCarta = profile.cardNumber
        dataScadenza = "${profile.cardExpireMonth}/${profile.cardExpireYear}"
        cvv = profile.cardCVV

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { keyboardController?.hide() })
            }
            .padding(16.dp)
    ) {
        Column {
            Text("Profilo Utente", style = MaterialTheme.typography.headlineSmall)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cognome,
                        onValueChange = { cognome = it },
                        label = { Text("Cognome") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nomeCarta,
                        onValueChange = { nomeCarta = it },
                        label = { Text("Nome sulla Carta") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = numeroCarta,
                        onValueChange = { numeroCarta = it },
                        label = { Text("Numero Carta (16 cifre)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dataScadenza,
                        onValueChange = { dataScadenza = it },
                        label = { Text("Data Scadenza (MMYY)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV (3 cifre)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = {
                    val newProfile = Profile(
                        firstName = nome,
                        lastName = cognome,
                        cardFullName = nomeCarta,
                        cardNumber =  numeroCarta,
                        cardExpireMonth = dataScadenza.take(2).toInt(),
                        cardExpireYear = dataScadenza.takeLast(2).toInt(),
                        cardCVV = cvv,
                        sid = sid ?: ""
                    )
                    Log.d("ProfileScreen", newProfile.toString())

                    // Salva i nuovi dati e mostra il popup se va a buon fine
                    profileViewModel.saveNewDatas(newProfile) { success ->
                        if (success) {
                            showDialog = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Salva e Visualizza Menù")
            }

            // ✅ Mostra l'AlertDialog solo se `showDialog == true`
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Successo") },
                    text = { Text("Profilo aggiornato correttamente!") },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
