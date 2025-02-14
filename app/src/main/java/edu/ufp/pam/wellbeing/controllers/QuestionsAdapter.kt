package edu.ufp.pam.wellbeing.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import edu.ufp.pam.wellbeing.R
import androidx.recyclerview.widget.RecyclerView
import edu.ufp.pam.wellbeing.databases.entities.Question

class QuestionsAdapter(private val questions: List<Question>,
                       private val responseCallback: (questionId: Int, answerId: Int) -> Unit,
) :
RecyclerView.Adapter<QuestionsAdapter.QuestionsViewHolder>(){

    class QuestionsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val questionText: TextView = view.findViewById(R.id.textViewQuestion)
        val agreeIcn: ImageView = view.findViewById(R.id.iconAgree)
        val disagreeIcn: ImageView = view.findViewById(R.id.iconDisagree)
        val neutralIcn: ImageView = view.findViewById(R.id.iconNeutral)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionsViewHolder, position: Int) {

        val question = questions[position]

        holder.questionText.text = question.title

        // Set up click listeners for answer icons
        holder.disagreeIcn.setOnClickListener {
            resetIcons(holder)
            holder.disagreeIcn.setImageResource(R.drawable.ic_disagree_filled)

            responseCallback(question.id, 1)
        }
        holder.neutralIcn.setOnClickListener {
            resetIcons(holder)
            holder.neutralIcn.setImageResource(R.drawable.ic_neutral_filled)

            responseCallback(question.id, 2)
        }
        holder.agreeIcn.setOnClickListener {
            resetIcons(holder)
            holder.agreeIcn.setImageResource(R.drawable.ic_agree_filled)

            responseCallback(question.id, 3)
        }

    }

    private fun resetIcons(holder: QuestionsViewHolder) {

        holder.agreeIcn.setImageResource(R.drawable.ic_agree)
        holder.neutralIcn.setImageResource(R.drawable.ic_neutral)
        holder.disagreeIcn.setImageResource(R.drawable.ic_disagree)
    }



    override fun getItemCount(): Int  = questions.size
}