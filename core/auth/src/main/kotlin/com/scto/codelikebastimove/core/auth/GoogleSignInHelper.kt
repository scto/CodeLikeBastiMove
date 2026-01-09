package com.scto.codelikebastimove.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInHelper(
    private val context: Context,
    private val webClientId: String
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): Result<GoogleSignInResult> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context as android.app.Activity
            )

            handleSignInResult(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): Result<GoogleSignInResult> {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    Result.success(
                        GoogleSignInResult(
                            idToken = idToken,
                            firebaseCredential = firebaseCredential,
                            displayName = googleIdTokenCredential.displayName,
                            email = googleIdTokenCredential.id,
                            profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()
                        )
                    )
                } else {
                    Result.failure(Exception("Unexpected credential type"))
                }
            }
            else -> Result.failure(Exception("Unexpected credential type"))
        }
    }
}

data class GoogleSignInResult(
    val idToken: String,
    val firebaseCredential: com.google.firebase.auth.AuthCredential,
    val displayName: String?,
    val email: String?,
    val profilePictureUri: String?
)
