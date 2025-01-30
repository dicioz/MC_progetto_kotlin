package com.example.mc_progetto_kotlin.view

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_progetto_kotlin.viewmodel.MenuItem
import com.example.mc_progetto_kotlin.viewmodel.MenuListViewModel
import kotlinx.coroutines.flow.StateFlow


@Composable
fun MenuListScreen(onMenuSelected: (String) -> Unit) {
    val menuListViewModel: MenuListViewModel = viewModel()
    LaunchedEffect(Unit) {
        menuListViewModel.loadMenus() // Carica i menu una sola volta all'inizio
    }
    val menuList by menuListViewModel.menuList.collectAsState()
    val isLoading = menuListViewModel.menuList.collectAsState().value.isEmpty()

    Log.d("MenuListScreen", menuList.toString())

    // Card di visualizzazione del menu
    @Composable
    fun MenuCard(menu: MenuItem, onMenuSelected: (String) -> Unit) {
        val imageBitmap = menu.image.let { base64String ->
            try {
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                Log.e("MenuCard", "Errore nella decodifica dell'immagine: ${e.message}")
                null
            }
        }

        Card(
            modifier = Modifier
                .padding(16.dp)  // Maggiore padding per una visualizzazione più spaziosa
                .fillMaxWidth()
                .clickable { onMenuSelected(menu.mid.toString()) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)  // Padding per separare i contenuti all'interno della card
            ) {
                // Se l'immagine è disponibile, visualizzala
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Immagine del menù",
                        modifier = Modifier
                            .fillMaxWidth()  // L'immagine occupa tutta la larghezza
                            .aspectRatio(16f / 9f)  // Imposta un buon rapporto per l'immagine
                            .padding(bottom = 8.dp)  // Padding tra l'immagine e il testo
                    )
                }

                // Nome del menu
                Text(
                    text = menu.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 4.dp)  // Padding per separare il nome dal resto
                )

                // Descrizione breve
                Text(
                    text = menu.shortDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)  // Padding per separare dalla sezione prezzo
                )

                // Prezzo
                Text(
                    text = "Prezzo: ${menu.price} €",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Se i menu sono in fase di caricamento, mostra un testo
    if (isLoading) {
        Text(
            text = "Caricamento...",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(menuList) { menu ->
                MenuCard(menu, onMenuSelected)
            }
        }
    }
    Log.d("MenuListScreen", "MenuList caricato")
}