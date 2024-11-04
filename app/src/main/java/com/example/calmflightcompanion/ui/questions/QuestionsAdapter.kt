package com.example.calmflightcompanion.ui.questions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calmflightcompanion.R

class QuestionsAdapter(private var questionsList: List<Question>) :
    RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.question_text)
        val answerText: TextView = view.findViewById(R.id.answer_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionsList[position]
        holder.questionText.text = question.question
        holder.answerText.text = question.answer

        // Toggle answer visibility when question is clicked
        holder.itemView.setOnClickListener {
            if (holder.answerText.visibility == View.GONE) {
                holder.answerText.visibility = View.VISIBLE
            } else {
                holder.answerText.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = questionsList.size

    // Function to update the list and notify changes
    fun updateList(newList: List<Question>) {
        questionsList = newList
        notifyDataSetChanged()
    }
}
