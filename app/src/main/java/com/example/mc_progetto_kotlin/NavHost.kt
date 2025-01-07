package com.example.mc_progetto_kotlin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List

@Composable
fun MainAppNavHost() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation {
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
    ) { innerPadding ->  // Applica innerPadding qui
        NavHost(
            navController = navController,
            startDestination = "menuList",
            modifier = Modifier.padding(innerPadding) // Aggiungi innerPadding al NavHost
        ) {
            composable("profile") {
                ProfileScreen { navController.navigate("menuList") }
            }
            composable("menuList") {
                MenuListScreen { menuName ->
                    navController.navigate("menuDetails/$menuName")
                }
            }
            composable("menuDetails/{menuName}") { backStackEntry ->
                val menuName = backStackEntry.arguments?.getString("menuName") ?: ""
                MenuDetailsScreen(menuName) {
                    navController.navigate("deliveryStatus")
                }
            }
            composable("deliveryStatus") {
                DeliveryStatusScreen()
            }
        }
    }
}
