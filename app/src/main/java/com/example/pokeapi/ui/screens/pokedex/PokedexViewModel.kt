package com.example.pokeapi.ui.screens.pokedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.data.repository.PokemonRepository
import com.example.pokeapi.domain.model.PokemonItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokedexViewModel : ViewModel() {
    private val repository = PokemonRepository()
    
    private val _uiState = MutableStateFlow<PokedexUiState>(PokedexUiState.Loading)
    val uiState: StateFlow<PokedexUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val limit = 20
    private val totalPokemon = 151 // Limitamos a la primera generación como en el repo
    val totalPages = (totalPokemon + limit - 1) / limit

    init {
        loadPage(1)
    }

    fun loadPage(page: Int) {
        if (page < 1 || page > totalPages) return
        
        _currentPage.value = page
        val offset = (page - 1) * limit
        
        viewModelScope.launch {
            _uiState.value = PokedexUiState.Loading
            try {
                val pokemon = repository.getPokemonList(limit, offset)
                _uiState.value = PokedexUiState.Success(pokemon)
            } catch (e: Exception) {
                _uiState.value = PokedexUiState.Error(e.message ?: "Error de red")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadPage(_currentPage.value)
        } else {
            // Para búsqueda, solemos buscar en todo el set o el actual. 
            // Dado que la API no permite búsqueda por nombre parcial en lista de forma fácil sin cargar todo,
            // aquí filtramos lo que ya tenemos o cargamos una muestra mayor.
            // Para mantener la simplicidad del ejercicio de paginación:
            filterSearch(query)
        }
    }

    private fun filterSearch(query: String) {
        viewModelScope.launch {
            try {
                // Cargamos los 151 para poder buscar en todos
                val all = repository.getPokemonList(151, 0)
                val filtered = all.filter { pokemon ->
                    pokemon.name.contains(query, ignoreCase = true)
                            || pokemon.id.toString() == query
                            || pokemon.types.any { it.contains(query, ignoreCase = true) }
                }
                _uiState.value = PokedexUiState.Success(filtered)
            } catch (e: Exception) {
                _uiState.value = PokedexUiState.Error("Error al buscar")
            }
        }
    }

    fun retry() {
        loadPage(_currentPage.value)
    }
}
