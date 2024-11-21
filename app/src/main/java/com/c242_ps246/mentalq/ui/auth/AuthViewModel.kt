package com.c242_ps246.mentalq.ui.auth

import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.local.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.c242_ps246.mentalq.data.local.repository.Result

data class AuthScreenUIState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }

}
