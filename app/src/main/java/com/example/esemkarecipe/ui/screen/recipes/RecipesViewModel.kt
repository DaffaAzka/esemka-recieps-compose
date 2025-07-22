package com.example.esemkarecipe.ui.screen.recipes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.esemkarecipe.repository.EsemkaReceiptRepository

class RecipesViewModel(
    private val categoryId: Int,
    private val repository: EsemkaReceiptRepository = EsemkaReceiptRepository()
) : ViewModel() {

    var state by mutableStateOf(RecipesState())
        private set

    private var searchJob: Job? = null

    init {
        loadRecipes()
    }

    fun onEvent(event: RecipesEvent) {
        when (event) {
            is RecipesEvent.SearchChanged -> {
                state = state.copy(searchQuery = event.value)
                performSearch()
            }
        }
    }

    private fun performSearch() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)

            val query = state.searchQuery.trim()
            if (query.isEmpty()) {
                loadRecipes()
            } else if (query.length >= 3) {
                searchRecipes(query)
            } else {
                loadRecipes()
            }
        }
    }

    private fun loadRecipes() {
        state = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                repository.getRecipes(categoryId) { recipes, error ->
                    state = state.copy(
                        isLoading = false,
                        recipes = recipes ?: emptyList(),
                        errorMessage = error
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    private fun searchRecipes(query: String) {
        state = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                repository.getRecipes(categoryId, query) { recipes, error ->
                    state = state.copy(
                        isLoading = false,
                        recipes = recipes ?: emptyList(),
                        errorMessage = error
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}