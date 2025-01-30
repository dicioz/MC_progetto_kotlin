package com.example.mc_progetto_kotlin.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mc_progetto_kotlin.model.CommunicationController
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sid") //come l'asyncStorage di react, creo lo storage per salvare il sid
    //private val dataStore = context.dataStore
    fun initializeUser(){
        try {
            viewModelScope.launch {

                if(CommunicationController.sid == "") {
                    Log.d("MainViewModel", "initializeUser")
                    Log.d("MainViewModel", CommunicationController.sid)
                    //CommunicationController.createUser()
                    //salva il sid nello storage
//                    dataStore.edit{ pref ->
//                        pref[sid] = CommunicationController.sid
//
//                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}