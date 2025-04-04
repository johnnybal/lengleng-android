package com.example.lengleng.models

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val bio: String = "",
    val connections: List<String> = emptyList(),
    val polls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false,
    val settings: UserSettings = UserSettings()
)

data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = false,
    val language: String = "en",
    val privacy: PrivacySettings = PrivacySettings()
)

data class PrivacySettings(
    val profileVisibility: String = "public",
    val showOnlineStatus: Boolean = true,
    val allowFriendRequests: Boolean = true
) 