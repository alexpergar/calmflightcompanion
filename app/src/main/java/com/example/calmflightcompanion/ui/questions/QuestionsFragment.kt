package com.example.calmflightcompanion.ui.questions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var fullQuestionsList: List<Question> = listOf()  // Holds the full list of questions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        // Load questions from the database
        fullQuestionsList = loadQuestionsFromDatabase()

        // Set up RecyclerView
        questionsAdapter = QuestionsAdapter(fullQuestionsList)
        binding.questionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = questionsAdapter
        }

        // Set up search field listener
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterQuestions(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

    private fun filterQuestions(query: String) {
        // Filter the list based on the query
        val filteredList = if (query.isEmpty()) {
            fullQuestionsList
        } else {
            fullQuestionsList.filter {
                it.question.contains(query, ignoreCase = true)
            }
        }

        // Update the adapter with the filtered list
        questionsAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
