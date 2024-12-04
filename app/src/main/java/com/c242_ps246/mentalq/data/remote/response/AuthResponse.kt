package com.c242_ps246.mentalq.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @field:SerializedName("user")
    val user: UserData? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)

data class RegisterResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class RequestResetPasswordResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey
    @field:SerializedName("user_id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("birthday")
    val birthday: String? = null,

    @field:SerializedName("profile_photo_url")
    val profilePhotoUrl: String? = null,

    @field:SerializedName("role")
    val role: String
)
