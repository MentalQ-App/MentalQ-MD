package com.c242_ps246.mentalq.data.repository

import com.c242_ps246.mentalq.data.local.room.ChatDao
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.remote.retrofit.ChatApiService

class ChatRepository(
    userDao: UserDao,
    chatDao: ChatDao
) {
}