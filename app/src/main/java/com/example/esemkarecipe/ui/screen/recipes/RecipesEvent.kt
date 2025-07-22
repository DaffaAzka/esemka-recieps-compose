package com.example.esemkarecipe.ui.screen.recipes

sealed class RecipesEvent {
    data class SearchChanged(val value: String) : RecipesEvent()
}