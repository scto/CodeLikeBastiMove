package com.scto.codelikebastimove.core.updater

enum class UpdateCheckInterval(val hours: Long, val displayName: String) {
    NEVER(-1, "Never"),
    HOURLY(1, "Hourly"),
    EVERY_6_HOURS(6, "Every 6 hours"),
    EVERY_12_HOURS(12, "Every 12 hours"),
    DAILY(24, "Daily"),
    WEEKLY(168, "Weekly");

    companion object {
        fun fromHours(hours: Long): UpdateCheckInterval {
            return entries.find { it.hours == hours } ?: DAILY
        }
    }
}
