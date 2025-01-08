package com.example.mc_progetto_kotlin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mc_progetto_kotlin.model.CommunicationController
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    fun initializeUser(){
        try {
            viewModelScope.launch {
                if(CommunicationController.sid == "") {
                    Log.d("MainViewModel", "initializeUser")
                    CommunicationController.createUser()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}