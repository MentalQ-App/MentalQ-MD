package com.c242_ps246.mentalq.ui.main.chat

import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem
import com.c242_ps246.mentalq.data.repository.AuthRepository
import com.c242_ps246.mentalq.data.repository.ChatRepository
import com.c242_ps246.mentalq.ui.main.psychologist.PsychologistScreenUiState
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageItem>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(PsychologistScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val firebase = Firebase.database

    init {
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

    fun sendMessage(chatRoomId: String, messageText: String) {
        authRepository.getUserId().observeForever { userId ->

            val messageId = firebase.reference.push().key ?: UUID.randomUUID().toString()

            val chatMessageRef = firebase.getReference("messages").child(chatRoomId)

            val message = ChatMessageItem(
                id = messageId,
                senderId = userId,
                chatRoomId = chatRoomId,
                content = messageText,
                createdAt = System.currentTimeMillis().toString()
            )

            chatMessageRef.child(messageId).setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateLastMessage(chatRoomId, messageId, message)
                }
            }
        }
    }

    private fun updateLastMessage(chatRoomId: String, messageId: String, message: ChatMessageItem) {
        val chatRoomRef = firebase.getReference("chatroom").child(chatRoomId)
        chatRoomRef.child("lastMessage").setValue(message.content)
    }


    fun getMessages(chatRoomId: String) {
        firebase.getReference("messages").child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listMessages = mutableListOf<ChatMessageItem>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(ChatMessageItem::class.java)
                        message?.let {
                            listMessages.add(it)
                        }
                    }
                    _messages.value = listMessages
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}