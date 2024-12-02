package com.c242_ps246.mentalq.ui.main.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps246.mentalq.data.remote.response.ChatRoomItem
//import com.c242_ps246.mentalq.data.remote.response.ChatPreview
import com.c242_ps246.mentalq.data.repository.ChatRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState = _uiState.asStateFlow()

    private val _chatRooms = MutableStateFlow<List<ChatRoomItem>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()


    private val firebaseDatabase = Firebase.database

    init {
        loadChatRooms()
    }

    private fun loadChatRooms() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        firebaseDatabase.getReference("userChats").get().addOnSuccessListener {
            val chatRooms = mutableListOf<ChatRoomItem>()
            it.children.forEach { data ->
                val chatRoom = ChatRoomItem(
                    id = data.child("id").value.toString(),
                    userId = data.child("user_id").value.toString(),
                    lastMessage = data.child("last_message").value.toString(),
                    psychologistId = data.child("psychologist_id").value.toString(),
                    createdAt = data.child("created_at").value.toString()
                )
                chatRooms.add(chatRoom)
            }
            _chatRooms.value = chatRooms

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

//    private fun loadChatRooms() {
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
}

data class ChatListUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)