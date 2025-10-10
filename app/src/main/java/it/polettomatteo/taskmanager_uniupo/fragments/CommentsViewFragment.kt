package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.adapters.CommentsAdapter
import it.polettomatteo.taskmanager_uniupo.dataclass.Comment

class CommentsViewFragment: Fragment()  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var goBackBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.recycler_projectview, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        goBackBtn = view.findViewById(R.id.goBack)


        val bundle = this.arguments
        val tmp = ArrayList<Comment>()
        if(bundle != null) {
            for (key in bundle.keySet()) {
                tmp.add(bundle.getSerializable(key) as Comment)
            }
        }

        val commentsAdapter = CommentsAdapter(tmp)

        recyclerView.adapter = commentsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
        return view
    }

    override fun onStart() {
        super.onStart()

        goBackBtn.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}