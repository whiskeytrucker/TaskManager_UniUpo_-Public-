package it.polettomatteo.taskmanager_uniupo.interfaces

import android.os.Bundle
import java.io.Serializable

interface StartNewRecycler : Serializable{
    fun onStartNewRecyclerView(data: Bundle)
}