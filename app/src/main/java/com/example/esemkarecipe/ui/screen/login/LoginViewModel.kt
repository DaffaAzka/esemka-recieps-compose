package com.example.esemkarecipe.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.esemkarecipe.repository.EsemkaReceiptRepository

class LoginViewModel(
    private val authRepository: EsemkaReceiptRepository = EsemkaReceiptRepository()
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.PasswordChanged -> {
                updatePasswordField(event.value)
            }

            is LoginEvent.UsernameChanged -> {
                updateUsernameField(event.value)
            }

            is LoginEvent.LoginButtonClicked -> {
                handleOnLogin(event.onSuccessLogin)
            }
        }
    }

    private fun updatePasswordField(value: String) {
        state = state.copy(
            formData = state.formData.copy(
                password = state.formData.password.copy(
                    value = value,
                    errorMessage = "",
                    isError = false
                )
            )
        )
    }

    private fun updateUsernameField(value: String) {
        state = state.copy(
            formData = state.formData.copy(
                username = state.formData.username.copy(
                    value = value,
                    errorMessage = "",
                    isError = false
                )
            )
        )
    }

    private fun handleOnLogin(onSuccessLogin: () -> Unit) {
        state = state.copy(isLoading = true, errorMessage = null)

        val usernameValue = state.formData.username.value.trim()
        val passwordValue = state.formData.password.value

        val validationResult = validateInput(usernameValue, passwordValue)
        if (!validationResult.isValid) {
            state = validationResult.newState
            return
        }

        authRepository.signIn(usernameValue, passwordValue) { result ->
            if (result.success) {
                state = state.copy(
                    isLoading = false,
                    errorMessage = null
                )
                onSuccessLogin()
            } else {
                state = state.copy(
                    isLoading = false,
                    errorMessage = result.message ?: "Login failed"
                )
            }
        }
    }

    private fun validateInput(username: String, password: String): ValidationResult {
        val isUsernameEmpty = username.isEmpty()
        val isPasswordEmpty = password.isEmpty()

        when {
            isUsernameEmpty -> {
                return ValidationResult(
                    isValid = false,
                    newState = state.copy(
                        formData = state.formData.copy(
                            username = state.formData.username.copy(
                                isError = true,
                                errorMessage = "Username cannot be empty"
                            )
                        ),
                        isLoading = false
                    )
                )
            }
            isPasswordEmpty -> {
                return ValidationResult(
                    isValid = false,
                    newState = state.copy(
                        formData = state.formData.copy(
                            password = state.formData.password.copy(
                                isError = true,
                                errorMessage = "Password cannot be empty"
                            )
                        ),
                        isLoading = false
                    )
                )
            }
            else -> {
                return ValidationResult(isValid = true, newState = state)
            }
        }
    }

    private data class ValidationResult(
        val isValid: Boolean,
        val newState: LoginState
    )
}