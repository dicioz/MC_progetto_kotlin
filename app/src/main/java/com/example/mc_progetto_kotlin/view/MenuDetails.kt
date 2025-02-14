package com.example.mc_progetto_kotlin.view

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.viewmodel.MenuDetailsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun MenuDetailsScreen(menuId: Int, onPurchase: (Int) -> Unit, navController: NavController) {
    val menuDetailsViewModel: MenuDetailsViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") } // Messaggio dinamico


    val menu by menuDetailsViewModel.menu.observeAsState()
    if(dialogMessage == "Acquisto effettuato con successo"){
        Log.d("MenuDetailsScreen", "Nome menu salvato correttamente")
        menu?.name?.let { DataStoreManager.saveMenuName(it) }
    }

    val imageBitmap = menu?.image?.let { base64String ->
        try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("MenuDetailsScreen", "Errore nella decodifica dell'immagine: ${e.message}")
            null
        }
    }

    val context = LocalContext.current
    LaunchedEffect(menuId) {
        menuDetailsViewModel.loadMenu(menuId, context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Menu image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        } else {
            Text(text = "Caricamento immagine...", style = MaterialTheme.typography.bodyMedium)
        }

        Text(text = menu?.name ?: "Caricamento...", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Prezzo: " + (menu?.price ?: "Caricamento...") + "€", style = MaterialTheme.typography.bodyMedium)
        Text(text = menu?.longDescription ?: "Caricamento...", style = MaterialTheme.typography.bodySmall)

        Button(
            onClick = {
                menu?.let {
                    MainScope().launch {
                        menuDetailsViewModel.buyMenu(menuId) { response ->
                            if (response == "true") {
                                dialogMessage = "Acquisto effettuato con successo"
                            } else if (response == "Invalid Card") {
                                dialogMessage = "Errore: Invalid Card"
                            } else if(response == "Hai già un altro ordine attivo"){
                                dialogMessage = "Hai già un altro ordine attivo"
                            }
                            showDialog = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("Ordina ora")
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text("Torna indietro")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Notifica") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
