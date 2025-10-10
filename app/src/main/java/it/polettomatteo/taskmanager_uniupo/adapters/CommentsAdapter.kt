package it.polettomatteo.taskmanager_uniupo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Comment

class CommentsAdapter(private var dataSet: ArrayList<Comment>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFrom: TextView
        val textComment: TextView
        val textVote: TextView


        init {
            textFrom = view.findViewById(R.id.textFrom)
            textComment = view.findViewById(R.id.textComment)
            textVote = view.findViewById(R.id.textVote)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)

        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textFrom.text = "Da: ${dataSet[position].from}"
        holder.textComment.text = "Commento: ${dataSet[position].text}"
        holder.textVote.text = "Voto: ${dataSet[position].vote}/10"
    }



    override fun getItemCount() = dataSet.size
}