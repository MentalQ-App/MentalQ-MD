package com.c242_ps246.mentalq.data.repository

import android.util.Log
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
import org.json.JSONObject

class AuthRepository(
    private val authApiService: AuthApiService,
    private val userDao: UserDao,
    private val noteDao: NoteDao,
    private val analysisDao: AnalysisDao,
    private val preferencesManager: MentalQAppPreferences
) {
    fun login(
        email: String,
        password: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.login(email, password)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == false) {
                    val role = body.user?.role
                    val userId = body.user?.id

                    if (role.isNullOrEmpty()) {
                        emit(Result.Error("No user role found"))
                        return@liveData
                    }

                    if (userId.isNullOrEmpty()) {
                        emit(Result.Error("No user id found"))
                        return@liveData
                    }

                    withContext(Dispatchers.IO) {
                        body.token?.let { token ->
                            preferencesManager.saveToken(token)
                            val savedToken = preferencesManager.getToken().first()
                            if (savedToken.isEmpty()) throw Exception("Token failed to save")
                        }
                        preferencesManager.saveUserRole(role)
                        val savedRole = preferencesManager.getUserRole().first()
                        if (savedRole.isEmpty()) throw Exception("User role failed to save")

                        preferencesManager.saveUserId(userId)
                        val savedUserId = preferencesManager.getUserId().first()
                        if (savedUserId.isEmpty()) throw Exception("User id failed to save")
                    }
                    userDao.clearUserData()
                    userDao.insertUser(body.user)
                    emit(Result.Success(body))
                } else {
                    emit(Result.Error(body?.message ?: "Unknown error occurred"))
                }
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

    fun googleLogin(
        firebaseToken: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.googleLogin(firebaseToken)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == false) {
                    val role = body.user?.role
                    val userId = body.user?.id
                    if (role.isNullOrEmpty()) {
                        emit(Result.Error("No user role found"))
                        return@liveData
                    }

                    if (userId.isNullOrEmpty()) {
                        emit(Result.Error("No user id found"))
                        return@liveData
                    }

                    withContext(Dispatchers.IO) {
                        body.token?.let { token ->
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

                        preferencesManager.saveUserId(userId)
                        Log.e("AuthRepo", "googleLogin: $userId")
                        val savedUserId = preferencesManager.getUserId().first()
                        if (savedUserId.isEmpty()) throw Exception("User id failed to save")
                    }
                    userDao.clearUserData()
                    body.user.let { userDao.insertUser(it) }

                    emit(Result.Success(body))
                } else {
                    emit(Result.Error(body?.message ?: "Unknown error occurred"))
                }
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

    fun register(
        name: String,
        email: String,
        password: String,
        birthday: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.register(name, email, password, birthday)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.error == false) {
                    emit(Result.Success(body))
                } else {
                    emit(Result.Error(body?.message ?: "Unknown error occurred"))
                }
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

    fun getUserId(): LiveData<String> {
        return preferencesManager.getUserId().asLiveData()
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
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(response.body()?.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun verifyOTP(email: String, otp: String): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            val response = authApiService.verifyOTP(email, otp)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error(response.body()?.message.toString()))
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
                if (response.isSuccessful) {
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error(response.body()?.message.toString()))
                }
            } catch (e: Exception) {
                emit(Result.Error("An error occurred: ${e.message}"))
            }
        }

    fun getUserRole(): LiveData<String> {
        return preferencesManager.getUserRole().asLiveData()
    }
}
