package com.example.diceywisey.Activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diceywisey.R
import com.example.diceywisey.Adapter.LeaderboardAdapter
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvLeaderboard: RecyclerView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Find views
        btnBack = findViewById(R.id.btnBack)
        rvLeaderboard = findViewById(R.id.rvLeaderboard)

        // Setup RecyclerView
        rvLeaderboard.layoutManager = LinearLayoutManager(this)

        // Load leaderboard
        loadLeaderboard()

        // Back button
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun loadLeaderboard() {
        val userId = sessionManager.getUserId()
        val leaderboard = databaseHelper.getLeaderboard(userId)

        adapter = LeaderboardAdapter(leaderboard)
        rvLeaderboard.adapter = adapter
    }
}
