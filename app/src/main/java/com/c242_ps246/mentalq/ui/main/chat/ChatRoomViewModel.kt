package com.c242_ps246.mentalq.ui.main.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem
import com.c242_ps246.mentalq.data.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageItem>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(PsychologistScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _profileUrl = MutableStateFlow<String?>(null)
    val profileUrl = _profileUrl.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    private val _isEnded = MutableStateFlow(false)
    val isEnded = _isEnded.asStateFlow()

    private val firebase = Firebase.database

    init {
        _uiState.value = uiState.value.copy(isLoading = true)
        getUserId()
        getUserRole()
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

    private fun getUserRole() {
        authRepository.getUserRole().observeForever {
            _userRole.value = it

            if (it != null) {
                _uiState.value =
                    _uiState.value.copy(isLoading = false)
            }

            authRepository.getUserRole().removeObserver { this }
        }
    }

    fun endSession(chatRoomId: String) {
        val chatRoomRef = firebase.getReference("chatroom").child(chatRoomId)
        chatRoomRef.child("isEnded").setValue(true).addOnSuccessListener {
            _isEnded.value = true
        }
    }

    fun getSessionStatus(chatRoomId: String) {
        val chatRoomRef = firebase.getReference("chatroom").child(chatRoomId)
        chatRoomRef.child("isEnded").get().addOnSuccessListener {
            _isEnded.value = it.value as Boolean
        }
    }

    fun getProfileUrl(chatRoomId: String, userId: String) {

        val membersRef = firebase.getReference("chatroom").child(chatRoomId).child("members")

        membersRef.child("user").child("id").get().addOnSuccessListener { id ->
            if (id.value.toString() == userId) {
                Log.e("TestProfile", "getProfileUrl: Psycholog!")
                membersRef.child("psychologist").child("profile").get()
                    .addOnSuccessListener { profileUrl ->
                        _profileUrl.value = profileUrl.value.toString()
                    }
                membersRef.child("psychologist").child("name").get()
                    .addOnSuccessListener { name ->
                        _userName.value = name.value.toString()
                    }
                _uiState.value = uiState.value.copy(isLoading = false)
            } else {
                Log.e("TestProfile", "getProfileUrl: User!!")
                membersRef.child("user").child("profile").get()
                    .addOnSuccessListener { profileUrl ->
                        _profileUrl.value = profileUrl.value.toString()
                    }
                membersRef.child("user").child("name").get()
                    .addOnSuccessListener { name ->
                        _userName.value = name.value.toString()
                    }
                _uiState.value = uiState.value.copy(isLoading = false)
            }
        }

    }

    fun sendMessage(userId: String, chatRoomId: String, messageText: String) {
        val messageId = firebase.reference.push().key ?: UUID.randomUUID().toString()

        val chatMessageRef = firebase.getReference("messages").child(chatRoomId)

        val chatRoomRef = firebase.getReference("chatroom").child(chatRoomId)

        val message = ChatMessageItem(
            id = messageId,
            senderId = userId,
            chatRoomId = chatRoomId,
            content = messageText,
            createdAt = System.currentTimeMillis().toString()
        )

        chatMessageRef.child(messageId).setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateLastMessage(chatRoomId, messageId, message, userId)

                chatRoomRef.child("updatedAt")
                    .setValue(System.currentTimeMillis().toString())
            }
        }

    }

    private fun updateLastMessage(
        chatRoomId: String,
        messageId: String,
        message: ChatMessageItem,
        senderId: String
    ) {

        val chatRoomRef = firebase.getReference("chatroom").child(chatRoomId)
        chatRoomRef.child("lastMessage").setValue(message.content)
        chatRoomRef.child("lastMessageSenderId").setValue(senderId)
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