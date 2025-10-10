@file:Suppress("OverrideDeprecatedMigration")

package it.polettomatteo.taskmanager_uniupo.fragments

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.ProjectsAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Project
import it.polettomatteo.taskmanager_uniupo.interfaces.StartNewRecycler

class ProjectsViewFragment: Fragment() {
    private var savedBundle: Bundle? = null
    private lateinit var savedAdapter: ProjectsAdapter
    private var notificationManager: NotificationManager? = null
    private lateinit var recyclerView: RecyclerView

    private lateinit var resetBtn: Button
    private lateinit var filterBtn: Button
    private var arrProjects = ArrayList<Project>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_projectview, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = view.findViewById(R.id.recyclerView)

        val searchbar = view.findViewById<SearchView>(R.id.search_bar)
        resetBtn = view.findViewById(R.id.reset)
        filterBtn = view.findViewById(R.id.filter)

        initSearchView(searchbar)

        val bundle: Bundle?
        var userType: String = ""

        if(savedBundle != null && this.arguments == null){bundle = savedBundle}
        else{bundle = this.arguments}


        var listener: StartNewRecycler? = null
        if(bundle != null){
            for(key in bundle.keySet()){
                if(key.compareTo("tipo") == 0){
                    userType = bundle.getString("tipo")!!
                }else if(key.compareTo("task_interface") == 0){
                    listener = bundle.getSerializable("task_interface") as StartNewRecycler
                }else {
                    arrProjects.add(bundle.getSerializable(key) as Project)
                }
            }
        }

        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(userType.compareTo("pm") != 0){
            val searchLayout = view.findViewById<RelativeLayout>(R.id.searchLayout)
            searchLayout.visibility = View.GONE
        }


        val projectsAdapter =
            listener?.let { context?.let { it1 -> ProjectsAdapter(userType, it1, arrProjects, it) } }!!
        recyclerView.adapter = projectsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)

        savedAdapter = projectsAdapter

        return view
    }

    override fun onStart(){
        super.onStart()

        resetBtn.setOnClickListener {
            val tmpAdp = recyclerView.adapter as ProjectsAdapter
            tmpAdp.resetFilters()
        }

        filterBtn.setOnClickListener {
            showFilterDialog()
        }

    }

    override fun onPause() {
        super.onPause()
        savedBundle = this.arguments
        savedAdapter = recyclerView.adapter as ProjectsAdapter
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter = savedAdapter
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
        val tmpAdp = recyclerView.adapter as ProjectsAdapter

        tmpAdp.searchProject(query)
    }


    private fun showFilterDialog() {
        val temp: MutableSet<String> = mutableSetOf()

        for(el in arrProjects){
            temp.add(el.assigned)
        }
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
                    val tmpAdp = recyclerView.adapter as ProjectsAdapter

                    tmpAdp.applyFilter(filters, checkedItems)
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
}