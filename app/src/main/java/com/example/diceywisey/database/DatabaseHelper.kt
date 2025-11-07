// Location: app/src/main/java/com/diceywisey/database/DatabaseHelper.kt
package com.example.diceywisey.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DiceyWisey.db"
        private const val DATABASE_VERSION = 1

        // Users table
        private const val TABLE_USERS = "users"
        private const val COL_ID = "id"
        private const val COL_USERNAME = "username"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_TROPHIES = "trophies"
        private const val COL_GAMES_PLAYED = "games_played"
        private const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_EMAIL TEXT NOT NULL,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_TROPHIES INTEGER DEFAULT 0,
                $COL_GAMES_PLAYED INTEGER DEFAULT 0,
                $COL_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // User Authentication
    fun registerUser(username: String, email: String, password: String): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_USERNAME, username)
                put(COL_EMAIL, email)
                put(COL_PASSWORD, password)
                put(COL_TROPHIES, 0)
                put(COL_GAMES_PLAYED, 0)
                put(COL_CREATED_AT, System.currentTimeMillis())
            }
            val result = db.insert(TABLE_USERS, null, values)
            result != -1L
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun loginUser(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                trophies = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TROPHIES)),
                gamesPlayed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GAMES_PLAYED)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID),
            "$COL_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // User Profile Operations
    fun getUserById(userId: Int): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                trophies = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TROPHIES)),
                gamesPlayed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GAMES_PLAYED)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun updateUserProfile(userId: Int, email: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_EMAIL, email)
        }
        val result = db.update(TABLE_USERS, values, "$COL_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    // Game Operations
    fun updateGameStats(userId: Int, trophiesEarned: Int) {
        val db = writableDatabase
        db.execSQL(
            "UPDATE $TABLE_USERS SET $COL_TROPHIES = $COL_TROPHIES + ?, $COL_GAMES_PLAYED = $COL_GAMES_PLAYED + 1 WHERE $COL_ID = ?",
            arrayOf(trophiesEarned, userId)
        )
        db.close()
    }

    // Leaderboard
    fun getLeaderboard(currentUserId: Int): List<LeaderboardEntry> {
        val db = readableDatabase
        val leaderboard = mutableListOf<LeaderboardEntry>()

        val cursor = db.rawQuery(
            "SELECT $COL_ID, $COL_USERNAME, $COL_TROPHIES, $COL_GAMES_PLAYED FROM $TABLE_USERS ORDER BY $COL_TROPHIES DESC",
            null
        )

        var rank = 1
        while (cursor.moveToNext()) {
            val userId = cursor.getInt(0)
            leaderboard.add(
                LeaderboardEntry(
                    rank = rank++,
                    username = cursor.getString(1),
                    trophies = cursor.getInt(2),
                    gamesPlayed = cursor.getInt(3),
                    isCurrentUser = userId == currentUserId
                )
            )
        }
        cursor.close()
        db.close()
        return leaderboard
    }
}