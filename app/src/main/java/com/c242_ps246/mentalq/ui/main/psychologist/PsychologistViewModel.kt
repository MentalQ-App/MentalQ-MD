package com.c242_ps246.mentalq.ui.main.psychologist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.remote.response.PsychologistItem
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.c242_ps246.mentalq.data.repository.PsychologistRepository
import com.c242_ps246.mentalq.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PsychologistScreenUiState(
    val isLoading: Boolean = true,
    val success: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PsychologistViewModel @Inject constructor(
    private val psychologistRepository: PsychologistRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PsychologistScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _psychologists = MutableStateFlow<List<PsychologistItem>?>(emptyList())
    val psychologists = _psychologists.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    init {
        loadPsychologists()
        getUserId()
    }

    private fun getUserId() {
        authRepository.getUserId().observeForever {
            _userId.value = it

            if (it != null) {
                _uiState.value =
                    _uiState.value.copy(isLoading = false)
            }

            authRepository.getUserId().removeObserver { this }
        }
    }

    private fun loadPsychologists() {
        viewModelScope.launch {
            psychologistRepository.getPsychologists().observeForever { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        Log.e("PsyViewModel", "loadPsychologists: $result")
                        _psychologists.value = result.data
                        _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                    }

                    is Result.Error -> {
                        Log.e("PsyViewModel", "loadPsychologists: $result")
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, error = result.error)
                    }
                }
            }
        }
    }


}