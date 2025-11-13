package com.example.dictionaryapp.data.repository

import com.example.dictionaryapp.data.local.AssetDao
import com.example.dictionaryapp.data.local.DataStoreManager
import com.example.dictionaryapp.data.local.toAsset
import com.example.dictionaryapp.data.local.toEntity
import com.example.dictionaryapp.data.models.Asset
import com.example.dictionaryapp.data.remote.CryptoApiService
import kotlinx.coroutines.flow.Flow

class CryptoRepository(
    private val apiService: CryptoApiService,
    private val assetDao: AssetDao,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getAssets(forceOnline: Boolean = false): Result<List<Asset>> {
        return try {
            println("🔵 Repository: Iniciando getAssets()")

            val response = apiService.getAssets() // Llama directamente a la API
            println("✅ Repository: ${response.data.size} assets obtenidos")

            // Guardar en base de datos local para acceso offline
            saveAssetsOffline(response.data)
            Result.success(response.data)

        } catch (e: Exception) {
            println("❌ Repository: Exception - ${e.javaClass.simpleName}")
            println("❌ Repository: Exception message - ${e.message}")
            e.printStackTrace()

            // Si hay error, intenta recuperar desde local
            val localAssets = assetDao.getAllAssets().map { it.toAsset() }
            if (localAssets.isNotEmpty()) {
                println("✅ Repository: ${localAssets.size} assets locales")
                Result.success(localAssets)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getAssetBySlug(slug: String): Result<Asset> {
        return try {
            println("🔵 Repository: Iniciando getAssetBySlug($slug)")

            val response = apiService.getAssetBySlug(slug)
            println("✅ Repository: Asset obtenido - ${response.data.name}")

            // Guardar en base de datos local
            assetDao.insertAssets(listOf(response.data.toEntity()))
            Result.success(response.data)

        } catch (e: Exception) {
            println("❌ Repository: Exception - ${e.javaClass.simpleName}")
            println("❌ Repository: Exception message - ${e.message}")
            e.printStackTrace()

            // Intentar recuperar desde local si hay error
            val localAsset = assetDao.getAssetById(slug)?.toAsset()
            if (localAsset != null) {
                println("✅ Repository: Asset local encontrado")
                Result.success(localAsset)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun saveAssetsOffline(assets: List<Asset>) {
        assetDao.clearAll()
        assetDao.insertAssets(assets.map { it.toEntity() })
        dataStoreManager.saveTimestamp(System.currentTimeMillis())
    }

    fun getLastSaveTimestamp(): Flow<Long?> {
        return dataStoreManager.getTimestamp()
    }
}
