package com.example.lab8

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * (Conceptos que le va a servir al Jorge del futuro)
 * Repository para Characters con enfoque Offline First
 *
 * Offline First significa:
 * 1. Primero intenta obtener datos de la base de datos local (Room)
 * 2. Si no hay datos locales, hace llamada al API
 * 3. Guarda la respuesta del API en la base de datos local
 */
class CharactersRepository(
    private val characterDao: CharacterDao,
    private val api: RickAndMortyApi
) {
    private val TAG = "CharactersRepository"

    /**
     * Obtiene todos los personajes (Offline First)
     * Retorna Flow para observar cambios en la base de datos
     */
    fun getAllCharacters(): Flow<List<CharacterEntity>> {
        return characterDao.getAllCharacters()
    }

    /**
     * Obtiene un personaje por ID desde Room
     * (Ya está en local porque se sincronizó en la lista)
     */
    suspend fun getCharacterById(id: Int): CharacterEntity? {
        return try {
            characterDao.getCharacterById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting character by id from Room", e)
            null
        }
    }

    /**
     * Sincroniza personajes desde el API
     * Se llama en el login inicial y cuando la lista está vacía
     */
    suspend fun syncCharacters(): Result<Unit> {
        return try {
            Log.d(TAG, "Starting character sync from API...")

            // Llamada al API
            val response = api.getAllCharacters()

            Log.d(TAG, "API Response: ${response.results.size} characters received")

            // Mapear DTOs a Entities
            val entities = response.results.map { it.toEntity() }

            // Guardar en Room
            characterDao.insertCharacters(entities)

            Log.d(TAG, "Characters saved to Room successfully")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing characters from API", e)
            Result.failure(e)
        }
    }

    /**
     * Verifica si hay datos en la base de datos local
     */
    suspend fun hasLocalData(): Boolean {
        return try {
            val characters = characterDao.getAllCharacters().first()
            characters.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Repository para Locations con enfoque Offline First
 */
class LocationsRepository(
    private val locationDao: LocationDao,
    private val api: RickAndMortyApi
) {
    private val TAG = "LocationsRepository"

    /**
     * Obtiene todas las locaciones (Offline First)
     * Retorna Flow para observar cambios en la base de datos
     */
    fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }

    /**
     * Obtiene una locación por ID desde Room
     * (Ya está en local porque se sincronizó en la lista)
     */
    suspend fun getLocationById(id: Int): LocationEntity? {
        return try {
            locationDao.getLocationById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location by id from Room", e)
            null
        }
    }

    /**
     * Sincroniza locaciones desde el API
     * Se llama en el login inicial y cuando la lista está vacía
     */
    suspend fun syncLocations(): Result<Unit> {
        return try {
            Log.d(TAG, "Starting locations sync from API...")

            // Llamada al API
            val response = api.getAllLocations()

            Log.d(TAG, "API Response: ${response.results.size} locations received")

            // Mapear DTOs a Entities
            val entities = response.results.map { it.toEntity() }

            // Guardar en Room
            locationDao.insertLocations(entities)

            Log.d(TAG, "Locations saved to Room successfully")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing locations from API", e)
            Result.failure(e)
        }
    }

    /**
     * Verifica si hay datos en la base de datos local
     */
    suspend fun hasLocalData(): Boolean {
        return try {
            val locations = locationDao.getAllLocations().first()
            locations.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Repository para usuario
 */
class UserRepository(private val dataStoreManager: DataStoreManager) {
    val userName: Flow<String?> = dataStoreManager.userName

    suspend fun saveUserName(name: String) {
        dataStoreManager.saveUserName(name)
    }

    suspend fun logout() {
        dataStoreManager.clearUserName()
    }
}