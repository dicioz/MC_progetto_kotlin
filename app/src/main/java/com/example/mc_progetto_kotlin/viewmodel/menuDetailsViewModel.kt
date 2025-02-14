package com.example.mc_progetto_kotlin.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapboxexample.UserLocation
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.repository.getCurrentLocation
import com.example.mc_progetto_kotlin.viewmodel.Image
import com.example.mc_progetto_kotlin.viewmodel.MenuItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.mc_progetto_kotlin.viewmodel.MenuListViewModel

class MenuDetailsViewModel : ViewModel() {
    private val _menu: MutableLiveData<MenuItem> = MutableLiveData()
    val menu: MutableLiveData<MenuItem> = _menu
    private var userLocation: UserLocation? = null
    private var sid: String = ""

    // funzione per ottenere i dettagli del menu
    fun loadMenu(menuId: Int, context: Context) {
        viewModelScope.launch {
            try {
                sid = DataStoreManager.getSid() ?: ""
                if (sid == "") {
                    Log.e("MenuDetailsViewModel", "Sid non presente")
                    return@launch
                }
                userLocation = getCurrentLocation(context)
                if (userLocation == null) {
                    Log.e("MenuListViewModel", "Impossibile ottenere la posizione")
                    return@launch
                }
                val url = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$menuId"

                val httpMethod = CommunicationController.HttpMethod.GET
                val queryParams = mapOf(
                    "lat" to userLocation!!.latitude,
                    "lng" to userLocation!!.longitude,
                    "sid" to sid
                )
                Log.d("MenuDetailsViewModel", "Richiesta del menu con id $menuId")
                val response: HttpResponse =
                    CommunicationController.genericRequest(url, httpMethod, queryParams, null)
                val url2 = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$menuId/image"
                val httpMethod2 = CommunicationController.HttpMethod.GET
                val image = CommunicationController.genericRequest(
                    url2,
                    httpMethod2,
                    mapOf("sid" to sid),
                    null
                )

                if (response.status == HttpStatusCode.OK && image.status == HttpStatusCode.OK) {
                    Log.d(
                        "MenuDetailsViewModel",
                        "Menu caricato con successo: ${response.body<MenuItem>()}"
                    )
                    val result: MenuItem = response.body<MenuItem>()
                    val imageResult: Image = image.body<Image>()
                    _menu.value = result.apply {
                        this.image = imageResult.base64
                    }
                } else {
                    Log.e("MenuDetailsViewModel", "Errore nella richiesta del menu/immagine")
                }
            } catch (e: Exception) {
                Log.e("MenuDetailsViewModel", "Errore di rete: ${e.message}")
            }
        }
    }


    @Serializable
    data class BuyMenuRequest(
        val sid: String,
        val deliveryLocation: DeliveryLocation
    )

    @Serializable
    data class DeliveryLocation(
        val lat: Double,
        val lng: Double
    )

    @Serializable
    data class BuyMenuResponse(
        val oid: Int,
        val mid: Int,
        val uid: Int,
//        val deliveryTimestamp: String,
        val creationTimestamp: String,
        val status: String,
        val deliveryLocation: Location,
        val expectedDeliveryTimestamp: String,
        val currentPosition: Location
    )


//
//    {
//        "oid": 0,
//        "mid": 0,
//        "uid": 0,
//        "creationTimestamp": "2025-02-09T14:03:00.157Z",
//        "status": "ON_DELIVERY",
//        "deliveryLocation": {
//        "lat": 45.4642,
//        "lng": 9.19
//    },
//        "deliveryTimestamp": "2025-02-09T14:03:00.157Z",
//        "expectedDeliveryTimestamp": "2025-02-09T14:03:00.157Z",
//        "currentPosition": {
//        "lat": 45.4642,
//        "lng": 9.19
//    }
//    }

    fun buyMenu(mid: Int, onBuySuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (userLocation == null) {
                    Log.e("MenuDetailsViewModel", "Impossibile ottenere la posizione")
                    return@launch
                }
                if (sid.isEmpty()) {
                    Log.e("MenuDetailsViewModel", "SID non presente")
                    return@launch
                }

                val endpoint = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$mid/buy"
                val httpMethod = CommunicationController.HttpMethod.POST

                // Creiamo il JSON corretto
                val requestBody = BuyMenuRequest(
                    sid = sid,
                    deliveryLocation = DeliveryLocation(
                        lat = userLocation!!.latitude,
                        lng = userLocation!!.longitude
                    )
                )

                // Stampiamo il JSON che stiamo inviando
                val jsonBody = Json.encodeToString(requestBody)
                Log.d("MenuDetailsViewModel", "Body della richiesta: $jsonBody")

                // Effettuiamo la richiesta HTTP
                val response = CommunicationController.genericRequest(
                    endpoint,
                    httpMethod,
                    emptyMap(),
                    requestBody
                )

                // Logghiamo la risposta
                Log.d("MenuDetailsViewModel", "Status della risposta: ${response.status}")
                Log.d("MenuDetailsViewModel", response.toString())

                if (response.status == HttpStatusCode.OK) {
                    Log.d("MenuDetailsViewModel", "Menu acquistato con successo")
                    onBuySuccess("true")
                    val result = response.body<BuyMenuResponse>()
                    Log.d("MenuDetailsViewModel", "OID: ${result.oid}")
                    DataStoreManager.saveOid(result.oid)
                } else if(response.status.value == 403){
                    Log.e(
                        "MenuDetailsViewModel",
                        "Errore nell'acquisto del menu: ${response.status}"
                    )
                    onBuySuccess("Invalid Card")
                } else if(response.status.value == 409){
                    Log.e("MenuDetailsViewModel", "Hai già un altro ordine attivo ${response.status}")
                    onBuySuccess("Hai già un altro ordine attivo")
                }
            } catch (e: Exception) {
                Log.e("MenuDetailsViewModel", "Errore durante la richiesta: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}