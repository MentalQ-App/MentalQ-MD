package com.c242_ps246.mentalq.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.c242_ps246.mentalq.data.repository.Result
import kotlinx.coroutines.launch

data class AuthScreenUIState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

enum class ForgotPasswordStep {
    EMAIL,
    OTP,
    NEW_PASSWORD
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role = _role.asStateFlow()

    init {
        getToken()
    }

    fun login(email: String, password: String) {
        authRepository.login(email, password).observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, error = null, success = true)
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error,
                        success = false
                    )
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, birthday: String) {
        authRepository.register(name, email, password, birthday).observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, error = null, success = true)
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error,
                        success = false
                    )
                }
            }
        }
    }

    private fun getToken() {
        authRepository.getToken().observeForever {
            _token.value = it
        }
    }

    fun getUserRole() {
        authRepository.getUserRole().observeForever {
            _role.value = it
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }

    fun requestResetPassword(email: String) {
        viewModelScope.launch {
            authRepository.requestResetPassword(email).observeForever { result ->
                when (result) {
                    Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = null, success = true)
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error,
                            success = false
                        )
                    }
                }
            }
        }
    }

    fun verifyOTP(email: String, otp: String) {
        viewModelScope.launch {
            authRepository.verifyOTP(email, otp).observeForever { result ->
                when (result) {
                    Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = null, success = true)
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error,
                            success = false
                        )
                    }
                }
            }
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email, otp, newPassword).observeForever { result ->
                when (result) {
                    Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = null, success = true)
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error,
                            success = false
                        )
                    }
                }
            }
        }
    }
}
