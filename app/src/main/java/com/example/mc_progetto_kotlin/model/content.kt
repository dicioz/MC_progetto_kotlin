
//package com.example.mc_progetto_kotlin.view
//
//package com.example.mc_progetto_kotlin.view
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.res.imageResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.mc_progetto_kotlin.R
//import com.example.mc_progetto_kotlin.viewmodel.Ingredients
//import com.example.mc_progetto_kotlin.viewmodel.MenuIngredientsViewModel
//
//var ingredientToShow: Ingredients = Ingredients("", "", false, "")
//
//@Composable
//fun MenuIngredients(menuId: Int, menuName: String, navController: NavController) {
//    val menuIngredientsViewModel: MenuIngredientsViewModel = viewModel()
//    val ingredients by menuIngredientsViewModel.ingredients.collectAsState()
//    var isLoading by remember { mutableStateOf(true) }
//
//    LaunchedEffect(ingredients) {
//        menuIngredientsViewModel.getIngredients(menuId)
//        if (ingredients.isNotEmpty()) {
//            isLoading = false
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Titolo centrato
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "Ingredienti del menu: $menuName",
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.headlineSmall
//            )
//        }
//
//        if (isLoading) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(12.dp) // Spazio tra gli elementi
//            ) {
//                items(ingredients) { ingredient ->
//                    ShowIngredients(ingredient)
//                }
//            }
//        }
//    }
//}
//@Composable
//fun ShowIngredients(ingredient: Ingredients) {
//    var showDialog: Boolean? by remember { mutableStateOf(null) }
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp)
//            .clickable { ingredientToShow = ingredient; showDialog = true },
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.medium
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // Se l'ingrediente è bio, mostra l'immagine in alto a destra
//            if (ingredient.bio) {
//                Image(
//                    bitmap = ImageBitmap.imageResource(id = R.drawable.bio),
//                    contentDescription = "Immagine del bio",
//                    modifier = Modifier
//                        .size(40.dp) // Dimensione dell'icona
//                        .align(Alignment.TopEnd) // Allinea in alto a destra
//                )
//            }
//
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(6.dp)
//            ) {
//                // Nome centrato
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Nome: ${ingredient.name}",
//                        fontWeight = FontWeight.Bold,
//                        textAlign = TextAlign.Center,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                }
//
//                // Descrizione allineata a sinistra
//                Text(
//                    text = "Descrizione: ${ingredient.description}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//
//                // Biologico allineato a sinistra
//                Text(
//                    text = "Biologico: ${if (ingredient.bio) "Sì" else "No"}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//
//                // Origine centrata
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Origine: ${ingredient.origin}",
//                        textAlign = TextAlign.Center,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//        }
//    }
//
//    if (showDialog == true) {
//        AlertDialog(
//            onDismissRequest = { showDialog = null },
//            title = { Text(text = ingredientToShow.name) },
//            confirmButton = {
//                Button(onClick = { showDialog = null }) {
//                    Text("OK")
//                }
//            }
//        )
//    }
//}


//---------------


//package com.example.mc_progetto_kotlin.viewmodel
//
//import android.provider.ContactsContract.Data
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.mc_progetto_kotlin.model.CommunicationController
//import com.example.mc_progetto_kotlin.model.DataStoreManager
//import io.ktor.client.call.body
//import io.ktor.http.HttpStatusCode
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class Ingredients(
//    val name: String,
//    val description: String,
//    val bio: Boolean,
//    val origin: String
//)
//
//
//
//class MenuIngredientsViewModel: ViewModel() {
//
//    private val _ingredients = MutableStateFlow<List<Ingredients>>(emptyList())
//    val ingredients: StateFlow<List<Ingredients>> = _ingredients
//
//    fun getIngredients(menuId: Int){
//        viewModelScope.launch{
//            val sid = DataStoreManager.getSid() ?: ""
//            if(sid == ""){
//                return@launch
//            }
//            val endpoint = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$menuId/ingredients"
//            val httpMethod = CommunicationController.HttpMethod.GET
//            val queryParams = mapOf(
//                "sid" to sid
//            )
//            Log.d("MenuIngredientsViewModel", "Richiesta ingredienti del menu con id $menuId e sid $sid")
//            val response = CommunicationController.genericRequest(endpoint, httpMethod, queryParams)
//            Log.d("MenuIngredientsViewModel", "Risposta ottenuta: $response")
//            if(response.status == HttpStatusCode.OK){
//                val datas: List<Ingredients> = response.body()
//
//                _ingredients.value = datas
//                Log.d("MenuIngredientsViewModel", "Ingredienti ottenuti con successo: $datas")
//            }
//        }
//    }
//
//}






//composable("menuIngredients/{mid}/{menuName}") { backStackEntry ->
//    val mid = backStackEntry.arguments?.getString("mid")?.toIntOrNull() ?: -1
//    val menuName = backStackEntry.arguments?.getString("menuName") ?: ""
//    // Gestisci la visualizzazione degli ingredienti qui
//    MenuIngredients(
//        menuId = mid,
//        menuName = menuName,
//        navController = navController
//    )
//}


//showIngredients: (Int, String?) -> Unit
//Button(
//modifier = Modifier
//.fillMaxWidth()
//.padding(top = 16.dp),
//onClick = { showIngredients(menuId, menu?.name)}
//) {
//    Text("Visualizza ingredienti")
//}
