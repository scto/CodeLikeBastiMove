package com.scto.codelikebastimove.core.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {
  val currentUser: FirebaseUser?
    get() = firebaseAuth.currentUser

  val isLoggedIn: Boolean
    get() = currentUser != null

  val authStateFlow: Flow<AuthState> =
    callbackFlow {
        val listener =
          FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
              trySend(AuthState.Authenticated(user))
            } else {
              trySend(AuthState.NotAuthenticated)
            }
          }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
      }
      .flowOn(Dispatchers.IO)

  suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
    return withContext(Dispatchers.IO) {
      try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        result.user?.let { Result.success(it) }
          ?: Result.failure(Exception("Sign in failed: User is null"))
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun createUserWithEmailAndPassword(
    email: String,
    password: String,
  ): Result<FirebaseUser> {
    return withContext(Dispatchers.IO) {
      try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        result.user?.let { Result.success(it) }
          ?: Result.failure(Exception("Registration failed: User is null"))
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser> {
    return withContext(Dispatchers.IO) {
      try {
        val result = firebaseAuth.signInWithCredential(credential).await()
        result.user?.let { Result.success(it) }
          ?: Result.failure(Exception("Sign in with credential failed: User is null"))
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
    return withContext(Dispatchers.IO) {
      try {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun sendEmailVerification(): Result<Unit> {
    return withContext(Dispatchers.IO) {
      try {
        currentUser?.sendEmailVerification()?.await()
        Result.success(Unit)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  fun signOut() {
    firebaseAuth.signOut()
  }

  suspend fun deleteAccount(): Result<Unit> {
    return withContext(Dispatchers.IO) {
      try {
        currentUser?.delete()?.await()
        Result.success(Unit)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  companion object {
    @Volatile private var INSTANCE: AuthRepository? = null

    fun getInstance(): AuthRepository {
      return INSTANCE ?: synchronized(this) { INSTANCE ?: AuthRepository().also { INSTANCE = it } }
    }
  }
}
