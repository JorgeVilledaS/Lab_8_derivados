package com.example.lab8

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CharactersRepository(
    private val characterDao: CharacterDao,
    private val characterDb: CharacterDb
) {
    fun getAllCharacters(): Flow<List<CharacterEntity>> {
        return characterDao.getAllCharacters()
    }

    suspend fun getCharacterById(id: Int): CharacterEntity? {
        delay(2000) // Simular delay de red
        return characterDao.getCharacterById(id)
    }

    suspend fun syncCharacters() {
        delay(4000) // Delay de sincronización inicial
        val characters = characterDb.getAllCharacters()
        characterDao.insertCharacters(characters.map { it.toEntity() })
    }
}

class LocationsRepository(
    private val locationDao: LocationDao,
    private val locationDb: LocationDb
) {
    fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }

    suspend fun getLocationById(id: Int): LocationEntity? {
        delay(2000) // Simular delay de red
        return locationDao.getLocationById(id)
    }

    suspend fun syncLocations() {
        delay(4000) // Delay de sincronización inicial
        val locations = locationDb.getAllLocations()
        locationDao.insertLocations(locations.map { it.toEntity() })
    }
}

class UserRepository(private val dataStoreManager: DataStoreManager) {
    val userName: Flow<String?> = dataStoreManager.userName

    suspend fun saveUserName(name: String) {
        dataStoreManager.saveUserName(name)
    }

    suspend fun logout() {
        dataStoreManager.clearUserName()
    }
}