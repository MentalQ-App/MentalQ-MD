package com.c242_ps246.mentalq.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.remote.response.UserData

@Database(entities = [ListNoteItem::class, UserData::class], version = 2, exportSchema = false)
abstract class MentalQDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
}