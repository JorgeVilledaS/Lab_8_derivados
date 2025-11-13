package com.example.dictionaryapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object AssetsListScreen

@Serializable
data class AssetDetailScreen(val assetId: String)