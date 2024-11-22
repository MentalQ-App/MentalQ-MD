package com.c242_ps246.mentalq.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.data.remote.response.AuthResponse
import com.c242_ps246.mentalq.data.remote.response.RegisterResponse
import com.c242_ps246.mentalq.data.remote.response.UpdateProfileResponse
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.remote.retrofit.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository(
    private val authApiService: AuthApiService,
    private val userDao: UserDao,
    private val preferencesManager: MentalQAppPreferences
) {
    fun login(email: String, password: String): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.login(email, password)
            if (response.error == false) {
                userDao.clearUserData()
                response.user?.let { userDao.insertUser(it) }
                response.token?.let { token ->
                    withContext(Dispatchers.IO) {
                        preferencesManager.saveToken(token)
                        preferencesManager.getToken().first().also { savedToken ->
                            if (savedToken.isEmpty()) {
                                throw Exception("Token failed to save")
                            }
                        }
                    }
                }
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
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.register(name, email, password, birthday)
            if (response.error == false) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    suspend fun logout() = runCatching {
        withContext(Dispatchers.IO) {
            preferencesManager.saveToken("")
            userDao.clearUserData()
        }
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error("Logout failed: ${it.message}") }
    )

    fun getToken(): LiveData<String> {
        return preferencesManager.getToken().asLiveData()
    }

    fun getUser(): LiveData<Result<UserData>> = liveData {
        emit(Result.Loading)
        try {
            val user = userDao.getUserData()
            if (user != null) {
                emit(Result.Success(user))
            } else {
                emit(Result.Error("User not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
