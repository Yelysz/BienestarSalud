package com.example.bienestarsalud.domain.model.auth

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String? = "",
    val photoUrl: String? = null
)