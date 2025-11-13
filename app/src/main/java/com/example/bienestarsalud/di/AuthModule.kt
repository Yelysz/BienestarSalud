package com.example.bienestarsalud.di

import com.example.bienestarsalud.data.auth.FirebaseAuthRepositoryImpl
import com.example.bienestarsalud.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        // Hilt ahora sabe que debe usar la implementación
        // cuando se pida la interfaz
        return FirebaseAuthRepositoryImpl(auth)
    }
}