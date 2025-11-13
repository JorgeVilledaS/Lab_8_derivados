package com.example.dictionaryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.data.models.Asset
import com.example.dictionaryapp.data.repository.CryptoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState<out T> {
    class Idle<T> : UiState<T>()
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T, val isFromCache: Boolean = false) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
}

class CryptoViewModel(
    private val repository: CryptoRepository
) : ViewModel() {

    private val _assetsState = MutableStateFlow<UiState<List<Asset>>>(UiState.Idle())
    val assetsState: StateFlow<UiState<List<Asset>>> = _assetsState.asStateFlow()

    private val _assetDetailState = MutableStateFlow<UiState<Asset>>(UiState.Idle())
    val assetDetailState: StateFlow<UiState<Asset>> = _assetDetailState.asStateFlow()

    val lastSaveTimestamp = repository.getLastSaveTimestamp()

    fun loadAssets() {
        viewModelScope.launch {
            _assetsState.value = UiState.Loading()

            val result = withContext(Dispatchers.IO) {
                repository.getAssets()
            }

            result.fold(
                onSuccess = { assets ->
                    _assetsState.value = UiState.Success(assets)
                },
                onFailure = { error ->
                    _assetsState.value = UiState.Error(
                        error.message ?: "Error al cargar criptomonedas"
                    )
                }
            )
        }
    }

    fun loadAssetDetail(slug: String) {
        viewModelScope.launch {
            _assetDetailState.value = UiState.Loading()

            val result = withContext(Dispatchers.IO) {
                repository.getAssetBySlug(slug)
            }

            result.fold(
                onSuccess = { asset ->
                    _assetDetailState.value = UiState.Success(asset)
                },
                onFailure = { error ->
                    _assetDetailState.value = UiState.Error(
                        error.message ?: "Error al cargar detalles"
                    )
                }
            )
        }
    }

    fun saveOffline() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _assetsState.value
            if (currentState is UiState.Success) {
                repository.saveAssetsOffline(currentState.data)
            }
        }
    }
}

class CryptoViewModelFactory(
    private val repository: CryptoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CryptoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
