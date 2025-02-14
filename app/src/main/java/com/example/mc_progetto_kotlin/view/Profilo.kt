package com.example.mc_progetto_kotlin.view

import android.graphics.Outline
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
import com.example.mc_progetto_kotlin.repository.getCurrentLocation
import com.example.mc_progetto_kotlin.viewmodel.OrderStatusViewModel
import com.example.mc_progetto_kotlin.viewmodel.Profile
import com.example.mc_progetto_kotlin.viewmodel.ProfileGET
import com.example.mc_progetto_kotlin.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var sid by remember { mutableStateOf<String?>(null) }
    var showDialog: Boolean? by remember { mutableStateOf(null) }

    // Ottieni il ViewModel
    val profileViewModel: ProfileViewModel = viewModel()
    //val orderStatusViewModel: OrderStatusViewModel = viewModel()

    // Ottieni i dati del profilo attuale
    val profile by profileViewModel.profile.collectAsState()

    // Variabili per i campi del profilo che verranno aggiornate
    var nome by remember { mutableStateOf(profile.firstName) }
    var cognome by remember { mutableStateOf(profile.lastName) }
    var nomeCarta by remember { mutableStateOf(profile.cardFullName) }
    var numeroCarta by remember { mutableStateOf(profile.cardNumber) }
    var dataScadenza by remember { mutableStateOf("${profile.cardExpireMonth}/${profile.cardExpireYear}") }
    var cvv by remember { mutableStateOf(profile.cardCVV) }
    var orderStatus by remember { mutableStateOf(profile.orderStatus) }
    var menuName: String? by remember { mutableStateOf(null) }
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
        orderStatus = profile.orderStatus ?: ""
        menuName = DataStoreManager.getMenuName()

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
                        onValueChange = {
                            if (it.length <= 16) numeroCarta = it
                        },
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

                    OutlinedTextField(
                        value = orderStatus ?: "Nessun ordine effettuato",
                        onValueChange = {},
                        label = { Text("Stato Ordine") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )

                    OutlinedTextField(
                        value = menuName ?: "Nessun menu acquistato",
                        onValueChange = {},
                        label = { Text("Stato Ordine") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false

                    )
                }
            }

            Button(
                onClick = {
                    val newProfile = Profile(
                        firstName = nome,
                        lastName = cognome,
                        cardFullName = nomeCarta,
                        cardNumber = numeroCarta,
                        cardExpireMonth = dataScadenza.take(2).toInt(),
                        cardExpireYear = dataScadenza.takeLast(2).toInt(),
                        cardCVV = cvv,
                        sid = sid ?: ""
                    )
                    Log.d("ProfileScreen", newProfile.toString())

                    // Salva i nuovi dati e, in base al risultato, aggiorna lo stato del dialogo
                    profileViewModel.saveNewDatas(newProfile) { success ->
                        showDialog = success
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Salva")
            }


            if (showDialog == true) {
                AlertDialog(
                    onDismissRequest = { showDialog = null },
                    title = { Text("Successo") },
                    text = { Text("Profilo aggiornato correttamente!") },
                    confirmButton = {
                        Button(onClick = { showDialog = null }) {
                            Text("OK")
                        }
                    }
                )
            } else if (showDialog == false) {
                AlertDialog(
                    onDismissRequest = { showDialog = null },
                    title = { Text("Errore") },
                    text = { Text("Il numero della carta deve contenere 16 cifre e il CVV 3 cifre") },
                    confirmButton = {
                        Button(onClick = { showDialog = null }) {
                            Text("OK")
                        }
                    }
                )
            }

        }
    }
}
