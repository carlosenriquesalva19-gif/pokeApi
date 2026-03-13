package com.example.pokeapi.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokeapi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBack: () -> Unit,
    viewModel: PokemonDetailViewModel = viewModel()
) {
    LaunchedEffect(pokemonId) {
        viewModel.loadPokemon(pokemonId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme
                        .colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is PokemonDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is PokemonDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message,
                        color = MaterialTheme.colorScheme.error)
                }
            }
            is PokemonDetailUiState.Success -> {
                val pokemon = state.pokemon
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment =
                    Alignment.CenterHorizontally
                ) {
                    // Imagen oficial
                    AsyncImage(
                        model = pokemon.imageUrl,
                        contentDescription = pokemon.name,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    // Nombre en español
                    Text(
                        "#${pokemon.id} ${pokemon.name}",
                        style = MaterialTheme
                            .typography.headlineMedium
                    )
                    // Categoría (genus)
                    if (pokemon.genus.isNotEmpty()) {
                        Text(
                            pokemon.genus,
                            style = MaterialTheme
                                .typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme
                                .colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    // Tipos en español
                    Row(horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
                    ) {
                        pokemon.types.forEach { type ->
                            AssistChip(
                                onClick = {},
                                label = { Text(type) }
                            )
                        }
                    }
                    // Descripción de la Pokédex
                    if (pokemon.description.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Card(elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                        ) {
                            Text(
                                pokemon.description,
                                modifier = Modifier
                                    .padding(12.dp),
                                style = MaterialTheme
                                    .typography.bodyMedium,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    // Altura y peso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                        Arrangement.SpaceEvenly
                    ) {
                        InfoCard(stringResource(R.string.label_height),
                            stringResource(R.string.unit_meter, pokemon.height / 10.0))
                        InfoCard(stringResource(R.string.label_weight),
                            stringResource(R.string.unit_kg, pokemon.weight / 10.0))
                    }
                    Spacer(Modifier.height(16.dp))
                    // Estadísticas base en español
                    Text(
                        stringResource(R.string.title_stats),
                        style = MaterialTheme
                            .typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    pokemon.stats.forEach { stat ->
                        StatBar(
                            name = stat.name,
                            value = stat.value
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    Card(elevation =
    CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label,
                style = MaterialTheme.typography.bodySmall)
            Text(value,
                style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun StatBar(name: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.width(72.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$value",
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
        )
    }
}
