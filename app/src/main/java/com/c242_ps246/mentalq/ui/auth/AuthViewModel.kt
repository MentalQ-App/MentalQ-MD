package com.c242_ps246.mentalq.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.c242_ps246.mentalq.data.repository.Result
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

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
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
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

    @Suppress("DEPRECATION")
    fun loginWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val idToken = account.idToken
                if (idToken == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Google sign in failed: No ID token",
                        success = false
                    )
                    return@launch
                }

                val credential = GoogleAuthProvider.getCredential(idToken, null)

                val authResult = suspendCancellableCoroutine { continuation ->
                    firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener { result ->
                            continuation.resume(result, null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }

                if (authResult == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Google sign in failed: No auth result",
                        success = false
                    )
                    return@launch
                }

                val user = firebaseAuth.currentUser
                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Firebase auth failed: No user",
                        success = false
                    )
                    return@launch
                }

                val firebaseIdToken = suspendCancellableCoroutine { continuation ->
                    user.getIdToken(true)
                        .addOnSuccessListener { result ->
                            continuation.resume(result.token, null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }

                if (firebaseIdToken == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to get Firebase token",
                        success = false
                    )
                    return@launch
                }

                authRepository.googleLogin(firebaseIdToken).observeForever { result ->
                    when (result) {
                        Result.Loading -> {
                            // Already handling loading state
                        }

                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = null,
                                success = true
                            )
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
            } catch (e: Exception) {

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

            authRepository.getToken().removeObserver { this }
        }
    }

    fun getUserRole() {
        authRepository.getUserRole().observeForever {
            _role.value = it

            authRepository.getUserRole().removeObserver { this }
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
