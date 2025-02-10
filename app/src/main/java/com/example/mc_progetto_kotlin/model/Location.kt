package com.example.mapboxexample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.location.Location as AndroidLocation
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

// Modello di Location personalizzato nel tuo progetto
data class UserLocation(val latitude: Double, val longitude: Double)


//@Composable
//fun LocationPermissionApp() {
//
//    val context = LocalContext.current
//    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//    var locationText by remember { mutableStateOf("Checking location permission") }
//    var hasPermission by remember { mutableStateOf(false) }
//
//    // Launcher per richiedere i permessi
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        hasPermission = isGranted
//        if (isGranted) {
//            Log.d("MainActivity", "Posso calcolare la posizione")
//            // Ottieni la posizione
//            fetchLocation(fusedLocationClient, context) { location ->
//                locationText = "Lat: ${location.latitude}, Lon: ${location.longitude}"
//            }
//        } else {
//            Log.d("MainActivity", "Caso in cui l'utente non ha dato i permessi")
//            locationText = "Permesso negato"
//        }
//    }
//
//    //esequiamo il codice solo una volta, permette di ottenere la posizione
//    LaunchedEffect(Unit) {
//        hasPermission = checkLocationPermission(context)
//        if (hasPermission) {
//            fetchLocation(fusedLocationClient, context) { location ->
//                locationText = "Lat: ${location.latitude}, Lon: ${location.longitude}"
//            }
//        } else {
//            locationText = "Sto richiedendo i permessi"
//            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
//
//    // Mostra il risultato
//    Text(text = locationText)
//}

fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

// Funzione che mappa la posizione da Android a quella del tuo progetto
@SuppressLint("MissingPermission")
fun fetchLocation(fusedLocationClient: FusedLocationProviderClient, context: Context, onLocationFetched: (UserLocation) -> Unit) {
    val cancellationTokenSource = CancellationTokenSource()

    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
        .addOnSuccessListener { androidLocation: AndroidLocation ->
            // Mappatura da AndroidLocation a Location del tuo progetto
            val location = UserLocation(
                latitude = androidLocation.latitude,
                longitude = androidLocation.longitude
            )
            onLocationFetched(location)
        }
        .addOnFailureListener { exception ->
            Log.e("MainActivity", "Errore durante la lettura della posizione: ${exception.message}")
        }
}
