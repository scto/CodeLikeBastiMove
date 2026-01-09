package com.scto.codelikebastimove.core.auth

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    data object Loading : AuthState()
    data object NotAuthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean
) {
    companion object {
        fun fromFirebaseUser(user: FirebaseUser): AuthUser {
            return AuthUser(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString(),
                isEmailVerified = user.isEmailVerified
            )
        }
    }
}
