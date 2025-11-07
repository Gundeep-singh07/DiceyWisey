// Location: app/src/main/java/com/diceywisey/database/User.kt
package com.example.diceywisey.database

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val trophies: Int = 0,
    val gamesPlayed: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val trophies: Int,
    val gamesPlayed: Int,
    val isCurrentUser: Boolean = false
)