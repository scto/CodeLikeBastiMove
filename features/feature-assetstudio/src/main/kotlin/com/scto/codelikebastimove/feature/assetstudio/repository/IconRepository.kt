package com.scto.codelikebastimove.feature.assetstudio.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.scto.codelikebastimove.feature.assetstudio.model.IconCategory
import com.scto.codelikebastimove.feature.assetstudio.model.IconProvider
import com.scto.codelikebastimove.feature.assetstudio.model.IconSearchResult
import com.scto.codelikebastimove.feature.assetstudio.model.IconStyle
import com.scto.codelikebastimove.feature.assetstudio.model.VectorAsset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface IconRepositoryProvider {
    val provider: IconProvider
    suspend fun searchIcons(query: String, category: String? = null, style: IconStyle? = null, page: Int = 0): IconSearchResult
    suspend fun getCategories(): List<IconCategory>
    suspend fun getIconsByCategory(category: String, page: Int = 0): IconSearchResult
    suspend fun downloadIcon(asset: VectorAsset): Result<String>
    suspend fun getFeaturedIcons(): List<VectorAsset>
}

class MaterialIconsRepository : IconRepositoryProvider {
    override val provider = IconProvider.MATERIAL_ICONS

    private val allIcons = buildMaterialIconsList()

    override suspend fun searchIcons(query: String, category: String?, style: IconStyle?, page: Int): IconSearchResult {
        val filtered = allIcons.filter { icon ->
            val matchesQuery = query.isEmpty() || icon.name.contains(query, ignoreCase = true) ||
                    icon.tags.any { it.contains(query, ignoreCase = true) }
            val matchesCategory = category == null || icon.category == category
            matchesQuery && matchesCategory
        }
        
        val pageSize = 50
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, filtered.size)
        val pageIcons = if (startIndex < filtered.size) filtered.subList(startIndex, endIndex) else emptyList()
        
