package com.example.dictionaryapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dictionaryapp.data.models.Asset

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey
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

fun AssetEntity.toAsset() = Asset(
    id = id,
    rank = rank,
    symbol = symbol,
    name = name,
    supply = supply,
    maxSupply = maxSupply,
    marketCapUsd = marketCapUsd,
    volumeUsd24Hr = volumeUsd24Hr,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr,
    vwap24Hr = vwap24Hr
)

fun Asset.toEntity() = AssetEntity(
    id = id,
    rank = rank,
    symbol = symbol,
    name = name,
    supply = supply,
    maxSupply = maxSupply,
    marketCapUsd = marketCapUsd,
    volumeUsd24Hr = volumeUsd24Hr,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr,
    vwap24Hr = vwap24Hr
)