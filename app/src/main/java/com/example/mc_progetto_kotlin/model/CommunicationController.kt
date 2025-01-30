package com.example.mc_progetto_kotlin.model


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mc_progetto_kotlin.utils.dataStore
//import com.example.ktorexample.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json



object CommunicationController {
    private val BASE_URL = "https://develop.ewlab.di.unimi.it/mc/2425"
    var sid : String = ""
    private val TAG = CommunicationController::class.simpleName

    //crea un client http
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {//installa il plugin per la serializzazione/deserializzazione
            json(Json {
                ignoreUnknownKeys = true //ignora i campi non mappati
            })
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        DELETE,
        PUT
    }

    //funzione generica per effettuare una richiesta http
    suspend fun genericRequest(url: String, method: HttpMethod,
                               queryParameters: Map<String, Any> = emptyMap(),
                               requestBody: Any? = null) : HttpResponse {

        val urlUri = Uri.parse(url) //converte la stringa url in un oggetto Uri
        val urlBuilder = urlUri.buildUpon() //permette di costruire un url

        //aggiunge i parametri alla query urlBuilder
        queryParameters.forEach { (key, value) ->
            urlBuilder.appendQueryParameter(key, value.toString())
        }
        //costruisce l'url completo
        val completeUrlString = urlBuilder.build().toString()
        Log.d(TAG, completeUrlString)

        val request: HttpRequestBuilder.() -> Unit = {
            requestBody?.let { //se è presente un body..., se no non fa nulla
                contentType(ContentType.Application.Json)
                setBody(requestBody)//imposta il body della richiesta
            }
        }

        //effettua la richiesta http in base al metodo, con l'url completo e il body
        val result = when (method) {
            HttpMethod.GET -> client.get(completeUrlString, request)
            HttpMethod.POST -> client.post(completeUrlString, request)
            HttpMethod.DELETE -> client.delete(completeUrlString, request)
            HttpMethod.PUT -> client.put(completeUrlString, request)
        }
        return result
    }

    suspend fun createUser(): UserResponse {

        Log.d(TAG, "createUser")
        val url = "$BASE_URL/user"


        try {
            val httpResponse = genericRequest(url, HttpMethod.POST)
            val result: UserResponse = httpResponse.body()
            sid = result.sid
            Log.d(TAG, "createUser result: $sid")
            //saveSid(context, sid)
            return result
        } catch (e: Exception) {
            Log.e(TAG, "createUser error: ${e.message}")
            throw e
        }


    }

    private suspend fun saveSid(context: Context, sid: String) {
        val sidKey = stringPreferencesKey("sid")
        context.dataStore.edit { pref ->
            pref[sidKey] = sid
        }
    }

    @Serializable
    data class UserResponse(val sid: String, val uid: Int)
}

