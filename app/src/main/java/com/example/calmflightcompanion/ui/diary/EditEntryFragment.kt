package com.example.calmflightcompanion.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.calmflightcompanion.database.DatabaseHelper
import com.example.calmflightcompanion.databinding.FragmentEditEntryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EditEntryFragment : Fragment() {

    private var _binding: FragmentEditEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private var entryId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditEntryBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())

        // Retrieve entryId from arguments, or null if not provided.
        entryId = arguments?.getInt("entryId", -1)?.takeIf { it != -1 }

        // If an entryId is provided, load the existing entry data for editing.
        entryId?.let { loadEntry(it.toLong()) }

        binding.buttonSaveEntry.setOnClickListener {
            saveEntry()
        }

        binding.buttonDeleteEntry.setOnClickListener {
            deleteEntry()
        }

        return binding.root
    }

    private fun loadEntry(id: Long) {
        // Query the database for the entry data using the entryId.
        val cursor = dbHelper.getFlightDiaryEntryById(id)
        if (cursor != null && cursor.moveToFirst()) {
            val dateTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DATE))
            val origin = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ORIGIN))
            val destination = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DESTINATION))
            val anxietyLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DESCRIPTION))

            // Parse and format the date.
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateTime)?.let { inputFormat.format(it) } ?: dateTime

            // Populate fields with existing data.
            binding.editTextDate.setText(date)
            binding.editTextOrigin.setText(origin)
            binding.editTextDestination.setText(destination)
            binding.editTextAnxietyLevel.setText(anxietyLevel.toString())
            binding.editTextDescription.setText(description)
        } else {
            Toast.makeText(requireContext(), "No se ha encontrado la entrada", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
    }

    private fun saveEntry() {
        // Retrieve data from input fields.
        val dateInput = binding.editTextDate.text.toString()
        val origin = binding.editTextOrigin.text.toString()
        val destination = binding.editTextDestination.text.toString()
        val anxietyLevel = binding.editTextAnxietyLevel.text.toString().toIntOrNull() ?: 0
        val description = binding.editTextDescription.text.toString()

        if (origin.isEmpty() || destination.isEmpty() || anxietyLevel !in 1..10) {
            Toast.makeText(requireContext(), "Por favor, rellena todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse and format the date.
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateInput)?.let { outputFormat.format(it) } ?: dateInput

        // Check if we're updating an existing entry or creating a new one.
        if (entryId != null) {
            // Update existing entry
            dbHelper.updateFlightDiaryEntry(entryId!!.toLong(), origin, destination, anxietyLevel, description, date)
            Toast.makeText(requireContext(), "Entrada actualizada", Toast.LENGTH_SHORT).show()
        } else {
            // Insert new entry.
            dbHelper.insertFlightDiaryEntry(origin, destination, anxietyLevel, description, date)
            Toast.makeText(requireContext(), "Nueva entrada guardada", Toast.LENGTH_SHORT).show()
        }

        // Navigate back to the diary list or close the fragment.
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteEntry() {
        entryId?.let {
            // Confirm before deleting.
            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Borrar entrada")
                .setMessage("¿Estás seguro de borrar esta entrada?")
                .setPositiveButton("Yes") { _, _ ->
                    // Perform the deletion.
                    dbHelper.deleteFlightDiaryEntry(it.toLong())
                    Toast.makeText(requireContext(), "Entrada borrada", Toast.LENGTH_SHORT).show()
                    // Navigate back.
                    requireActivity().onBackPressed()
                }
                .setNegativeButton("No", null)
                .create()
            dialog.show()
        } ?: run {
            Toast.makeText(requireContext(), "No hay entradas para borrar", Toast.LENGTH_SHORT).show()
        }
    }
}
