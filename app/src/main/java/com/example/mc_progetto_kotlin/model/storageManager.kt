package com.example.mc_progetto_kotlin.model

import android.app.Application
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey val sid: String
)

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

// Usa un Application Context per creare il database
class MyApplication : Application() {
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "dbtestUsers"
    ).build()

}

@Dao
interface UserDao {
    // per inserire un utente, in questo caso di tipo User perche il database contiene solo entità di tipo user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>
}

//fun checkUser() {
//    // controlla se l'utente è già presente nel database, altrimenti lo crea e lo salva
//    val userDao = MyApplication().db.userDao()
//    CoroutineScope(Dispatchers.Main).launch {
//        val checkSid = userDao.getAllUsers()
//        if (checkSid.isEmpty()) {
//            CommunicationController.createUser()
//            userDao.insertUser(User(CommunicationController.sid))
//        } else {
//            Log.d("User", CommunicationController.sid.toString())
//        }
//    }
//        val user = User(CommunicationController.sid)
//        CoroutineScope(Dispatchers.Main).launch {
//            userDao.insertUser(user)
//        }
//}


