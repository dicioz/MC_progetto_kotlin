package com.example.mc_progetto_kotlin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.ui.platform.LocalContext
import com.example.mc_progetto_kotlin.model.DataStoreManager
import com.example.mc_progetto_kotlin.view.DeliveryStatusScreen
import com.example.mc_progetto_kotlin.view.MenuDetailsScreen
import com.example.mc_progetto_kotlin.view.MenuListScreen
import com.example.mc_progetto_kotlin.view.ProfileScreen
import kotlinx.coroutines.flow.collect

@Composable
fun MainAppNavHost(startDestination: String) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Salva la rotta corrente ogni volta che cambia
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val currentRoute = backStackEntry.destination.route
            if (currentRoute != null) {
                DataStoreManager.saveLastPage(currentRoute)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Menu List") },
                    label = { Text("Menu") },
                    selected = false,
                    onClick = { navController.navigate("menuList") }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Order Status") },
                    label = { Text("Ordine") },
                    selected = false,
                    onClick = { navController.navigate("deliveryStatus") }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profilo") },
                    selected = false,
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable("menuList") {
                MenuListScreen { mid ->
                    navController.navigate("menuDetails/${mid}")
                }
            }
            composable("menuDetails/{mid}") { backStackEntry ->
                val mid = backStackEntry.arguments?.getString("mid")?.toIntOrNull() ?: -1
                MenuDetailsScreen(
                    menuId = mid,
                    onPurchase = { selectedMenuId ->
                        // Gestisci l'acquisto qui
                    },
                    navController = navController
                )
            }
            composable("deliveryStatus") {
                DeliveryStatusScreen()
            }
        }
    }
}
