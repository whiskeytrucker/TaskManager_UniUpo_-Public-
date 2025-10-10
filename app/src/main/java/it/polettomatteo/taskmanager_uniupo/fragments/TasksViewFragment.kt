package it.polettomatteo.taskmanager_uniupo.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.ProjectsAdapter
import it.polettomatteo.taskmanager_uniupo.adapters.TasksAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.firebase.NotificationDB
import it.polettomatteo.taskmanager_uniupo.firebase.ProjectsDB
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler
import it.polettomatteo.taskmanager_uniupo.interfaces.TempActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

class TasksViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var addStuffBtn: Button
    private lateinit var goBackBtn: Button

    private lateinit var resetBtn: Button
    private lateinit var filterBtn: Button

    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var userType: String
    private var arrTasks = ArrayList<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_taskview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addStuffBtn = view.findViewById(R.id.addStuff)
        goBackBtn = view.findViewById(R.id.goBack)
        resetBtn = view.findViewById(R.id.reset)
        filterBtn = view.findViewById(R.id.filter)
        recyclerView = view.findViewById(R.id.recyclerTaskView)

        val searchbar = view.findViewById<SearchView>(R.id.search_bar)
        initSearchView(searchbar)


        var bundle: Bundle?
        arrTasks = ArrayList()


        if(savedBundle != null && this.arguments == null){
            bundle = savedBundle
        }else{
           bundle = this.arguments
        }


        if(bundle != null){
            for(key in bundle.keySet()){
                if(key.compareTo("tipo") == 0)userType = bundle.getString("tipo")!!
                else arrTasks.add(bundle.getSerializable(key) as Task)
            }

            if(userType.compareTo("pl") == 0){
                addStuffBtn.visibility = View.VISIBLE

                val searchLayout = view.findViewById<RelativeLayout>(R.id.searchLayout)
                searchLayout.visibility = View.VISIBLE
            }

        }

        arrTasks.sortWith(compareBy{it.nome})


        tasksAdapter =
            subtaskListener.let { context?.let { it1 ->
                TasksAdapter(
                    userType,
                    it1,
                    arrTasks,
                    it,
                    modifyActivityListener,
                    deleteActivityListener
                )
            } }!!
        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        return view
    }


    override fun onStart(){
        super.onStart()

        goBackBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack();
        }

        addStuffBtn.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, AddTaskFragment())
                .addToBackStack(null)
                .commit()
        }

        resetBtn.setOnClickListener {
            val tmpAdp = recyclerView.adapter as TasksAdapter
            tmpAdp.resetFilters()
        }

        filterBtn.setOnClickListener {
            showFilterDialog()
        }

        val key = "data"
        parentFragmentManager.setFragmentResultListener(key, this){key, bundle ->
            val task = bundle.getSerializable("data") as Task
            val done = bundle.getString("done")

            if (done?.compareTo("mod") == 0){
                val i = findIndex(task)
                if(i != -1){
                    arrTasks.removeAt(i)
                    tasksAdapter.notifyItemRemoved(i)
                }

            }

            arrTasks.add(task)
            arrTasks.sortWith(compareBy{it.nome})
            tasksAdapter.notifyItemInserted(arrTasks.indexOf(task))

            var med = 0.0
            for(taskArr in arrTasks){med += taskArr.progress}

            med = ceil(med/arrTasks.size)
            ProjectsDB.updateTaskProgress(arrTasks[0].idPrg, med)

            if(med >= 100){
                NotificationDB.notifySuperior(arrTasks[0].idPrg)
            }
        }

    }

    override fun onPause() {
        savedBundle = this.arguments
        tasksAdapter = recyclerView.adapter as TasksAdapter
        super.onPause()
    }

    override fun onResume(){
        recyclerView.adapter = tasksAdapter
        super.onResume()
    }



    /* LISTENER */
    val subtaskListener = object: StartNewRecycler{
        override fun onStartNewRecyclerView(data: Bundle){
            val fragment = SubtasksViewFragment()

            data.putSerializable("tipo", userType)
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit()

        }
    }



    val modifyActivityListener = object: TempActivity{
        override fun onStartNewTempActivity(data: Bundle) {
            val fragment = ModifyTaskFragment()
            fragment.arguments = data

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }

    val deleteActivityListener = object: TempActivity{
        override fun onStartNewTempActivity(data: Bundle) {
            val position = data.getInt("pos")
            val id = data.getString("id")
            arrTasks.removeAt(position)
            tasksAdapter.notifyItemRemoved(position)
            tasksAdapter.notifyItemRangeChanged(position, arrTasks.size)

            if(savedBundle != null){
                arguments?.remove(id?.let { findIndex(it, arguments!!) })
            }else{
                savedBundle?.remove(id?.let { findIndex(it, savedBundle!!) })
            }

        }
    }






    // AUSILIARIE
    private fun findIndex(toFind: Task):Int{
        for(task in arrTasks){
            if(task.id == toFind.id)return arrTasks.indexOf(task)
        }
        return -1
    }

    private fun findIndex(toFind: String, bundle: Bundle):String{
        for(key in bundle.keySet()){
            if(key.compareTo("tipo") != 0 && key.compareTo("subtask_interface") != 0){
                val task = bundle.getSerializable(key) as Task
                if(task.id == toFind)return key
            }
        }
        return ""
    }

    private fun initSearchView(searchView: SearchView){
        searchView.queryHint = "Cerca qui..."
        searchView.isIconified = false
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let{
                    performSearch(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    performSearch(newText)
                }
                return true
            }
        })
    }

    private fun performSearch(query: String){
        tasksAdapter.searchTask(query)
    }

    private fun showFilterDialog() {
        val temp: MutableSet<String> = mutableSetOf()
        for(el in arrTasks){
            temp.add(el.dev)
        }
        val currentDate = Date()

        val ms = 24 * 60 * 60 * 1000

        temp.add(formatTimestamp(currentDate)) // Oggi
        currentDate.time += 30L * ms
        temp.add(formatTimestamp(currentDate)) // Oggi + 1 mese

        temp.add("<50%")
        temp.add(">50%")

        val filters = temp.toTypedArray()
        val checkedItems = BooleanArray(filters.size){false}


        AlertDialog.Builder(requireContext())
            .setTitle("Seleziona Filtri")
            .setMultiChoiceItems(filters, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Applica") { _, _ ->
                if(checkedItems.contains(true)){
                    val tmpAdp = recyclerView.adapter as TasksAdapter
                    tmpAdp.applyFilter(filters, checkedItems)
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }


    private fun formatTimestamp(timestamp: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
