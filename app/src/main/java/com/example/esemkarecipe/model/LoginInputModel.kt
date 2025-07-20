package com.example.esemkarecipe.model

data class LoginInputModel(
    val username: InputModel = InputModel(),
    val password: InputModel = InputModel(),
)

data class InputModel(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = ""
)
