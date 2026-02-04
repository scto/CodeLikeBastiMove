package com.scto.codelikebastimove.core.updater

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    @SerialName("tag_name")
    val tagName: String,
    val name: String,
    val body: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("published_at")
    val publishedAt: String,
    val prerelease: Boolean,
    val draft: Boolean,
    val assets: List<GitHubAsset> = emptyList()
)

@Serializable
data class GitHubAsset(
    val name: String,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String,
    val size: Long,
    @SerialName("content_type")
    val contentType: String
)

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val releaseUrl: String,
    val publishedAt: String
)

enum class UpdateCheckInterval(val hours: Long) {
    NEVER(0),
    HOURLY(1),
    EVERY_6_HOURS(6),
    EVERY_12_HOURS(12),
    DAILY(24),
    WEEKLY(168);

    companion object {
        fun fromHours(hours: Long): UpdateCheckInterval {
            return entries.find { it.hours == hours } ?: DAILY
        }
    }
}
