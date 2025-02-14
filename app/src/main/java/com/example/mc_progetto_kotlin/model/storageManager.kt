package com.example.mc_progetto_kotlin.model

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.room.*
import com.example.mc_progetto_kotlin.view.MenuListScreen

// Definizione dell’entità per salvare le immagini dei menu
@Entity(tableName = "menu_images")
data class MenuImageEntity(
    @PrimaryKey val menuId: Int,
    val base64: String,
    val imageVersion: Int
)

// DAO per le operazioni sul database
@Dao
interface MenuImageDao {

    // Inserisce o aggiorna l’immagine (in caso di conflitto sostituisce il record)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuImage(menuImage: MenuImageEntity)

    // Recupera l’immagine (con tutte le informazioni) per un determinato menuId
    @Query("SELECT * FROM menu_images WHERE menuId = :menuId LIMIT 1")
    suspend fun getMenuImage(menuId: Int): MenuImageEntity?

    // Recupera solo la versione dell’immagine per un determinato menuId
    @Query("SELECT imageVersion FROM menu_images WHERE menuId = :menuId LIMIT 1")
    suspend fun getImageVersion(menuId: Int): Int?

    // Funzione di transazione che inserisce o aggiorna in base alla versione
    @Transaction
    suspend fun insertOrUpdateMenuImage(newImage: MenuImageEntity) {
        val existingImage = getMenuImage(newImage.menuId)
        // Se non esiste o la nuova versione è maggiore, inserisce/aggiorna
        if (existingImage == null || newImage.imageVersion > existingImage.imageVersion) {
            insertMenuImage(newImage)
        }
    }

    // (Opzionale) Recupera tutte le immagini per debug o altre operazioni
    @Query("SELECT * FROM menu_images")
    fun getAllImages(): kotlinx.coroutines.flow.Flow<List<MenuImageEntity>>
}

@Database(entities = [MenuImageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuImageDao(): MenuImageDao
}

class MyApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        // Inizializza direttamente il database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "menu_images"
        ).build()
    }
}


