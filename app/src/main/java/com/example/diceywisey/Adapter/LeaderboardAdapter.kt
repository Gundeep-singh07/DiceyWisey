package com.example.diceywisey.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.diceywisey.R
import com.example.diceywisey.database.LeaderboardEntry

class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardLeaderboard: CardView = view.findViewById(R.id.cardLeaderboard)
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val tvGamesPlayed: TextView = view.findViewById(R.id.tvGamesPlayed)
        val tvTrophies: TextView = view.findViewById(R.id.tvTrophies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val entry = entries[position]

        holder.tvRank.text = entry.rank.toString()
        holder.tvUsername.text = entry.username
        holder.tvGamesPlayed.text = "${entry.gamesPlayed} games"
        holder.tvTrophies.text = entry.trophies.toString()

        // Highlight current user
        if (entry.isCurrentUser) {
            holder.cardLeaderboard.setCardBackgroundColor(
                holder.itemView.context.getColor(R.color.leaderboard_current_user)
            )
        } else {
            holder.cardLeaderboard.setCardBackgroundColor(
                holder.itemView.context.getColor(R.color.card_background)
            )
        }

        // Color ranks
        when (entry.rank) {
            1 -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.leaderboard_gold))
            2 -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.leaderboard_silver))
            3 -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.leaderboard_bronze))
            else -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.text_primary))
        }
    }

    override fun getItemCount() = entries.size
}
