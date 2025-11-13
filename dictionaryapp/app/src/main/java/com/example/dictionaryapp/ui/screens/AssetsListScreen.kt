package com.example.dictionaryapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dictionaryapp.data.models.Asset
import com.example.dictionaryapp.ui.viewmodel.CryptoViewModel
import com.example.dictionaryapp.ui.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsListScreen(
    viewModel: CryptoViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val assetsState by viewModel.assetsState.collectAsState()
    val lastSaveTimestamp by viewModel.lastSaveTimestamp.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.loadAssets()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criptomonedas") },
                actions = {
                    IconButton(onClick = { viewModel.saveOffline() }) {
                        Icon(Icons.Default.Download, contentDescription = "Ver offline")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = assetsState) {
                is UiState.Idle -> {
                    // Do nothing
                }

                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Status indicator
                        DataSourceIndicator(lastSaveTimestamp)

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { asset ->
                                AssetCard(
                                    asset = asset,
                                    onClick = { onNavigateToDetail(asset.id) }
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAssets() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataSourceIndicator(lastSaveTimestamp: Long?) {
    val text = if (lastSaveTimestamp != null) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        "Viendo data del ${sdf.format(Date(lastSaveTimestamp))}"
    } else {
        "Viendo data más reciente"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun AssetCard(asset: Asset, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = asset.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${formatPrice(asset.priceUsd)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val change = asset.changePercent24Hr.toDoubleOrNull() ?: 0.0
                val isPositive = change >= 0

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = if (isPositive)
                                Color(0xFF4CAF50).copy(alpha = 0.2f)
                            else
                                Color(0xFFF44336).copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", change)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun formatPrice(price: String): String {
    val priceDouble = price.toDoubleOrNull() ?: return price
    return when {
        priceDouble >= 1000 -> String.format("%,.2f", priceDouble)
        priceDouble >= 1 -> String.format("%.2f", priceDouble)
        priceDouble >= 0.01 -> String.format("%.4f", priceDouble)
        else -> String.format("%.8f", priceDouble)
    }
}