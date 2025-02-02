package com.c242_ps246.mentalq.ui.main.chat

import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.remote.response.ChatRoomItem
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

                val userChatsRef = firebase.getReference("userChats").child(userId)

                userChatsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            val chatRooms = mutableListOf<ChatRoomItem>()
                            val chatRoomsMap = mutableMapOf<String, ChatRoomItem>()
                            val userChats = snapshot.children
                            var remainingRequests = userChats.count()

                            snapshot.children.forEach { chatRoomSnapshot ->

                                val chatRoomId = chatRoomSnapshot.value.toString()

                                val chatRoomRef =
                                    firebase.getReference("chatroom").child(chatRoomId)

                                chatRoomRef.get().addOnSuccessListener { chatRoomData ->
                                    if (chatRoomData.exists()) {

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
                                            psychologistPrefix = chatRoomData.child("members")
                                                .child("psychologist").child("prefix")
                                                .value.toString(),
                                            psychologistSuffix = chatRoomData.child("members")
                                                .child("psychologist")
                                                .child("suffix").value.toString(),
                                            psychologistProfile = chatRoomData.child("members")
                                                .child("psychologist")
                                                .child("profile").value.toString(),
                                            lastMessage = chatRoomData.child("lastMessage").value.toString(),
                                            lastMessageSenderId = chatRoomData.child("lastMessageSenderId").value.toString(),
                                            psychologistId = chatRoomData.child("psychologistId").value.toString(),
                                            createdAt = chatRoomData.child("createdAt").value.toString(),
                                            updatedAt = chatRoomData.child("updatedAt").value.toString()
                                        )

                                        chatRoomsMap[chatRoom.id] = chatRoom

                                        chatRooms.clear()
                                        chatRooms.addAll(chatRoomsMap.values)

                                        chatRoomRef.child("lastMessage")
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(lastMessageSnapshot: DataSnapshot) {
                                                    if (lastMessageSnapshot.exists()) {
                                                        val updatedLastMessage =
                                                            lastMessageSnapshot.value.toString()

                                                        val updatedChatRoom =
                                                            chatRoomsMap[chatRoom.id]?.copy(
                                                                lastMessage = updatedLastMessage
                                                            )

                                                        if (updatedChatRoom != null) {
                                                            chatRoomsMap[chatRoom.id] =
                                                                updatedChatRoom
                                                            chatRooms.clear()
                                                            chatRooms.addAll(chatRoomsMap.values)
                                                            _chatRooms.value =
                                                                chatRooms.sortedByDescending { it.updatedAt }
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                }

                                            })

                                        chatRoomRef.child("updatedAt")
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(updatedAtSnapshot: DataSnapshot) {
                                                    if (updatedAtSnapshot.exists()) {
                                                        val updatedUpdatedAt =
                                                            updatedAtSnapshot.value.toString()

                                                        val updatedChatRoom =
                                                            chatRoomsMap[chatRoom.id]?.copy(
                                                                updatedAt = updatedUpdatedAt
                                                            )

                                                        if (updatedChatRoom != null) {
                                                            chatRoomsMap[chatRoom.id] =
                                                                updatedChatRoom
                                                            chatRooms.clear()
                                                            chatRooms.addAll(chatRoomsMap.values)
                                                            _chatRooms.value =
                                                                chatRooms.sortedByDescending { it.updatedAt }
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                }

                                            })

                                        chatRoomRef.child("lastMessageSenderId")
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(
                                                    lastMessageSenderIdSnapshot: DataSnapshot
                                                ) {
                                                    if (lastMessageSenderIdSnapshot.exists()) {
                                                        val updatedLastMessageSenderId =
                                                            lastMessageSenderIdSnapshot.value.toString()

                                                        val updatedChatRoom =
                                                            chatRoomsMap[chatRoom.id]?.copy(
                                                                lastMessageSenderId = updatedLastMessageSenderId
                                                            )

                                                        if (updatedChatRoom != null) {
                                                            chatRoomsMap[chatRoom.id] =
                                                                updatedChatRoom
                                                            chatRooms.clear()
                                                            chatRooms.addAll(chatRoomsMap.values)
                                                            _chatRooms.value =
                                                                chatRooms.sortedByDescending { it.updatedAt }
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                }

                                            })


                                    } else {
                                        _uiState.value = _uiState.value.copy(isLoading = false)

                                    }

                                    remainingRequests--
                                    if (remainingRequests == 0) {
                                        _chatRooms.value =
                                            chatRooms.sortedByDescending { it.updatedAt }
                                        _uiState.value = _uiState.value.copy(isLoading = false)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
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