        return IconSearchResult(
            icons = pageIcons,
            totalCount = filtered.size,
            hasMore = endIndex < filtered.size,
            nextPage = if (endIndex < filtered.size) page + 1 else null
        )
    }

    override suspend fun getCategories(): List<IconCategory> {
        return allIcons.groupBy { it.category }
            .map { (category, icons) -> IconCategory(category, category, icons.size) }
            .sortedBy { it.name }
    }

    override suspend fun getIconsByCategory(category: String, page: Int): IconSearchResult {
        return searchIcons("", category, null, page)
    }

    override suspend fun downloadIcon(asset: VectorAsset): Result<String> {
        return Result.success(generateAvdXml(asset))
    }

    override suspend fun getFeaturedIcons(): List<VectorAsset> {
        return allIcons.take(20)
    }

    private fun generateAvdXml(asset: VectorAsset): String {
        return """
            |<?xml version="1.0" encoding="utf-8"?>
            |<vector xmlns:android="http://schemas.android.com/apk/res/android"
            |    android:width="24dp"
            |    android:height="24dp"
            |    android:viewportWidth="24"
            |    android:viewportHeight="24"
            |    android:tint="#000000">
            |    <!-- ${asset.name} icon from Material Icons -->
            |    <path
            |        android:fillColor="@android:color/white"
            |        android:pathData="M12,2L2,7l10,5l10,-5L12,2z"/>
            |</vector>
        """.trimMargin()
    }

    private fun buildMaterialIconsList(): List<VectorAsset> {
        return listOf(
            createIcon("home", "Home", Icons.Filled.Home, "Navigation", listOf("house", "main")),
            createIcon("search", "Search", Icons.Filled.Search, "Action", listOf("find", "magnify")),
            createIcon("settings", "Settings", Icons.Filled.Settings, "Action", listOf("gear", "config")),
            createIcon("favorite", "Favorite", Icons.Filled.Favorite, "Action", listOf("heart", "like")),
            createIcon("add", "Add", Icons.Filled.Add, "Content", listOf("plus", "new")),
            createIcon("delete", "Delete", Icons.Filled.Delete, "Action", listOf("trash", "remove")),
            createIcon("edit", "Edit", Icons.Filled.Edit, "Image", listOf("pencil", "modify")),
            createIcon("share", "Share", Icons.Filled.Share, "Social", listOf("send")),
            createIcon("menu", "Menu", Icons.Filled.Menu, "Navigation", listOf("hamburger")),
            createIcon("close", "Close", Icons.Filled.Close, "Navigation", listOf("x", "cancel")),
            createIcon("check", "Check", Icons.Filled.Check, "Navigation", listOf("done", "tick")),
            createIcon("info", "Info", Icons.Filled.Info, "Action", listOf("information")),
            createIcon("warning", "Warning", Icons.Filled.Warning, "Alert", listOf("caution")),
            createIcon("error", "Error", Icons.Filled.Error, "Alert", listOf("problem")),
            createIcon("notifications", "Notifications", Icons.Filled.Notifications, "Social", listOf("bell", "alert")),
            createIcon("person", "Person", Icons.Filled.Person, "Social", listOf("user", "account")),
            createIcon("email", "Email", Icons.Filled.Email, "Communication", listOf("mail", "message")),
            createIcon("phone", "Phone", Icons.Filled.Phone, "Communication", listOf("call", "telephone")),
            createIcon("location", "Location", Icons.Filled.LocationOn, "Maps", listOf("place", "pin")),
            createIcon("calendar", "Calendar", Icons.Filled.DateRange, "Action", listOf("date", "schedule")),
            createIcon("camera", "Camera", Icons.Filled.CameraAlt, "Image", listOf("photo", "picture")),
            createIcon("image", "Image", Icons.Filled.Image, "Image", listOf("photo", "picture")),
            createIcon("play", "Play", Icons.Filled.PlayArrow, "AV", listOf("start", "media")),
            createIcon("pause", "Pause", Icons.Filled.Pause, "AV", listOf("stop", "media")),
            createIcon("stop", "Stop", Icons.Filled.Stop, "AV", listOf("end", "media")),
            createIcon("refresh", "Refresh", Icons.Filled.Refresh, "Navigation", listOf("reload", "sync")),
            createIcon("download", "Download", Icons.Filled.Download, "File", listOf("save")),
            createIcon("upload", "Upload", Icons.Filled.Upload, "File", listOf("send")),
            createIcon("folder", "Folder", Icons.Filled.Folder, "File", listOf("directory")),
            createIcon("file", "File", Icons.Filled.InsertDriveFile, "File", listOf("document")),
            createIcon("code", "Code", Icons.Filled.Code, "Action", listOf("programming", "developer")),
            createIcon("android", "Android", Icons.Filled.Android, "Device", listOf("robot", "mobile")),
            createIcon("star", "Star", Icons.Filled.Star, "Toggle", listOf("favorite", "rate")),
            createIcon("visibility", "Visibility", Icons.Filled.Visibility, "Action", listOf("eye", "show")),
            createIcon("lock", "Lock", Icons.Filled.Lock, "Action", listOf("secure", "password")),
            createIcon("language", "Language", Icons.Filled.Language, "Action", listOf("globe", "world")),
            createIcon("dark_mode", "Dark Mode", Icons.Filled.DarkMode, "Device", listOf("night", "moon")),
            createIcon("light_mode", "Light Mode", Icons.Filled.LightMode, "Device", listOf("day", "sun")),
            createIcon("palette", "Palette", Icons.Filled.Palette, "Image", listOf("color", "theme")),
            createIcon("brush", "Brush", Icons.Filled.Brush, "Image", listOf("paint", "draw")),
            createIcon("build", "Build", Icons.Filled.Build, "Action", listOf("tools", "wrench")),
            createIcon("bug_report", "Bug Report", Icons.Filled.BugReport, "Action", listOf("debug", "issue")),
            createIcon("cloud", "Cloud", Icons.Filled.Cloud, "File", listOf("storage", "sync")),
            createIcon("keyboard", "Keyboard", Icons.Filled.Keyboard, "Hardware", listOf("type", "input")),
            createIcon("terminal", "Terminal", Icons.Filled.Terminal, "Action", listOf("console", "command")),
            createIcon("apps", "Apps", Icons.Filled.Apps, "Navigation", listOf("grid", "launcher")),
            createIcon("dashboard", "Dashboard", Icons.Filled.Dashboard, "Action", listOf("panel", "overview")),
            createIcon("analytics", "Analytics", Icons.Filled.Analytics, "Action", listOf("stats", "chart")),
            createIcon("security", "Security", Icons.Filled.Security, "Action", listOf("shield", "protect")),
            createIcon("account_circle", "Account Circle", Icons.Filled.AccountCircle, "Social", listOf("avatar", "profile"))
        )
    }

    private fun createIcon(id: String, name: String, icon: ImageVector, category: String, tags: List<String>): VectorAsset {
        return VectorAsset(
            id = id,
            name = name,
            category = category,
            tags = tags,
            imageVector = icon,
            provider = IconProvider.MATERIAL_ICONS
        )
    }
}

