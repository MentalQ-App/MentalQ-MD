package com.c242_ps246.mentalq.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.local.repository.AuthRepository
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.c242_ps246.mentalq.data.local.repository.Result
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: MentalQAppPreferences
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
                    result.data.token?.let { token ->
                        saveToken(token)
                        _token.value = token
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
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
                    result.data.token?.let { token ->
                        saveToken(token)
                        _token.value = token
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferencesManager.saveToken(token)
        }
    }

    private fun getToken() {
        viewModelScope.launch {
            preferencesManager.token.first()?.let { savedToken ->
                if (savedToken.isNotEmpty()) {
                    _token.value = savedToken
                }
            }
        }
    }
}

data class AuthScreenUIState(
    val isLoading: Boolean = false,
    val note: ListNoteItem? = null,
    val error: String? = null
)