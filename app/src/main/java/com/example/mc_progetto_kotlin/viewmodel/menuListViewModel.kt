package com.example.mc_progetto_kotlin.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mapboxexample.UserLocation
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.model.MenuImageEntity
import com.example.mc_progetto_kotlin.model.MyApplication
import com.example.mc_progetto_kotlin.repository.getCurrentLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    @SerialName("default image") var image: String? = null,
    @SerialName("longDescription") val longDescription: String? = null,
    val shortDescription: String,
    val deliveryTime: Int
)


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
//    "deliveryTime": 30,
//    "longDescription": "Pizza con pomodoro, mozzarella e basilico, cotta in forno a legna."
//}

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@Serializable
data class Image(
    val base64: String
)

class MenuListViewModel : ViewModel() {
    private val _menuList = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuList: StateFlow<List<MenuItem>> = _menuList
    private var sid: String = ""
    fun loadMenus(context: Context) {
        viewModelScope.launch {
            sid = DataStoreManager.getSid() ?: ""
            if (sid.isEmpty()) {
                Log.d("MenuListViewModel", "SID non presente, creando utente...")
                // Chiamata per creare l'utente (che salva il SID)
                CommunicationController.createUser()
                // Attendi che il SID venga salvato
                repeat(10) { // Ad esempio, riprova fino a 10 volte (5 secondi totali)
                    sid = DataStoreManager.getSid() ?: ""
                    if (sid.isNotEmpty()) return@repeat
                    kotlinx.coroutines.delay(500)
                }
            }
            // Se dopo il tentativo il SID è ancora vuoto, non proseguire
            if (sid.isEmpty()) {
                Log.e("MenuListViewModel", "Impossibile ottenere SID dopo la creazione")
                return@launch
            }
            Log.d("MenuListViewModel", "SID: $sid")



            // Ottieni la posizione corrente
            val userLocation: UserLocation? = getCurrentLocation(context)
            if (userLocation == null) {
                Log.e("MenuListViewModel", "Impossibile ottenere la posizione")
                return@launch
            }
//            if (sid == "") {
//                Log.e("MenuListViewModel", "Sid non presente")
//                return@launch
//            }
//            Log.d("MenuListViewModel", "Using SID: $sid")
            try {
                val url = "https://develop.ewlab.di.unimi.it/mc/2425/menu"
                val httpMethod = CommunicationController.HttpMethod.GET
                val queryParameters = mapOf(
                    "lat" to userLocation.latitude,
                    "lng" to userLocation.longitude,
                    "sid" to sid
                )
                val response: HttpResponse = CommunicationController.genericRequest(url, httpMethod, queryParameters, null)
                if (response.status == HttpStatusCode.OK) {
                    val result: List<MenuItem> = response.body()
                    _menuList.value = result
                    result.forEach { menuItem ->
                        getMenuImage(sid,menuItem.imageVersion, menuItem.mid) { image ->
                            val updatedItem = menuItem.copy(image = image.base64)
                            _menuList.value = _menuList.value.map {
                                if (it.mid == updatedItem.mid) updatedItem else it
                            }
                        }
                    }
                } else {
                    Log.e("MenuListViewModel", "Errore nel caricamento dei menu: ${response.status.description}")
                }
            } catch (e: Exception) {
                Log.e("MenuListViewModel", "Network error: ${e.message}")
            }
        }
    }

    private fun getMenuImage(
        sid: String,
        newVersionImage: Int,
        menuId: Int,
        onImageLoaded: (Image) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Costruisci l'URL per recuperare l'immagine del menu
                val url = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$menuId/image"
                val httpMethod = CommunicationController.HttpMethod.GET

                // Esegui la richiesta HTTP per ottenere la nuova immagine
                val response = CommunicationController.genericRequest(url, httpMethod, mapOf("sid" to sid), null)
                Log.d("MenuListViewModel", "Image fetch response status: ${response.status}")

                if (response.status == HttpStatusCode.OK) {
                    // Deserializza il corpo della risposta in un oggetto Image
                    val newImage: Image = response.body()

                    // Ottieni il DAO per le immagini dal database in un contesto asincrono
                    val db = MyApplication.database.menuImageDao()  // Accesso al database già inizializzato
                    val existingImage = db.getMenuImage(menuId)

                    // Se non esiste, inserisci la nuova immagine nel database
                    if (existingImage == null || newVersionImage > existingImage.imageVersion) {
                        db.insertOrUpdateMenuImage(
                            MenuImageEntity(
                                menuId = menuId,
                                base64 = newImage.base64,
                                imageVersion = newVersionImage
                            )
                        )
                        Log.d("MenuListViewModel", "Immagine aggiornata per menuId: $menuId")
                    }

                    // Passa l'immagine a onImageLoaded per aggiornare la UI
                    onImageLoaded(newImage)
                } else {
                    throw Exception("Error fetching image: ${response.status.description}")
                }
            } catch (e: Exception) {
                Log.e("MenuListViewModel", "Error fetching image: ${e.message}")
            }
        }
    }

}
