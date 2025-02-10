package com.example.mc_progetto_kotlin.viewmodel

import android.util.Log
import android.view.Menu
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Index
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.Exception

@Serializable
data class OrderStatus(
    val oid: Int,
    val mid: Int,
    val uid: Int,
    val creationTimestamp: String,
    val status: String,
    val deliveryLocation: Location,
    @SerialName("expectedDeliveryTimestamp") val expectedDeliveryTimestamp: String? = null,
    @SerialName("deliveryTimestamp") val deliveryTimestamp: String? = null,
    val currentPosition: Location
)


class OrderStatusViewModel: ViewModel() {

    private val _orderStatus = MutableStateFlow<OrderStatus?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    private val _menuName = MutableStateFlow<String?>(null)
    val menuName = _menuName.asStateFlow()

    private var mid: Int? = null
    var sid: String = ""


    fun getOrderStatus(oid: Int) {
        viewModelScope.launch {
            try {
                sid = DataStoreManager.getSid() ?: ""
                Log.d("OrderStatusViewModel", "SID: $sid")
                if (sid.isEmpty()) {
                    return@launch
                }
                val endpoint = "https://develop.ewlab.di.unimi.it/mc/2425/order/$oid"
                val httpMethod = CommunicationController.HttpMethod.GET
                val queryParams = mapOf("sid" to sid)
                val response = CommunicationController.genericRequest(endpoint, httpMethod, queryParams)

                Log.d("OrderStatusViewModel", "HTTP Status: ${response.status}")

                if (response.status == HttpStatusCode.OK) {
                    val order = response.body<OrderStatus>()  // Prova a deserializzare
                    Log.d("OrderStatusViewModel", "Order loaded: ${order.status}")

                    mid = order.mid
                    val isCompleted = order.status == "COMPLETED"
                    _orderStatus.value = order.copy(status = if (isCompleted) "COMPLETED" else order.status)
                } else {
                    Log.e("OrderStatusViewModel", "Errore HTTP: ${response.status}")
                }
            } catch (e: Exception) {
                Log.e("OrderStatusViewModel", "Errore durante la richiesta: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }

    }


    fun getMenuName(lat: Double, lng: Double){
        viewModelScope.launch {
            try {
                if(sid == ""){
                    return@launch
                }
                val endpoint = "https://develop.ewlab.di.unimi.it/mc/2425/menu/$mid"
                val httpMethod = CommunicationController.HttpMethod.GET
                val queryParams = mapOf("sid" to sid, "lat" to lat, "lng" to lng)
                val response =
                    CommunicationController.genericRequest(endpoint, httpMethod, queryParams)
                if (response.status == HttpStatusCode.OK) {
                    val menu = response.body<MenuItem>()
                    Log.d("OrderStatusViewModel", "Menu name caricato con successo: ${menu.name}")
                    _menuName.value = menu.name
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}