class FeatherIconsRepository : IconRepositoryProvider {
    override val provider = IconProvider.FEATHER_ICONS

    private val featherIcons = buildFeatherIconsList()

    override suspend fun searchIcons(query: String, category: String?, style: IconStyle?, page: Int): IconSearchResult {
        val filtered = featherIcons.filter { icon ->
            query.isEmpty() || icon.name.contains(query, ignoreCase = true) ||
                    icon.tags.any { it.contains(query, ignoreCase = true) }
        }
        return IconSearchResult(icons = filtered, totalCount = filtered.size, hasMore = false)
    }

    override suspend fun getCategories(): List<IconCategory> {
        return featherIcons.groupBy { it.category }
            .map { (category, icons) -> IconCategory(category, category, icons.size) }
    }

    override suspend fun getIconsByCategory(category: String, page: Int): IconSearchResult {
        return searchIcons("", category, null, page)
    }

    override suspend fun downloadIcon(asset: VectorAsset): Result<String> {
        return Result.success("""
            |<vector xmlns:android="http://schemas.android.com/apk/res/android"
            |    android:width="24dp"
            |    android:height="24dp"
            |    android:viewportWidth="24"
            |    android:viewportHeight="24">
            |    <!-- ${asset.name} from Feather Icons -->
            |</vector>
        """.trimMargin())
    }

    override suspend fun getFeaturedIcons(): List<VectorAsset> = featherIcons.take(10)

    private fun buildFeatherIconsList(): List<VectorAsset> {
        return listOf(
            VectorAsset("feather_arrow_left", "Arrow Left", "Arrows", listOf("back", "previous"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_arrow_right", "Arrow Right", "Arrows", listOf("next", "forward"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_check", "Check", "General", listOf("done", "tick"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_x", "X", "General", listOf("close", "cancel"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_plus", "Plus", "General", listOf("add", "new"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_minus", "Minus", "General", listOf("remove", "subtract"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_user", "User", "Social", listOf("person", "account"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_settings", "Settings", "General", listOf("gear", "config"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_heart", "Heart", "Social", listOf("love", "like"), provider = IconProvider.FEATHER_ICONS),
            VectorAsset("feather_star", "Star", "General", listOf("favorite", "rate"), provider = IconProvider.FEATHER_ICONS)
        )
    }
}

class IconRepositoryManager {
    private val repositories = mapOf(
        IconProvider.MATERIAL_ICONS to MaterialIconsRepository(),
        IconProvider.FEATHER_ICONS to FeatherIconsRepository()
    )

    private val _currentProvider = MutableStateFlow(IconProvider.MATERIAL_ICONS)
    val currentProvider: Flow<IconProvider> = _currentProvider.asStateFlow()

    fun setProvider(provider: IconProvider) {
        _currentProvider.value = provider
    }

    fun getRepository(provider: IconProvider): IconRepositoryProvider? {
        return repositories[provider]
    }

    fun getCurrentRepository(): IconRepositoryProvider {
        return repositories[_currentProvider.value] ?: repositories.values.first()
    }

    fun getAvailableProviders(): List<IconProvider> {
        return repositories.keys.toList()
    }
}
