package com.example.esemkarecipe.ui.screen.home

import com.example.esemkarecipe.model.Category

data class HomeState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null
)