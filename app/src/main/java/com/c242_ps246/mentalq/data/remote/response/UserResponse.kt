package com.c242_ps246.mentalq.data.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @field:SerializedName("user")
    val user: UserData? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)