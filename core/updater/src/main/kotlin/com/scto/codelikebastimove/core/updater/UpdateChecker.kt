package com.scto.codelikebastimove.core.updater

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class UpdateChecker(
    private val currentVersionCode: Int,
    private val currentVersionName: String,
    private val githubOwner: String = "AbandonedCart",
    private val githubRepo: String = "CodeLikeBastiMove"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun checkForUpdates(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/$githubOwner/$githubRepo/releases/latest")
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Failed to fetch release info: ${response.code}")
                )
            }

            val body = response.body?.string()
                ?: return@withContext Result.failure(Exception("Empty response body"))

            val release = json.decodeFromString<GitHubRelease>(body)

            val latestVersion = release.tagName.removePrefix("v")
            val isUpdateAvailable = isNewerVersion(latestVersion, currentVersionName)

            val apkAsset = release.assets.find { 
                it.name.endsWith(".apk") 
            }

            Result.success(
                UpdateInfo(
                    isUpdateAvailable = isUpdateAvailable,
                    currentVersion = currentVersionName,
                    latestVersion = latestVersion,
                    releaseNotes = release.body,
                    downloadUrl = apkAsset?.browserDownloadUrl ?: release.htmlUrl,
                    releaseUrl = release.htmlUrl,
                    publishedAt = release.publishedAt
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllReleases(): Result<List<GitHubRelease>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/$githubOwner/$githubRepo/releases")
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Failed to fetch releases: ${response.code}")
                )
            }

            val body = response.body?.string()
                ?: return@withContext Result.failure(Exception("Empty response body"))

            val releases = json.decodeFromString<List<GitHubRelease>>(body)
            Result.success(releases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val latestPart = latestParts.getOrElse(i) { 0 }
            val currentPart = currentParts.getOrElse(i) { 0 }

            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }

        return false
    }

    companion object {
        const val GITHUB_PROJECT_URL = "https://github.com/AbandonedCart/CodeLikeBastiMove"
        const val GITHUB_RELEASES_URL = "https://github.com/AbandonedCart/CodeLikeBastiMove/releases"
    }
}
