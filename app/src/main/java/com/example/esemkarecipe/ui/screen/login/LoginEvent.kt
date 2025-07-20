package com.example.esemkarecipe.ui.screen.login

sealed class LoginEvent {
    data class UsernameChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    data class LoginButtonClicked(val onSuccessLogin: () -> Unit) : LoginEvent()
}