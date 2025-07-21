package com.example.esemkarecipe.ui.screen.recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.esemkarecipe.repository.EsemkaReceiptRepository

class RecipeViewModel(
    private val recipeId: Int,
    private val repository: EsemkaReceiptRepository = EsemkaReceiptRepository()
) : ViewModel() {

    var state by mutableStateOf(RecipeState())
        private set

    init {

    }

    private fun loadRecipe() {
        state = state.copy(isLoading = true)
        repository.getRecipe(recipeId) { recipe, error ->
            state = state.copy(
                isLoading = false,
                recipe = recipe,
                errorMessage = error
            )
        }
    }

}