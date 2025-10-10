package it.polettomatteo.taskmanager_uniupo.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.firebase.NotificationDB
import it.polettomatteo.taskmanager_uniupo.firebase.SubtasksDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.firebase.UsersDB
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.reflect.typeOf


@SuppressLint("NotifyDataSetChanged")
class TasksAdapter(private val userType: String, private val context: Context, private var dataSet: ArrayList<Task>, private val newFragment: StartNewRecycler, private val modifyListener: TempActivity, private val deleteListener: TempActivity) : RecyclerView.Adapter<TasksAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.name)
        val descr: TextView = view.findViewById(R.id.descr)
        val dev: TextView = view.findViewById(R.id.dev)
        val expiring: TextView = view.findViewById(R.id.expiring)
        val progressTask: TextView = view.findViewById(R.id.progressTask)
        val seekBar: SeekBar  = view.findViewById(R.id.seekBar)

        val modifyBtn: Button = view.findViewById(R.id.modifyTask)
        val deleteBtn: Button = view.findViewById(R.id.deleteTask)
        val promptBtn: Button = view.findViewById(R.id.promptDev)

        init {
            seekBar.isEnabled = false
            seekBar.progress = 0
        }
    }


    private var filteredList: MutableList<Task> = dataSet.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)

        return ViewHolder(view)
    }




    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position in 0 until filteredList.size){
            val sec = filteredList[position].expire.seconds
            val ns = filteredList[position].expire.nanoseconds

            val ms = sec * 1000 + ns /  1000000

            // prendi l'elemento dal dataset e rimpazza i contenuti
            holder.nome.text = filteredList[position].nome
            holder.descr.text = filteredList[position].descr
            holder.dev.text = "Developer: ${filteredList[position].dev}"
            holder.expiring.text = "Scadenza: ${formatTimestamp(Date(ms))}"
            holder.progressTask.text = "${filteredList[position].progress}%"
            holder.seekBar.progress = filteredList[position].progress

            val idTask = filteredList[position].id
            val idProject = filteredList[position].idPrg

            holder.itemView.setOnClickListener {
                SubtasksDB.getSubtasks(idProject, idTask){bundle ->
                    if(bundle != null){
                        filteredList.clear()
                        dataSet.clear()
                        newFragment.onStartNewRecyclerView(bundle)
                    }
                }
            }


            if(userType.compareTo("pl") == 0){
                holder.modifyBtn.visibility = View.VISIBLE
                holder.deleteBtn.visibility = View.VISIBLE
                holder.promptBtn.visibility = View.VISIBLE

                holder.modifyBtn.setOnClickListener{
                    val bundle = Bundle()
                    bundle.putString("idTask", idTask)
                    bundle.putString("titolo", filteredList[position].nome)
                    bundle.putString("descr", filteredList[position].descr)
                    bundle.putString("dev", filteredList[position].dev)
                    bundle.putInt("progress", filteredList[position].progress.toInt())
                    bundle.putLong("scadenza", ms)

                    modifyListener.onStartNewTempActivity(bundle)
                }

                holder.deleteBtn.setOnClickListener{
                    TasksDB.deleteTask(idProject, idTask){result ->
                        if(result == true){
                            val bun = Bundle()
                            bun.putInt("pos", position)
                            bun.putString("id", idTask)
                            deleteListener.onStartNewTempActivity(bun)
                        }else{
                            Toast.makeText(context, "Errore nella cancellazione!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                holder.promptBtn.setOnClickListener {
                    NotificationDB.promptUser(filteredList[position].dev, filteredList[position].nome){ result ->
                        if(result == true)Toast.makeText(context, "Notifica inviata!", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(context, "Errore nel mandare la notifica", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }else{
            Log.e("TasksAdapter", "Indice fuori limite: $position, dimensione lista: ${filteredList.size}")
            for(el in filteredList)Log.e("TasksAdapter", el.nome)
        }

    }


    fun applyFilter(filters: Array<String>, checkedList: BooleanArray){
        filteredList = if (checkedList.isEmpty()){
            dataSet.toMutableList()
        }else{
            val tempFilters = ArrayList<String>()
            val len = checkedList.size

            var i = 0
            while(i < len-3){
                if(checkedList[i])tempFilters.add(filters[i])
                i++
            }

            // checkedList[len-1] e len-2 == <%50 && >50%
            // checkedList[len-4] == oggi
            // checkedList[len-3] == oggi + 1 mese

            var toRet: List<Task> = dataSet.toList()

            if(tempFilters.isNotEmpty()){
                toRet = toRet.filter{it.dev in tempFilters}
            }

            if(checkedList[len-2] || checkedList[len-1]){
                toRet = filterProgress(toRet, checkedList[len-2])
            }

            if(checkedList[len-4] || checkedList[len-3]){
                toRet = filterDate(toRet, checkedList[len-4])
            }

            toRet.toMutableList()
        }
        notifyDataSetChanged()
    }



    fun resetFilters(){
        filteredList = dataSet.toMutableList()
        notifyDataSetChanged()
    }

    fun searchTask(query: String){
        if(query.isNotEmpty()){
            filteredList = dataSet.filter{it.nome.contains(query, ignoreCase = true)}.toMutableList()
            notifyDataSetChanged()
        }
    }


    override fun getItemCount() = filteredList.size



    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun filterProgress(toFilter: List<Task>, lessThan: Boolean): List<Task>{
        val toRet = if(lessThan){
            toFilter.filter{it.progress <= 50}
        }else{
            toFilter.filter{it.progress >= 50}
        }
        return toRet
    }

    private fun filterDate(toFilter: List<Task>, now: Boolean): List<Task>{
        val currentMS = Timestamp.now()

        val toRet = if(now){
            toFilter.filter{ it.expire <= currentMS }
        }else{
            val calendar = Calendar.getInstance()
            calendar.time = currentMS.toDate()
            calendar.add(Calendar.MONTH, 1)

            toFilter.filter{it.expire <= Timestamp(calendar.time)}
        }
        return toRet
    }

}