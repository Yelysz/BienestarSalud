package com.example.bienestarsalud.di

import android.content.Context
import com.example.bienestarsalud.data.auth.FirebaseAuthRepositoryImpl
import com.example.bienestarsalud.data.wellness.WellnessRepositoryImpl
import com.example.bienestarsalud.domain.repository.auth.AuthRepository
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import com.example.bienestarsalud.ui.screens.reminders.AlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage // <--- 1. Agregamos esto aquí
    ): AuthRepository {
        // <--- 2. Pasamos 'storage' al constructor
        return FirebaseAuthRepositoryImpl(auth, storage)
    }

    @Provides
    @Singleton
    fun provideWellnessRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): WellnessRepository {
        return WellnessRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context
    ): AlarmScheduler {
        return AlarmScheduler(context)
    }
}