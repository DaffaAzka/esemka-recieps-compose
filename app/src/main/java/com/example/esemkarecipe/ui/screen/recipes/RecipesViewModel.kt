package com.example.esemkarecipe.ui.screen.recipes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.esemkarecipe.repository.EsemkaReceiptRepository

class RecipesViewModel(
    private val categoryId: Int,
    private val repository: EsemkaReceiptRepository = EsemkaReceiptRepository()
) : ViewModel() {

    var state by mutableStateOf(RecipesState())
        private set

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        state = state.copy(isLoading = true)
        repository.getRecipes(categoryId) { recipes, error ->
            state = state.copy(
                isLoading = false,
                recipes = recipes ?: emptyList(),
                errorMessage = error
            )
        }
    }
}