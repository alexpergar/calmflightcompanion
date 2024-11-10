package com.example.calmflightcompanion.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calmflightcompanion.R

data class DiaryEntry(val id: Int, val date: String, val origin: String, val destination: String, val anxietyLevel: Int)

class FlightDiaryAdapter(
    private var entries: List<DiaryEntry>,
    private val onItemClick: (DiaryEntry) -> Unit
) : RecyclerView.Adapter<FlightDiaryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.text_date)
        val originText: TextView = view.findViewById(R.id.text_origin)
        val destinationText: TextView = view.findViewById(R.id.text_destination)
        val anxietyLevelText: TextView = view.findViewById(R.id.text_anxiety_level)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_diary_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.dateText.text = entry.date
        holder.originText.text = entry.origin
        holder.destinationText.text = entry.destination
        holder.anxietyLevelText.text = "Anxiety: ${entry.anxietyLevel}"

        holder.itemView.setOnClickListener { onItemClick(entry) }
    }

    override fun getItemCount(): Int = entries.size

    fun updateList(newList: List<DiaryEntry>) {
        entries = newList
        notifyDataSetChanged()
    }
}
