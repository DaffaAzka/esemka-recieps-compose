package com.example.esemkarecipe.model

data class Recipe(
    val category: Category,
    val categoryId: Int,
    val cookingTimeEstimate: Int,
    val description: String,
    val id: Int,
    val image: String,
    val ingredients: List<String>,
    val priceEstimate: Int,
    val steps: List<String>,
    val title: String
)