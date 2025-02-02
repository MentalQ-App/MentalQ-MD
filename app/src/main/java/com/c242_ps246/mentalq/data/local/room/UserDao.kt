package com.c242_ps246.mentalq.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.c242_ps246.mentalq.data.remote.response.UserData

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserData)

    @Query("SELECT * FROM user_data LIMIT 1")
    suspend fun getUserData(): UserData?

    @Query("DELETE FROM user_data")
    suspend fun clearUserData()
}
