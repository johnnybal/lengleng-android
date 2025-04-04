package com.example.lengleng.models

data class Poll(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val options: List<PollOption> = emptyList(),
    val creatorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000, // 7 days
    val category: String = "general",
    val visibility: String = "public",
    val votes: List<Vote> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class PollOption(
    val id: String = "",
    val text: String = "",
    val votes: Int = 0
)

data class Vote(
    val userId: String = "",
    val optionId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Comment(
    val id: String = "",
    val userId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0
) 