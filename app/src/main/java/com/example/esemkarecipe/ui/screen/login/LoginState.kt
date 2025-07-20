package com.example.esemkarecipe.ui.screen.login

import com.example.esemkarecipe.model.LoginInputModel

data class LoginState(
    val isLoading: Boolean = false,
    val formData: LoginInputModel = LoginInputModel(),
    val errorMessage: String? = null,
)