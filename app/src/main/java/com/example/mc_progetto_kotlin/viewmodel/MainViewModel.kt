package com.example.mc_progetto_kotlin.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sid") //come l'asyncStorage di react, creo lo storage per salvare il sid
    //private val dataStore = context.dataStore

    fun initializeUser(){
        try {
            viewModelScope.launch {
                val sid = DataStoreManager.getSid()
                val uid = DataStoreManager.getUid()

                Log.d("MainViewModel", "uid: $uid")
                Log.d("MainViewModel", "sid: $sid")
                if(sid == null) {
                    Log.d("MainViewModel", "initializeUser")
                    CommunicationController.createUser()
                }else {
                    Log.d("MainViewModel", "sid già presente")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}