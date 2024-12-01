package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.ChatMessageResponse
import com.c242_ps246.mentalq.data.remote.response.ChatResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatApiService {
    @GET("chat_rooms")
    suspend fun getChatRooms(): ChatResponse

    @GET("chat_rooms/{id}")
    suspend fun getChatRoomMessages(id: String): ChatMessageResponse

    @FormUrlEncoded
    @POST("chat_rooms")
    suspend fun createChatRoom(
        @Field("user_id") userId: String,
        @Field("psychologist_id") psychologistId: String
    )

    @FormUrlEncoded
    @POST("chat_rooms/{id}")
    suspend fun createChatMessage(
        @Field("chatroom_id") chatRoomId: String,
        @Field("message") message: String
    )

}