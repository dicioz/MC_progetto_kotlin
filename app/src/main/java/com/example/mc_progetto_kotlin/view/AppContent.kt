package com.example.mc_progetto_kotlin.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.mapboxexample.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.example.mc_progetto_kotlin.MainAppNavHost

@SuppressLint("MissingPermission")
fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationFetched: (UserLocation?) -> Unit
) {
    val cancellationTokenSource = CancellationTokenSource()
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
        .addOnSuccessListener { androidLocation ->
            if (androidLocation != null) {
                onLocationFetched(UserLocation(androidLocation.latitude, androidLocation.longitude))
            } else {
                Log.e("AppContent", "Posizione non disponibile")
                onLocationFetched(null)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("AppContent", "Errore durante il fetching della posizione: ${exception.message}")
            onLocationFetched(null)
        }
}

@Composable
fun AppContent(startDestination: String) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var permissionGranted by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) }
    var userLocation by remember { mutableStateOf<UserLocation?>(null) }
    var loading by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            loading = true
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            fetchLocation(fusedLocationClient, context) { location ->
                userLocation = location
                loading = false
            }
        } else {
            loading = false
        }
    }

    when {
        loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        !permissionGranted -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Permesso di localizzazione negato. Impossibile mostrare i menu.")
            }
        }
        userLocation == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Impossibile ottenere la posizione.")
            }
        }
        else -> {
            // Una volta che la posizione è disponibile, avvia il NavHost
            MainAppNavHost(startDestination)
        }
    }
}
