package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.AuthResponse
import com.c242_ps246.mentalq.data.remote.response.RegisterResponse
import com.c242_ps246.mentalq.data.remote.response.RequestResetPasswordResponse
import com.c242_ps246.mentalq.data.remote.response.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface AuthApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("birthday") birthday: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("request-reset")
    suspend fun requestResetPassword(
        @Field("email") email: String
    ): RequestResetPasswordResponse

    @FormUrlEncoded
    @POST("verify-otp")
    suspend fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("reset-password")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("otp") otp: String,
        @Field("newPassword") newPassword: String
    ): AuthResponse
}
