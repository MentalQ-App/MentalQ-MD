package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.local.room.AnalysisDao
import com.c242_ps246.mentalq.data.local.room.NoteDao
import com.c242_ps246.mentalq.data.local.room.UserDao
import com.c242_ps246.mentalq.data.manager.MentalQAppPreferences
import com.c242_ps246.mentalq.data.remote.response.AuthResponse
import com.c242_ps246.mentalq.data.remote.response.RegisterResponse
import com.c242_ps246.mentalq.data.remote.response.UserData
import com.c242_ps246.mentalq.data.remote.retrofit.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authApiService: AuthApiService,
    private val userDao: UserDao,
    private val noteDao: NoteDao,
    private val analysisDao: AnalysisDao,
    private val preferencesManager: MentalQAppPreferences
) {
    fun login(email: String, password: String): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.login(email, password)
            if (response.error == false) {
                val role = response.user?.role
                if (role.isNullOrEmpty()) {
                    emit(Result.Error("No user role found"))
                    return@liveData
                }

                withContext(Dispatchers.IO) {
                    response.token?.let { token ->
                        preferencesManager.saveToken(token)
                        val savedToken = preferencesManager.getToken().first()
                        if (savedToken.isEmpty()) {
                            throw Exception("Token failed to save")
                        }
                    }
                    preferencesManager.saveUserRole(role)
                    val savedRole = preferencesManager.getUserRole().first()
                    if (savedRole.isEmpty()) {
                        throw Exception("User role failed to save")
                    }
                }
                userDao.clearUserData()
                response.user.let { userDao.insertUser(it) }

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
            preferencesManager.saveUserRole("")
            preferencesManager.saveStreakInfo("", 0)
            userDao.clearUserData()
            noteDao.clearAllNotes()
            analysisDao.clearAllAnalysis()
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

    fun requestResetPassword(email: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.requestResetPassword(email)
            if (response.error == false) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun verifyOTP(email: String, otp: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.verifyOTP(email, otp)
            if (response.error == false) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun resetPassword(email: String, otp: String, password: String): LiveData<Result<Unit>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = authApiService.resetPassword(email, otp, password)
                if (response.error == false) {
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error(response.message.toString()))
                }
            } catch (e: Exception) {
                emit(Result.Error("An error occurred: ${e.message}"))
            }
        }

    fun getUserRole(): LiveData<String> {
        return preferencesManager.getUserRole().asLiveData()
    }
}
