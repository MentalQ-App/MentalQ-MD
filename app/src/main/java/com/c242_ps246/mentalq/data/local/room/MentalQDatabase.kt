package com.c242_ps246.mentalq.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem
import com.c242_ps246.mentalq.data.remote.response.ChatRoomItem
import com.c242_ps246.mentalq.data.remote.response.ListAnalysisItem
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.data.remote.response.UserData

@Database(
    entities = [ListNoteItem::class, UserData::class, ListAnalysisItem::class, ChatRoomItem::class, ChatMessageItem::class],
    version = 10,
    exportSchema = false
)
abstract class MentalQDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
    abstract fun analysisDao(): AnalysisDao
}
