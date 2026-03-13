package com.example.pokeapi.ui.screens.detail

import com.example.pokeapi.domain.model.PokemonDetail

sealed interface PokemonDetailUiState {
    data object Loading : PokemonDetailUiState
    data class Success(
        val pokemon: PokemonDetail
    ) : PokemonDetailUiState
    data class Error(val message: String) : PokemonDetailUiState
}
