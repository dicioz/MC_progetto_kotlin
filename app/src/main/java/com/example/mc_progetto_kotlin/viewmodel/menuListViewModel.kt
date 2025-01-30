package com.example.mc_progetto_kotlin.viewmodel

import android.util.Log
import android.view.Menu
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.mc_progetto_kotlin.model.CommunicationController
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Serial
import com.example.mc_progetto_kotlin.model.LocationPermissionHandler

@Serializable
data class MenuItem(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
    var image: String? = null
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double // Corretto il nome da "lgn" a "lng"
)

@Serializable
data class Image(
    val base64: String
)


//[
//{
//    "mid": 0,
//    "name": "Pizza Margherita",
//    "price": 5,
//    "location": {
//    "lat": 45.4642,
//    "lng": 9.19
//},
//    "imageVersion": 0,
//    "shortDescription": "Pizza con pomodoro, mozzarella e basilico.",
//    "deliveryTime": 30
//}
//]

class MenuListViewModel : ViewModel() {
    // Stato per la lista di menu
    private val _menuList = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuList: StateFlow<List<MenuItem>> = _menuList

    // Stato per l'errore
    private val _error = MutableStateFlow<String?>(null)
    //val error: StateFlow<String?> = _error

    // Recupera i menu dal server
    fun loadMenus() {
        viewModelScope.launch {
            try {
                val url = "https://develop.ewlab.di.unimi.it/mc/2425/menu"
                val httpMethod = CommunicationController.HttpMethod.GET
                val lat = 45.4789
                val lng = 9.19

                // Crea l'utente prima di inviare la richiesta
                CommunicationController.createUser()
                val sid = CommunicationController.sid
                val queryParameters = mapOf("lat" to lat, "lng" to lng, "sid" to sid)

                // Effettua la richiesta per ottenere i menu
                val response: HttpResponse =
                    CommunicationController.genericRequest(url, httpMethod, queryParameters, null)

                if (response.status == HttpStatusCode.OK) {
                    try {
                        Log.d("MenuListViewModel", "inizio deserializzazione")
                        val result: List<MenuItem> = response.body()
                        Log.d("MenuListViewModel", "Menu list response: $result")

                        // Aggiorna la lista dei menu
                        _menuList.value = result

                        // Carica le immagini per ogni menu
                        result.forEach { menuItem ->
                            getMenuImage(menuItem.mid) { image ->
                                // Ogni volta che una nuova immagine è caricata, aggiorna l'immagine nella lista
                                val updatedItem =
                                    menuItem.copy(image = image.base64) //copy crea una copia dell'oggetto
                                _menuList.value = _menuList.value.map {
                                    if (it.mid == updatedItem.mid) updatedItem else it
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MenuListViewModel", "Error deserializing JSON: ${e.message}")
                        _error.value = "Errore nel parsing dei dati"
                    }
                } else {
                    _error.value = "Errore nel caricamento del menu: ${response.status.description}"
                }
            } catch (e: Exception) {
                _error.value = "Errore di rete: ${e.message}"
                Log.e("MenuListViewModel", "Network error: ${e.message}")
            }
        }
    }

    // Funzione per ottenere l'immagine del menu
    private fun getMenuImage(menuId: Int, onImageLoaded: (Image) -> Unit) {
        viewModelScope.launch {
            try {
                val sid = CommunicationController.sid
                val url = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$menuId/image"
                val httpMethod = CommunicationController.HttpMethod.GET
                Log.d("MenuListViewModel", "Fetching image for menu id $menuId")

                val response = CommunicationController.genericRequest(
                    url,
                    httpMethod,
                    mapOf("sid" to sid),
                    null
                )
                Log.d("MenuListViewModel", "Image fetch response status: ${response.status}")

                if (response.status == HttpStatusCode.OK) {
                    Log.d("MenuListViewModel", "Assigned image to menu id $menuId")
                    val image: Image = response.body()
                    onImageLoaded(image) // Passa l'immagine alla callback
                } else {
                    throw Exception("Error fetching image: ${response.status.description}")
                }
            } catch (e: Exception) {
                Log.e("MenuListViewModel", "Error fetching image: ${e.message}")
            }
        }
    }


}