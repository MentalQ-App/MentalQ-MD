package com.c242_ps246.mentalq.ui.splash

import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role = _role.asStateFlow()

    init {
        getToken()
    }

    private fun getToken() {
        authRepository.getToken().observeForever {
            _token.value = it

            authRepository.getToken().removeObserver { this }
        }
    }

    fun getUserRole() {
        authRepository.getUserRole().observeForever {
            _role.value = it

            authRepository.getUserRole().removeObserver { this }
        }
    }
}