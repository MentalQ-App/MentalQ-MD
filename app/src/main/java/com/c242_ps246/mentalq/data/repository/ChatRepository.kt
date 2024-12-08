package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.ChatDao
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem
import com.c242_ps246.mentalq.data.remote.retrofit.ChatApiService
import kotlinx.coroutines.delay

class ChatRepository(
    userDao: UserDao,
    chatDao: ChatDao,
    chatApiService: ChatApiService
) {
    fun getChatPreviews(): LiveData<Result<List<ChatMessageItem>>> = liveData {
        emit(Result.Loading)
        delay(1000)
        emit(Result.Success(listOf()))
    }
}