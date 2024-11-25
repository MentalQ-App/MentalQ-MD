package com.c242_ps246.mentalq.ui.main.chat
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.c242_ps246.mentalq.data.remote.response.ChatPreview
//import com.c242_ps246.mentalq.data.repository.ChatRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class ChatListViewModel @Inject constructor(
//    private val chatRepository: ChatRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(ChatListUiState())
//    val uiState = _uiState.asStateFlow()
//
//    private val _chatPreviews = MutableStateFlow<List<ChatPreview>>(emptyList())
//    val chatPreviews = _chatPreviews.asStateFlow()
//
//    init {
//        loadChatPreviews()
//    }
//
//    private fun loadChatPreviews() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//                _chatPreviews.value = chatRepository.getChatPreviews()
//                _uiState.update { it.copy(isLoading = false) }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        error = e.message,
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }
//}
//
//data class ChatListUiState(
//    val isLoading: Boolean = false,
//    val error: String? = null
//)