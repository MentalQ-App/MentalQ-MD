package com.c242_ps246.mentalq.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem
import com.c242_ps246.mentalq.data.remote.response.ChatRoomItem
import java.util.Date

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRooms(chats: List<ChatRoomItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chat: ChatRoomItem)

    //    TODO: Might need to change later
    @Query("UPDATE chat_room SET deletedAt = :timestamp WHERE id = :id")
    suspend fun deleteChatRoom(id: String, timestamp: Date = Date())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageItem)
}