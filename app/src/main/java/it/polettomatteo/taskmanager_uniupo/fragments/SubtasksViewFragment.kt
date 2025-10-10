package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.SubtasksAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask
import it.polettomatteo.taskmanager_uniupo.firebase.NotificationDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity
import kotlin.math.ceil

class SubtasksViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var addStuffBtn: Button
    private lateinit var goBackBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var subtasksAdapter: SubtasksAdapter
    private lateinit var tmp: ArrayList<Subtask>
    private var userType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_subtaskview, container, false)
        recyclerView = view.findViewById(R.id.recyclerTaskView)

        addStuffBtn = view.findViewById(R.id.addStuff)
        goBackBtn = view.findViewById(R.id.goBack)

        val bundle: Bundle?

        if(savedBundle != null && this.arguments == null){
            bundle = savedBundle
        }else{
            bundle = this.arguments
        }

        tmp = ArrayList()

        if(bundle != null){
            if(bundle.getString("tipo") != null){
                userType = bundle.getString("tipo")!!
                bundle.remove("tipo")
            }


            for(key in bundle.keySet()){
                tmp.add(bundle.getSerializable(key) as Subtask)
            }


            if(userType.compareTo("d") == 0 || userType.compareTo("pl") == 0){
                addStuffBtn.visibility = View.VISIBLE
            }

        }

        tmp.sortWith(compareByDescending<Subtask>{it.priorita}.thenBy { it.scadenza })

        subtasksAdapter =
            context?.let { SubtasksAdapter(userType, it, tmp, modifyActivityListener, commentsListener) }!! // <-- Da cambiare con i dati presi da savedInstanceState
        recyclerView.adapter = subtasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    private val modifyActivityListener = object: TempActivity {
        override fun onStartNewTempActivity(data: Bundle) {
            val fragment = ModifySubtaskFragment()
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }

    private val commentsListener = object: TempActivity{
        override fun onStartNewTempActivity(data: Bundle) {
            val fragment = CommentsViewFragment()
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onStart(){
        super.onStart()

        goBackBtn.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        addStuffBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, AddSubtaskFragment())
                .addToBackStack(null)
                .commit()
        }



        val key = "data"
        parentFragmentManager.setFragmentResultListener(key, this){key, bundle ->
            val subtask = bundle.getSerializable("data") as Subtask
            val done = bundle.getString("done")

            if (done?.compareTo("mod") == 0){
                val i = findIndex(subtask)
                if(i != -1){
                    tmp.removeAt(i)
                    subtasksAdapter.notifyItemRemoved(i)
                }
            }

            tmp.add(subtask)
            tmp.sortWith(compareByDescending<Subtask>{it.priorita}.thenBy { it.scadenza })
            subtasksAdapter.notifyItemInserted(tmp.indexOf(subtask))


            var med = 0.0
            for(subTask in tmp){
                med += subTask.progress
            }

            med = ceil(med/tmp.size)
            TasksDB.updateTaskProgress(tmp[0].idPrg, tmp[0].idTask, med)

            if(med >= 100){
                NotificationDB.notifySuperior(tmp[0].idPrg, tmp[0].idTask)
            }
        }


    }


    override fun onPause() {
        savedBundle = this.arguments
        super.onPause()
    }
    private fun findIndex(toFind: Subtask):Int{
        for(subtask in tmp){
            if(subtask.id == toFind.id)return tmp.indexOf(subtask)
        }
        return -1
    }

}