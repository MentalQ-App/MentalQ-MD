package com.c242_ps246.mentalq.ui.main.chat

import androidx.lifecycle.ViewModel
import com.c242_ps246.mentalq.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {}