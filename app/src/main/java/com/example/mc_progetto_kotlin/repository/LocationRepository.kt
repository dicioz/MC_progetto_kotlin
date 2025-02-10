package com.example.mc_progetto_kotlin.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location as AndroidLocation
import android.util.Log
import com.example.mapboxexample.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): UserLocation? {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    return try {
        val cancellationTokenSource = CancellationTokenSource()
        val androidLocation: AndroidLocation? =
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token).await()
        if (androidLocation != null) {
            UserLocation(
                latitude = androidLocation.latitude,
                longitude = androidLocation.longitude
            )
        } else {
            Log.e("LocationRepository", "Android location is null")
            null
        }
    } catch (e: Exception) {
        Log.e("LocationRepository", "Errore durante il recupero della posizione: ${e.message}")
        null
    }
}
