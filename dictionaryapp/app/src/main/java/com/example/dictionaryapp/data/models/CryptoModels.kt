package com.example.dictionaryapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AssetsResponse(
    val data: List<Asset>
)

@Serializable
data class AssetResponse(
    val data: Asset
)

@Serializable
data class Asset(
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val supply: String,
    val maxSupply: String?,
    val marketCapUsd: String,
    val volumeUsd24Hr: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val vwap24Hr: String?
)

