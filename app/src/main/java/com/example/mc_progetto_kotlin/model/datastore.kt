package com.example.mc_progetto_kotlin.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

// Estensione per ottenere il DataStore dal Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

object DataStoreManager {
    private val SID = stringPreferencesKey("sid")
    private val UID = intPreferencesKey("uid")
    private val OID = intPreferencesKey("oid")
    private val menuName = stringPreferencesKey("menuName")
    private val lasPage = stringPreferencesKey("lastPage")

    // Funzione per salvare il conteggio
    fun saveSid(sid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            CommunicationController.appContext.dataStore.edit { preferences ->
                preferences[SID] = sid
            }
        }
        Log.d("DataStoreManager", "sid salvato correttamente: $sid" )
    }

    //funziione per salvare l'uid
    fun saveUid(uid: Int){
        CoroutineScope(Dispatchers.IO).launch {
            CommunicationController.appContext.dataStore.edit { preferences ->
                preferences[UID] = uid
            }
        }
        Log.d("DataStoreManager", "uid salvato correttamente: $uid")
    }

    // Funzione per ottenere il sid
    suspend fun getSid(): String? {
        return CommunicationController.appContext.dataStore.data.map { preferences ->
            preferences[SID]
        }.first()
    }

    //funzione per ottenere l'uid
    suspend fun getUid(): Int{
        return CommunicationController.appContext.dataStore.data.map { preferences ->
            preferences[UID] ?: 0
        }.first()
    }

    //funzione per salvare l'oid
    fun saveOid(oid: Int){
        CoroutineScope(Dispatchers.IO).launch {
            CommunicationController.appContext.dataStore.edit { preferences ->
                preferences[OID] = oid
            }
        }
        Log.d("DataStoreManager", "oid salvato correttamente: $oid")
    }

    //funzione per ottenere l'oid
    suspend fun getOid(): Int{
        return CommunicationController.appContext.dataStore.data.map { preferences ->
            preferences[OID] ?: 0
        }.first()
    }

    //funzione per salvare il nome del menu
    fun saveMenuName(name: String){
        CoroutineScope(Dispatchers.IO).launch {
            CommunicationController.appContext.dataStore.edit { preferences ->
                preferences[menuName] = name
            }
        }
        Log.d("DataStoreManager", "nome del menu salvato correttamente: $name")
    }

    //funzione per ottenere il nome del menu
    suspend fun getMenuName(): String?{
        return CommunicationController.appContext.dataStore.data.map { preferences ->
            preferences[menuName]
        }.first()
    }

    //funzione per salvare l'ultima pagina visitata
    fun saveLastPage(page: String){
        CoroutineScope(Dispatchers.IO).launch {
            CommunicationController.appContext.dataStore.edit { preferences ->
                preferences[lasPage] = page
            }
        }
    }

    //funzione per ottenere l'ultima pagina visitata
    suspend fun getLastPage(): String?{
        return CommunicationController.appContext.dataStore.data.map { preferences ->
            preferences[lasPage]
        }.first()
    }
}