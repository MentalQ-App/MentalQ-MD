package com.c242_ps246.mentalq.ui.main.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.c242_ps246.mentalq.data.repository.Result
import com.c242_ps246.mentalq.data.repository.UserRepository
import com.c242_ps246.mentalq.ui.auth.AuthScreenUIState
import com.c242_ps246.mentalq.ui.notification.NotificationHelper
import com.c242_ps246.mentalq.ui.notification.StreakWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesManager: MentalQAppPreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    val notificationsEnabled = preferencesManager.getNotificationsState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                NotificationHelper.createNotificationChannel(context)
                StreakWorker.schedule(context)
            } else {
                StreakWorker.cancel(context)
            }
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun getUserData() {
        authRepository.getUser().observeForever { result ->
            when (result) {
                Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    _userData.value = result.data
                }

                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        birthday: RequestBody,
        profileImage: MultipartBody.Part?
    ) {
        Log.d("ProfileViewModel", "updateProfile: $name, $email, $birthday, $profileImage")
        viewModelScope.launch {
            userRepository.updateProfile(name, email, birthday, profileImage)
                .observeForever { result ->
                    when (result) {
                        Result.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                            _userData.value = result.data
                        }

                        is Result.Error -> {
                            _uiState.value =
                                _uiState.value.copy(isLoading = false, error = result.error)
                        }
                    }
                }
        }
    }
}
