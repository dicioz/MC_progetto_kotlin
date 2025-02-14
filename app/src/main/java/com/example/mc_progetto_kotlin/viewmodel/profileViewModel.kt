package com.example.mc_progetto_kotlin.viewmodel
import android.app.AlertDialog
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_progetto_kotlin.model.CommunicationController
import com.example.mc_progetto_kotlin.model.DataStoreManager
import io.ktor.client.call.body
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


@Serializable
data class Profile(
    val firstName: String,
    val lastName: String,
    val cardFullName: String,
    val cardNumber: String,
    val cardExpireMonth: Int,
    val cardExpireYear: Int,
    val cardCVV: String,
    val sid: String
)

@Serializable
data class ProfileGET(
    val firstName: String,
    val lastName: String,
    val cardFullName: String,
    val cardNumber: String,
    val cardExpireMonth: Int,
    val cardExpireYear: Int,
    val cardCVV: String,
    val uid: Int,
    val lastOid: Int?,
    val orderStatus: String?
)


//{
//    "firstName": "cc",
//    "lastName": "dd",
//    "cardFullName": "sdaasd",
//    "cardNumber": "1234567890123456",
//    "cardExpireMonth": 11,
//    "cardExpireYear": 24,
//    "cardCVV": "123",
//    "uid": 41964,
//    "lastOid": null,
//    "orderStatus": null
//}


class ProfileViewModel: ViewModel() {
    private var uid : Int? = null
    private val _profile: MutableStateFlow<ProfileGET> = MutableStateFlow(ProfileGET("", "", "", "", 0, 0, "", 0, null, null))
    val profile = _profile.asStateFlow() // Esponi il profilo come StateFlow, ossia in sola lettura



    fun saveNewDatas(newProfile: Profile, onSuccess: (Boolean) -> Unit) {
        Log.d("ProfileViewModel", "sid: ${newProfile.sid}")
        if(newProfile.cardNumber.length != 16 || newProfile.cardCVV.length != 3){
            Log.d("ProfileViewModel", "Errore di validazione")
            onSuccess(false)
            return
        }
        viewModelScope.launch {
            try {
                uid = DataStoreManager.getUid()
                Log.d("ProfileViewModel", "UID: $uid")
                val endpoint = "https://develop.ewlab.di.unimi.it/mc/2425/user/$uid"
                val response = CommunicationController.genericRequest(
                    endpoint,
                    CommunicationController.HttpMethod.PUT,
                    requestBody = newProfile
                )
                Log.d("ProfileViewModel", "Response: ${response.status.value}")

                if (response.status.value == 204) {
                    Log.d("ProfileViewModel", "Profile updated")
                    onSuccess(true)  // ✅ Segnala al Composable che il salvataggio è riuscito
                } else {
                    Log.d("ProfileViewModel", "Error updating profile")
                    onSuccess(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onSuccess(false)
            }
        }
    }

    init {
        getProfileDetails()
    }

    private fun getProfileDetails(){
        viewModelScope.launch {
            try {
                uid = DataStoreManager.getUid() ?: 0
                val endpoint = "https://develop.ewlab.di.unimi.it/mc/2425/user/$uid"
                val httpMethod = CommunicationController.HttpMethod.GET
                val sid = DataStoreManager.getSid() ?: ""
                val queryParams = mapOf("sid" to sid)
                Log.d("ProfileViewModel", "Richiesta del profilo")
                val response = CommunicationController.genericRequest(endpoint, httpMethod, queryParams)
                Log.d("ProfileViewModel", "Response: ${response.status.value}")
                if(response.status.value == 200){
                    val profile = response.body<ProfileGET>()
                    _profile.value = profile
                    Log.d("ProfileViewModel", "Profilo caricato")
                } else {
                    Log.e("ProfileViewModel", "Error loading profile")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Errore di rete: ${e.message}")
            }
        }
    }



}
