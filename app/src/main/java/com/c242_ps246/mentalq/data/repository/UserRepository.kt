package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.remote.retrofit.UserApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class UserRepository(
    private val userApiService: UserApiService,
    private val userDao: UserDao
) {
    fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        birthday: RequestBody,
        profileImage: MultipartBody.Part?
    ): LiveData<Result<UserData?>> = liveData {
        emit(Result.Loading)
        try {
            val response = userApiService.updateProfile(profileImage, name, email, birthday)
            if (response.isSuccessful) {
                val body = response.body()
                userDao.clearUserData()
                body?.user?.let { userDao.insertUser(it) }
                emit(Result.Success(body?.user))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    JSONObject(errorBody).getString("message")
                } else {
                    "Unknown error occurred"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun getUserDataById(userId: String): LiveData<Result<UserData?>> = liveData {
        emit(Result.Loading)
        try {
            val response = userApiService.getUserById(userId)
            if (response.isSuccessful) {
                val body = response.body()
                emit(Result.Success(body?.user))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    JSONObject(errorBody).getString("message")
                } else {
                    "Unknown error occurred"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }
}