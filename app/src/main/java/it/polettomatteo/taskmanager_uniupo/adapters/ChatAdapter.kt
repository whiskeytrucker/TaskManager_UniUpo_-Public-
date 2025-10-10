package it.polettomatteo.taskmanager_uniupo.adapters

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Message
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(private val dataSet: ArrayList<Message>, private val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val messageTimestamp: TextView = view.findViewById(R.id.messageTimestamp)
        val chatLayout: LinearLayout = view.findViewById(R.id.itemChatLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = dataSet[position]

        if(message.sender){
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1.0f
                gravity = Gravity.END
            }

            holder.chatLayout.background = ContextCompat.getDrawable(context, R.drawable.bubble_sender_bg)
            holder.chatLayout.layoutParams = params

        }
        holder.messageText.text = message.text
        holder.messageTimestamp.text = formatTimestamp(message.timestamp)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd/MM/YYYY hh:mm a", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}
