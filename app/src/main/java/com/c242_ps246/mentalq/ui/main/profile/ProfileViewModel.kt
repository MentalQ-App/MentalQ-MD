package com.c242_ps246.mentalq.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.local.repository.AuthRepository
import com.c242_ps246.mentalq.ui.auth.AuthScreenUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthScreenUIState())
    val uiState = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}