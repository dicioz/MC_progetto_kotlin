package com.example.mc_progetto_kotlin.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun LocationPermissionHandler(onLocationRetrieved: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val hasPermission = remember { mutableStateOf(false) }
    val locationText = remember { mutableStateOf("Posizione non disponibile") }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission.value = isGranted
        Log.d("MainActivity", "Permission granted: $isGranted")
    }

    LaunchedEffect(Unit) {
        if (checkLocationPermission(context)) {
            hasPermission.value = true
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        locationText.value = "Lat: ${location.latitude}, Lon: ${location.longitude}"
                        onLocationRetrieved(location.latitude, location.longitude) // Passa la latitudine e longitudine
                    } else {
                        locationText.value = "Impossibile ottenere la posizione"
                    }
                }
                .addOnFailureListener { e ->
                    locationText.value = "Errore durante il calcolo della posizione: ${e.message}"
                }
        } else {
            locationText.value = "Sto richiedendo i permessi"
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (hasPermission.value) {
        Log.d("LocationPermission", "Il permesso è stato concesso, ora posso usare la posizione.")
    } else {
        Log.d("LocationPermission", "Il permesso non è stato concesso.")
    }
}