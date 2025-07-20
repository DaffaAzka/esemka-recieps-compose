package com.example.esemkarecipe.ui.screen.recipes

import com.example.esemkarecipe.model.Recipe

data class RecipesState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val errorMessage: String? = null
)