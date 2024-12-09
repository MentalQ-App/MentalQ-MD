package com.c242_ps246.mentalq.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ChatResponse(
    @field:SerializedName("listChatRooms")
    val listChatRooms: List<ChatRoomItem>? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)


@Entity(tableName = "chat_room")
data class ChatRoomItem(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("user_id")
    val userId: String,

    @field:SerializedName("psychologist_id")
    val psychologistId: String,

    @field:SerializedName("psychologist_name")
    val psychologistName: String,

    @field:SerializedName("psychologist_profile")
    val psychologistProfile: String? = null,

    @field:SerializedName("last_message")
    val lastMessage: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String = "",

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("deleted_at")
    val deletedAt: String? = null
)

data class Psychologist(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("profile")
    val profile: String?
)


data class ChatMessageResponse(
    @field:SerializedName("listMessages")
    val listMessages: List<ChatMessageItem>? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Entity(tableName = "chat_message")
data class ChatMessageItem(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String = "",

    @field:SerializedName("chat_room_id")
    val chatRoomId: String = "",

    @field:SerializedName("sender_id")
    val senderId: String = "",

    @field:SerializedName("message")
    val content: String = "",

    @field:SerializedName("createdAt")
    val createdAt: String? = "",
)