package com.c242_ps246.mentalq.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.c242_ps246.mentalq.data.local.remotemediator.RemoteKeys
import com.c242_ps246.mentalq.data.local.remotemediator.RemoteKeysDao
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.remote.response.UserData

@Database(entities = [ListNoteItem::class, RemoteKeys::class, UserData::class], version = 2, exportSchema = false)
abstract class MentalQDatabase: RoomDatabase() {
        abstract fun noteDao(): NoteDao
        abstract fun remoteKeysDao(): RemoteKeysDao

        companion object{
                @Volatile
                private var INSTANCE: MentalQDatabase? = null

                fun getInstance(context: Context): MentalQDatabase{
                        return INSTANCE ?: synchronized(this) {
                                INSTANCE ?: Room.databaseBuilder(
                                        context.applicationContext,
                                        MentalQDatabase::class.java,
                                        "mentalq_database"
                                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
                        }
                }
        }
}