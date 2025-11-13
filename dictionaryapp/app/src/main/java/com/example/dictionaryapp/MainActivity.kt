package com.example.dictionaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.dictionaryapp.data.local.AppDatabase
import com.example.dictionaryapp.data.local.DataStoreManager
import com.example.dictionaryapp.data.remote.CryptoApiService
import com.example.dictionaryapp.data.remote.HttpClientFactory
import com.example.dictionaryapp.data.repository.CryptoRepository
import com.example.dictionaryapp.ui.navigation.AssetDetailScreen
import com.example.dictionaryapp.ui.navigation.AssetsListScreen
import com.example.dictionaryapp.ui.screens.AssetDetailScreen
import com.example.dictionaryapp.ui.screens.AssetsListScreen
import com.example.dictionaryapp.ui.theme.CryptoAppTheme
import com.example.dictionaryapp.ui.viewmodel.CryptoViewModel
import com.example.dictionaryapp.ui.viewmodel.CryptoViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: CryptoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val httpClient = HttpClientFactory.create()
        val apiService = CryptoApiService(httpClient)
        val dataStoreManager = DataStoreManager(applicationContext)
        val repository = CryptoRepository(apiService, database.assetDao(), dataStoreManager)
        CryptoViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CryptoApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun CryptoApp(viewModel: CryptoViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AssetsListScreen
    ) {
        composable<AssetsListScreen> {
            AssetsListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { assetId ->
                    navController.navigate(AssetDetailScreen(assetId))
                }
            )
        }

        composable<AssetDetailScreen> { backStackEntry ->
            val detail: AssetDetailScreen = backStackEntry.toRoute()
            AssetDetailScreen(
                assetId = detail.assetId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}