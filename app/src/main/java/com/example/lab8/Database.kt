package com.example.lab8

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacterById(characterId: Int): CharacterEntity?

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()
}

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: Int): LocationEntity?

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
}

@Database(
    entities = [CharacterEntity::class, LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rickandmorty_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ============================================
// Mappers: Entity <-> Model
// ============================================

fun Character.toEntity() = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    image = image
)

fun CharacterEntity.toModel() = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    image = image
)

fun Location.toEntity() = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)

fun LocationEntity.toModel() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)