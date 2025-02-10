package com.example.mc_progetto_kotlin

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.navigation.NavController
import com.example.mc_progetto_kotlin.view.DeliveryStatusScreen
import com.example.mc_progetto_kotlin.view.MenuDetailsScreen
import com.example.mc_progetto_kotlin.view.MenuListScreen
import com.example.mc_progetto_kotlin.view.ProfileScreen



@Composable
fun MainAppNavHost() {
    val navController = rememberNavController()

    //Scaffold è un layout e mostra un contenuto principale e un contenuto inferiore
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            //BottomNavigation è un layout che fornisce una barra di navigazione inferiore per un'applicazione
            BottomNavigation {
                //BottomNavigationItem è un elemento di navigazione inferiore che rappresenta una destinazione in un'applicazione
                BottomNavigationItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Menu List") },
                    label = { Text("Menu") },
                    selected = false, // Gestisci la selezione con uno stato
                    onClick = { navController.navigate("menuList") }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Order Status") },
                    label = { Text("Ordine") },
                    selected = false, // Gestisci la selezione con uno stato
                    onClick = { navController.navigate("deliveryStatus") }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profilo") },
                    selected = false, // Gestisci la selezione con uno stato
                    onClick = { navController.navigate("profile") }
                )
            }
        }
        //innerPadding è un valore di padding che viene applicato al contenuto principale
    ) { innerPadding ->
        //navhost serve a navigare tra le schermate dell'applicazione
        NavHost(
            navController = navController,
            startDestination = "menuList", // Schermata iniziale
            modifier = Modifier.padding(innerPadding) // Aggiungi innerPadding al NavHost
        ) {
            composable("profile") {
                ProfileScreen (navController = navController) //da mettere a posto
            }
            composable("menuList") {
                MenuListScreen { mid ->
                    //definisce una funzione che naviga alla schermata MenuDetails, queeta viene passata a MenuListScreen
                    // e viene chiamata quando si seleziona un menù
                    navController.navigate("menuDetails/${mid}")
                }
            }
            composable("menuDetails/{mid}") { backStackEntry ->
                val mid = backStackEntry.arguments?.getString("mid")?.toIntOrNull() ?: -1
                MenuDetailsScreen(mid, onPurchase = { selectedMenuId ->
                    // Qui puoi gestire l'acquisto, ad esempio navigare alla schermata dell'ordine
                    Log.d("MainAppNavHost", "Acquistato menu con ID: $selectedMenuId")
                }, navController = navController) // Passiamo anche il navController
            }
            composable("deliveryStatus") {
                DeliveryStatusScreen()
            }
        }
    }
}
