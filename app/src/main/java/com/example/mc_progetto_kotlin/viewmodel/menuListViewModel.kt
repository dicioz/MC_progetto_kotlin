package com.example.mc_progetto_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.mc_progetto_kotlin.model.CommunicationController
import io.ktor.client.call.body

//dati di un menu
data class MenuItem(
    val name: String,
    val description: String,
    val price: Double,
    val deliveryTime: String
)


class MenuListViewModel : ViewModel() { //estende ViewModel perche deve mantenere lo stato dei dati anche al cambiamento della UI
    // e la logica di business
    private val _menuList = MutableStateFlow<List<MenuItem>>(emptyList())
    private val _error = MutableStateFlow<String?>(null)

    //recupera i menu dal server
    fun loadMenus() {
        //avvia un nuovo coroutine
        viewModelScope.launch {
            try {
                val url = "https://develop.ewlab.di.unimi.it/mc/2425/menu"
                val httpMethod = CommunicationController.HttpMethod.GET
                val lat = 45.4789
                val lng = 9.19
                val sid = CommunicationController.sid

                val queryParameters = mapOf("lat" to lat, "lon" to lng, "sid" to sid)
                val response = CommunicationController.genericRequest(url, httpMethod, queryParameters, null)

                if (response.status.value == 200) {
                    val menus = response.body<List<MenuItem>>()
                    _menuList.value = menus
                } else {
                    _error.value = "Errore nel caricamento del menu: ${response.status.description}"
                }
            } catch (e: Exception) {
                _error.value = "Errore di rete: ${e.message}"
            }
        }
    }
}
