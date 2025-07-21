package com.example.esemkarecipe.ui.screen.recipe

import com.example.esemkarecipe.model.Recipe

data class RecipeState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val errorMessage: String? = null
)