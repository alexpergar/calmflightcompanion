package com.example.calmflightcompanion.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calmflightcompanion.R
import com.example.calmflightcompanion.database.DatabaseHelper
import com.example.calmflightcompanion.databinding.FragmentFlightDiaryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FlightDiaryFragment : Fragment() {

    private var _binding: FragmentFlightDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var diaryAdapter: FlightDiaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlightDiaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        // Load diary entries from the database.
        val diaryEntries = loadDiaryEntriesFromDatabase()

        // Set up RecyclerView.
        diaryAdapter = FlightDiaryAdapter(diaryEntries) { entry ->
            // Navigate to EditEntryFragment with the selected entry ID.
            val bundle = Bundle().apply {
                putInt("entryId", entry.id)
            }
            findNavController().navigate(R.id.editEntryFragment, bundle)
        }

        binding.diaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = diaryAdapter
        }

        // Add new entry button
        binding.buttonAddEntry.setOnClickListener {
            // Navigate to EditEntryFragment with no entry ID (for creating a new entry)
            findNavController().navigate(R.id.editEntryFragment)
        }

        return root
    }

    private fun loadDiaryEntriesFromDatabase(): List<DiaryEntry> {
        val entries = mutableListOf<DiaryEntry>()
        val cursor = dbHelper.getAllFlightDiaryEntries()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ENTRY_ID))
                val origin = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ORIGIN))
                val destination = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DESTINATION))
                val dateTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DATE))
                // Parse and format the date
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateTime)?.let { inputFormat.format(it) } ?: dateTime
                Log.d("Database", "Formatted Date: $date")

                val anxietyLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL))
                entries.add(DiaryEntry(id, date, origin, destination, anxietyLevel))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return entries
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
