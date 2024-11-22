package com.c242_ps246.mentalq.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.remote.retrofit.UserApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
        Log.e("UserRepository", "updateProfile: $name, $email, $birthday, $profileImage")
        try {
            val response = userApiService.updateProfile(profileImage, name, email, birthday)
            Log.e("UserRepository", "updateProfile: $response")
            if (response.error == false) {
                userDao.clearUserData()
                response.user?.let { userDao.insertUser(it) }
                emit(Result.Success(response.user))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "updateProfile: ${e.message}")
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }
}