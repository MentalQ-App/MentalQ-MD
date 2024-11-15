package com.c242_ps246.mentalq.di

import android.content.Context
import com.c242_ps246.mentalq.data.local.room.MentalQDatabase
import com.c242_ps246.mentalq.data.local.room.NoteDao
import com.c242_ps246.mentalq.data.remote.retrofit.ApiService
import com.c242_ps246.mentalq.data.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMentalQDatabase(context: Context): MentalQDatabase {
        return MentalQDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(mentalQDatabase: MentalQDatabase, noteDao: NoteDao, apiService: ApiService): NoteRepository {
        return NoteRepository(mentalQDatabase, noteDao, apiService)
    }
}