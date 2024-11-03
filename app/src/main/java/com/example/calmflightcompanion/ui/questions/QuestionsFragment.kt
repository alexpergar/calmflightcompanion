package com.example.calmflightcompanion.ui.questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calmflightcompanion.database.DatabaseHelper
import com.example.calmflightcompanion.databinding.FragmentQuestionsBinding

class QuestionsFragment : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var questionsAdapter: QuestionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        // Load questions from the database
        val questionsList = loadQuestionsFromDatabase()

        // Set up RecyclerView
        questionsAdapter = QuestionsAdapter(questionsList)
        binding.questionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = questionsAdapter
        }

        return root
    }

    private fun loadQuestionsFromDatabase(): List<Question> {
        val questions = mutableListOf<Question>()
        val cursor = dbHelper.getAllFrequentQuestions()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FREQUENT_ID))
                val questionText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FREQUENT_QUESTION))
                val answerText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FREQUENT_ANSWER))
                questions.add(Question(id, questionText, answerText))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questions
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}