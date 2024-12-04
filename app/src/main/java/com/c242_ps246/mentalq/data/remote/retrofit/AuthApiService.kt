package com.c242_ps246.mentalq.data.remote.retrofit

import com.c242_ps246.mentalq.data.remote.response.AuthResponse
import com.c242_ps246.mentalq.data.remote.response.RegisterResponse
import com.c242_ps246.mentalq.data.remote.response.RequestResetPasswordResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("google-login")
    suspend fun googleLogin(
        @Field("firebaseToken") firebaseToken: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("birthday") birthday: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("request-reset")
    suspend fun requestResetPassword(
        @Field("email") email: String
    ): Response<RequestResetPasswordResponse>

    @FormUrlEncoded
    @POST("verify-otp")
    suspend fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("reset-password")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("otp") otp: String,
        @Field("newPassword") newPassword: String
    ): Response<AuthResponse>
}
