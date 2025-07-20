package com.example.esemkarecipe.ui.screen.home


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.esemkarecipe.model.Category
import com.example.esemkarecipe.repository.EsemkaReceiptRepository

class HomeViewModel(
    private val repository: EsemkaReceiptRepository = EsemkaReceiptRepository()
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        state = state.copy(isLoading = true)
        repository.getCategories { categories, error ->
            state = state.copy(
                isLoading = false,
                categories = categories ?: emptyList(),
                errorMessage = error
            )
        }
    }
}