package com.scto.codelikebastimove.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.auth.AuthRepository
import com.scto.codelikebastimove.core.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authStateFlow.collect { state ->
                _authState.value = state
            }
        }
    }

    fun updateLoginEmail(email: String) {
        _loginState.value = _loginState.value.copy(email = email, errorMessage = null)
    }

    fun updateLoginPassword(password: String) {
        _loginState.value = _loginState.value.copy(password = password, errorMessage = null)
    }

    fun updateRegisterEmail(email: String) {
        _registerState.value = _registerState.value.copy(email = email, errorMessage = null)
    }

    fun updateRegisterPassword(password: String) {
        _registerState.value = _registerState.value.copy(password = password, errorMessage = null)
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerState.value = _registerState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun login() {
        val state = _loginState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _loginState.value = state.copy(errorMessage = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _loginState.value = state.copy(isLoading = true, errorMessage = null)
            
            authRepository.signInWithEmailAndPassword(state.email, state.password)
                .onSuccess {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                }
                .onFailure { exception ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
        }
    }

    fun register() {
        val state = _registerState.value
        if (state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            _registerState.value = state.copy(errorMessage = "Please fill in all fields")
            return
        }

        if (state.password != state.confirmPassword) {
            _registerState.value = state.copy(errorMessage = "Passwords do not match")
            return
        }

        if (state.password.length < 6) {
            _registerState.value = state.copy(errorMessage = "Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _registerState.value = state.copy(isLoading = true, errorMessage = null)
            
            authRepository.createUserWithEmailAndPassword(state.email, state.password)
                .onSuccess {
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true
                    )
                }
                .onFailure { exception ->
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Registration failed"
                    )
                }
        }
    }

    fun signInWithGoogleCredential(credential: com.google.firebase.auth.AuthCredential) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.signInWithCredential(credential)
                .onSuccess {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                }
                .onFailure { exception ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Google sign in failed"
                    )
                }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _loginState.value = _loginState.value.copy(errorMessage = "Please enter your email address")
            return
        }

        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true)
            
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = "Password reset email sent"
                    )
                }
                .onFailure { exception ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to send reset email"
                    )
                }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _loginState.value = LoginUiState()
        _registerState.value = RegisterUiState()
    }

    fun clearLoginError() {
        _loginState.value = _loginState.value.copy(errorMessage = null)
    }

    fun clearRegisterError() {
        _registerState.value = _registerState.value.copy(errorMessage = null)
    }

    fun resetLoginSuccess() {
        _loginState.value = _loginState.value.copy(isLoginSuccessful = false)
    }

    fun resetRegistrationSuccess() {
        _registerState.value = _registerState.value.copy(isRegistrationSuccessful = false)
    }
}
