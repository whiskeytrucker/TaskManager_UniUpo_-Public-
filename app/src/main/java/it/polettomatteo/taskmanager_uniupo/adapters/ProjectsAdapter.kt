package it.polettomatteo.taskmanager_uniupo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.firebase.NotificationDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.fragments.TasksViewFragment
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler


@SuppressLint("NotifyDataSetChanged")
class ProjectsAdapter(private val userType: String, private val context: Context, private var dataSet: ArrayList<Project>, private val listener: StartNewRecycler) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
        val author: TextView = view.findViewById(R.id.authorProject)
        val assigned: TextView = view.findViewById(R.id.assigned)
        val progress: TextView = view.findViewById(R.id.progress)
        val seekBar: SeekBar = view.findViewById(R.id.seekBar)
        val promptPL: Button = view.findViewById(R.id.promptPL)


        init {
            seekBar.isEnabled = false
            seekBar.progress = 0
        }
    }

    private var filteredList: MutableList<Project> = dataSet.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var id = filteredList[position].id
        var auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser

        // prendi l'elemento dal dataset e rimpazza i contenuti
        holder.title.text = filteredList[position].titolo
        holder.content.text = filteredList[position].descr

        if (currentUser != null) {
            if(currentUser.email?.compareTo(filteredList[position].assigned) == 0) {
                holder.assigned.visibility = View.GONE
                holder.author.text = "Project Manager: ${filteredList[position].autore}"
            }else{
                holder.author.visibility = View.GONE
                holder.assigned.text = "Project Leader: ${filteredList[position].assigned}"
            }
        }
        holder.progress.text = "${filteredList[position].progress}%"
        holder.seekBar.progress = filteredList[position].progress

        if(userType.compareTo("pm") == 0){
            holder.promptPL.visibility = View.VISIBLE
        }

        holder.promptPL.setOnClickListener {
            NotificationDB.promptUser(filteredList[position].assigned, filteredList[position].titolo){ result ->
                if(result == true) Toast.makeText(context, "Notifica inviata!", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Errore nel mandare la notifica", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener {
            TasksDB.getTasks(id){bundle ->
                if(bundle != null){
                    dataSet.clear()
                    listener.onStartNewRecyclerView(bundle)
                }

            }

        }
    }

    fun applyFilter(filters: Array<String>, checkedList: BooleanArray){
        filteredList = if (checkedList.isEmpty()){
            dataSet.toMutableList()
        }else{
            val tempFilters = ArrayList<String>()
            val len = checkedList.size

            var i = 0
            while(i < len-2){
                if(checkedList[i])tempFilters.add(filters[i])
                i++
            }


            var toRet: List<Project> = dataSet.toList()

            if(tempFilters.isNotEmpty()) {
                toRet = toRet.filter{it.assigned in tempFilters}
            }

            if(checkedList[len-2] || checkedList[len-1]){
                toRet = filterProgress(toRet, checkedList[len-2])
            }


            toRet.toMutableList()
        }

        notifyDataSetChanged()
    }

    fun resetFilters(){
        filteredList = dataSet.toMutableList()
        notifyDataSetChanged()
    }

    fun searchProject(query: String){
        if(query.isNotEmpty()){
            filteredList = dataSet.filter{it.titolo.contains(query, ignoreCase = true)}.toMutableList()
            notifyDataSetChanged()
        }
    }


    override fun getItemCount() = filteredList.size


    private fun filterProgress(toFilter: List<Project>, lessThan: Boolean): List<Project>{
        val toRet = if(lessThan){
            toFilter.filter{it.progress <= 50}
        }else{
            toFilter.filter{it.progress >= 50}
        }
        return toRet
    }
}
