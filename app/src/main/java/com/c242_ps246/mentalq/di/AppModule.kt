package com.c242_ps246.mentalq.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.c242_ps246.mentalq.data.local.room.MentalQDatabase
import com.c242_ps246.mentalq.data.local.room.NoteDao
import com.c242_ps246.mentalq.data.remote.retrofit.ApiService
import com.c242_ps246.mentalq.data.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteApiService(): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://jember.agrosewa.serv00.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideMentalQDatabase(application: Application): MentalQDatabase {
        return Room.databaseBuilder(
            application,
            MentalQDatabase::class.java,
            "mentalq_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(mentalQDatabase: MentalQDatabase): NoteDao {
        return mentalQDatabase.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(mentalQDatabase: MentalQDatabase, noteDao: NoteDao, apiService: ApiService): NoteRepository {
        return NoteRepository(mentalQDatabase, noteDao, apiService)
    }
}