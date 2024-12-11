package com.c242_ps246.mentalq.ui.main.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.remote.response.ChatRoomItem
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState = _uiState.asStateFlow()

    private val _chatRooms = MutableStateFlow<List<ChatRoomItem>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    private val firebase = Firebase.database

    init {
        loadChatRooms()
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

    private fun loadChatRooms() {
        authRepository.getUserId().observeForever { userId ->
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                firebase.getReference("userChats").child(userId).get()
                    .addOnSuccessListener { userChats ->

                        if (userChats.exists()) {

                            val chatRooms = mutableListOf<ChatRoomItem>()
                            var remainingRequests = userChats.childrenCount.toInt()

                            userChats.children.forEach { chatRoomSnapshot ->

                                Log.e("ChatViewModel", "loadChatRooms: ${chatRoomSnapshot.value}")

                                val chatRoomId = chatRoomSnapshot.value.toString()

                                firebase.getReference("chatroom").child(chatRoomId).get()
                                    .addOnSuccessListener { chatRoomData ->
                                        if (chatRoomData.exists()) {
                                            Log.e(
                                                "ChatViewModel",
                                                "loadChatRooms: ${chatRoomData.value}"
                                            )


                                            val chatRoom = ChatRoomItem(
                                                id = chatRoomData.key.toString(),
                                                userId = chatRoomData.child("members")
                                                    .child("user").child("id")
                                                    .value.toString(),
                                                userName = chatRoomData.child("members")
                                                    .child("user").child("name")
                                                    .value.toString(),
                                                userProfile = chatRoomData.child("members")
                                                    .child("user").child("profile")
                                                    .value.toString(),
                                                psychologistName = chatRoomData.child("members")
                                                    .child("psychologist").child("name")
                                                    .value.toString(),
                                                psychologistProfile = chatRoomData.child("members")
                                                    .child("psychologist")
                                                    .child("profile").value.toString(),
                                                lastMessage = chatRoomData.child("lastMessage").value.toString(),
                                                lastMessageSenderId = chatRoomData.child("lastMessageSenderId").value.toString(),
                                                psychologistId = chatRoomData.child("psychologistId").value.toString(),
                                                createdAt = chatRoomData.child("createdAt").value.toString(),
                                                updatedAt = chatRoomData.child("updatedAt").value.toString()
                                            )
                                            chatRooms.add(chatRoom)
                                        } else {
                                            _uiState.value = _uiState.value.copy(isLoading = false)
                                            Log.e(
                                                "ChatRoom",
                                                "loadChatRooms: Chat room doesn't exist"
                                            )
                                        }

                                        remainingRequests--

                                        if (remainingRequests == 0) {
                                            _chatRooms.value =
                                                chatRooms.sortedByDescending { it.updatedAt }
                                            _uiState.value = _uiState.value.copy(isLoading = false)
                                        }
                                    }
                            }


                        } else {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            Log.e("ChatRoom", "loadChatRooms: Chat room doesn't exist")
                        }


                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
                Log.e("ChatViewModel", "loadChatRooms: ${e.message}")
            }
        }
    }
}

data class ChatListUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)