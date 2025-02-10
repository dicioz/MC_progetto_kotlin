package com.example.mc_progetto_kotlin.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import android.util.Log

// Funzione per verificare il permesso
fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

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
fun AppContent() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var permissionGranted by remember { mutableStateOf(checkLocationPermission(context)) }
    var userLocation by remember { mutableStateOf<UserLocation?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Launcher per richiedere il permesso di posizione
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            loading = true // Quando il permesso viene accettato, avviamo il caricamento
        }
    }

    // Se il permesso non è ancora concesso, lanciamolo
    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Se il permesso è concesso, prova ad ottenere la posizione
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

    // UI condizionale aggiornata
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
            // Se non riusciamo a ottenere la posizione dopo che i permessi sono stati accettati, manteniamo il loading
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Mantieni il caricamento fino alla posizione
            }
        }
        else -> {
            // Quando la posizione è disponibile, avviamo la navigazione principale.
            MainAppNavHost()
        }
    }
}
