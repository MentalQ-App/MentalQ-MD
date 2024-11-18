package com.c242_ps246.mentalq.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.remote.response.AuthResponse
import com.c242_ps246.mentalq.data.remote.retrofit.AuthApiService

class AuthRepository(
    private val authApiService: AuthApiService,
    private val userDao: UserDao
) {
    fun login(email: String, password: String): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.login(email, password)
            if (response.error == false) {
                response.user?.let { userDao.insertUser(it) }
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        birthday: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.register(name, email, password, birthday)
            if (response.error == false) {
                response.user?.let { userDao.insertUser(it) }
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }
}