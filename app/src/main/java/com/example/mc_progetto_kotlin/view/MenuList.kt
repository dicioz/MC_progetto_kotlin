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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_progetto_kotlin.viewmodel.MenuItem
import com.example.mc_progetto_kotlin.viewmodel.MenuListViewModel

@Composable
fun MenuListScreen(onMenuSelected: (Int) -> Unit) {
    val menuListViewModel: MenuListViewModel = viewModel()
    val context = LocalContext.current

    val menuList by menuListViewModel.menuList.collectAsState() // Osserva i dati dal ViewModel
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(menuList) {
        if (menuList.isEmpty()) {
            isLoading = true
            menuListViewModel.loadMenus(context)
        } else {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(menuList) { menu ->
                MenuCard(menu, onMenuSelected)
            }
        }
    }
}


@Composable
fun MenuCard(menu: MenuItem, onMenuSelected: (Int) -> Unit) {
    val imageBitmap = menu.image?.let { base64String ->
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
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onMenuSelected(menu.mid) }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Immagine del menù",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .padding(bottom = 8.dp)
                )
            }
            Text(
                text = menu.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = menu.shortDescription,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Prezzo: ${menu.price} €",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Tempo: ${menu.deliveryTime} min",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
