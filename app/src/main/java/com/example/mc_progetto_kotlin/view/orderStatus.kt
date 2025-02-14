package com.example.mc_progetto_kotlin.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mapboxexample.UserLocation
import com.mapbox.geojson.Point
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.example.mc_progetto_kotlin.R
import com.example.mc_progetto_kotlin.repository.getCurrentLocation
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.viewmodel.OrderStatusViewModel
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DeliveryStatusScreen() {
    val context = LocalContext.current

    // Ottieni il ViewModel e osserva gli state flow
    val orderStatusViewModel: OrderStatusViewModel = viewModel()
    val orderStatus by orderStatusViewModel.orderStatus.collectAsState() // stato dell'ordine
    val menuName by orderStatusViewModel.menuName.collectAsState()
    val startingLocation by orderStatusViewModel.menuStartingLocation.collectAsState() // da dove parte il menu

    // Stato per la posizione dell'utente
    val userLocationState = remember { mutableStateOf<UserLocation?>(null) }

    // Primo LaunchedEffect: ottieni la posizione e l'oid, poi aggiorna l'ordine ogni 5 secondi
    LaunchedEffect(Unit) {
        val loc = getCurrentLocation(context)
        userLocationState.value = loc
        if (loc == null) {
            Log.e("OrderStatus", "Impossibile ottenere la posizione")
            return@LaunchedEffect
        }
        val oid = DataStoreManager.getOid()
        if (oid == 0) {
            Log.e("OrderStatus", "Impossibile ottenere l'oid")
            return@LaunchedEffect
        }
        // Chiamata iniziale per aggiornare lo stato dell'ordine
        orderStatusViewModel.getOrderStatus(oid)
        // Aggiornamento periodico ogni 5 secondi finché lo status è "ON_DELIVERY" o è null
        while (orderStatus?.status == "ON_DELIVERY" || orderStatus == null) {
            orderStatusViewModel.getOrderStatus(oid)
            delay(5000)
        }
    }

    // Aggiorna il nome del menu una sola volta (se non è ancora stato impostato)
    LaunchedEffect(orderStatus) {
        if (userLocationState.value != null && (menuName == null || menuName!!.isEmpty())) {
            orderStatusViewModel.getMenuName(userLocationState.value!!.latitude, userLocationState.value!!.longitude)
        }
    }

    // Imposta il viewport della mappa usando la posizione utente (o default se non disponibile)
    val mapViewportState = rememberMapViewportState {
        val centerPoint = userLocationState.value?.let {
            Point.fromLngLat(it.longitude, it.latitude)
        } ?: Point.fromLngLat(9.19, 45.4642)
        setCameraOptions {
            center(centerPoint)
            zoom(15.5)
        }
    }

    // Funzione per formattare il timestamp in ora locale
    fun formatTimestampToLocalTime(timestamp: String?): String {
        return try {
            val utcDateTime = ZonedDateTime.parse(timestamp) // Converte il timestamp UTC
            val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault()) // Converte al fuso orario locale
            val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) // Formattazione in HH:mm
            localDateTime.format(formatter)
        } catch (e: Exception) {
            "Orario non disponibile"
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Titolo
        Text(
            text = "Stato dell'ordine",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        // Informazioni testuali
        Text(
            text = "Hai ordinato: ${menuName ?: "Caricamento..."}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Stato ordine: ${orderStatus?.status ?: "Caricamento..."}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        orderStatus?.let {
            if (orderStatus?.expectedDeliveryTimestamp != null) {
                Text(
                    text = "Consegna prevista: ${formatTimestampToLocalTime(orderStatus?.expectedDeliveryTimestamp)}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Consegna effettuata alle: ${formatTimestampToLocalTime(orderStatus?.deliveryTimestamp)}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Card che contiene la mappa
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState
            ) {
                // MapEffect per aggiornare lo stile e disegnare la linea
                MapEffect(orderStatus, userLocationState.value) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }
                    mapViewportState.transitionToFollowPuckState()

                    // Disegna la linea tra la posizione utente e quella dell'ordine
                    if (userLocationState.value != null && orderStatus?.currentPosition != null) {
                        val userPoint = Point.fromLngLat(
                            userLocationState.value!!.longitude,
                            userLocationState.value!!.latitude
                        )
                        // Se COMPLETED, sovrappone l'ordine alla posizione utente
                        val orderMarkerPoint = if (orderStatus?.status == "COMPLETED") {
                            userPoint
                        } else {
                            Point.fromLngLat(
                                orderStatus!!.currentPosition.lng,
                                orderStatus!!.currentPosition.lat
                            )
                        }
                        val lineString = LineString.fromLngLats(listOf(userPoint, orderMarkerPoint))
                        val lineFeature = Feature.fromGeometry(lineString)

                        val sourceId = "route-source"
                        val style = mapView.mapboxMap.style
                        if (style != null) {
                            if (style.getSource(sourceId) == null) {
                                val geoJsonSource = geoJsonSource(sourceId) {
                                    feature(lineFeature)
                                }
                                style.addSource(geoJsonSource)
                            } else {
                                val source = style.getSourceAs<com.mapbox.maps.extension.style.sources.generated.GeoJsonSource>(sourceId)
                                source?.feature(lineFeature)
                            }

                            val layerId = "route-layer"
                            if (style.getLayer(layerId) == null) {
                                val lineLayer = lineLayer(layerId, sourceId) {
                                    lineColor("blue")
                                    lineWidth(4.0)
                                }
                                style.addLayer(lineLayer)
                            }
                        }
                    }
                }

                // Marker per la posizione dell'utente 
                val markerUser = rememberIconImage(
                    key = "user-marker",
                    painter = painterResource(R.drawable.marker)
                )

                val shopPosition = rememberIconImage(
                    key = "shop-marker",
                    painter = painterResource(R.drawable.shop)
                )

                startingLocation?.let{ shopPos ->
                    PointAnnotation(
                        point = Point.fromLngLat(shopPos.lng, shopPos.lat)
                    ) {
                        iconImage = shopPosition
                        iconSize = 0.1
                    }
                }

                userLocationState.value?.let { loc ->
                    PointAnnotation(
                        point = Point.fromLngLat(loc.longitude, loc.latitude)
                    ) {
                        iconImage = markerUser
                        iconSize = 0.3
                    }
                }

                // Marker per la posizione dell'ordine
                val markerOrder = rememberIconImage(
                    key = "order-marker",
                    painter = painterResource(R.drawable.marker)
                )
                if (orderStatus?.status == "COMPLETED" && userLocationState.value != null) {
                    // Se COMPLETED, il marker dell'ordine è nella stessa posizione dell'utente
                    PointAnnotation(
                        point = Point.fromLngLat(
                            userLocationState.value!!.longitude,
                            userLocationState.value!!.latitude
                        )
                    ) {
                        iconImage = markerOrder
                        iconSize = 0.3
                    }
                } else {
                    orderStatus?.currentPosition?.let { currentPos ->
                        PointAnnotation(
                            point = Point.fromLngLat(currentPos.lng, currentPos.lat)
                        ) {
                            iconImage = markerOrder
                            iconSize = 0.3
                        }
                    }
                }
            }
        }
    }
}