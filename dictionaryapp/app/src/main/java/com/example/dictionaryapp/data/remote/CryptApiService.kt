package com.example.dictionaryapp.data.remote

import com.example.dictionaryapp.data.models.AssetResponse
import com.example.dictionaryapp.data.models.AssetsResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class CryptoApiService(private val client: HttpClient) {

    // Obtiene la lista de criptomonedas (máximo 100 por página)
    suspend fun getAssets(): AssetsResponse {
        val response = client.get("assets")
        return response.body()
    }

    // Obtiene la información de una criptomoneda específica
    // CoinCap v3 usa "slug" (no "id")
    suspend fun getAssetBySlug(slug: String): AssetResponse {
        val response = client.get("assets/$slug")
        return response.body()
    }
}
