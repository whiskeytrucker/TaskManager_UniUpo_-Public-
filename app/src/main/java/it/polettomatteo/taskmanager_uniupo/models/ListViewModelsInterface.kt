package com.example.appupo.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import it.polettomatteo.taskmanager_uniupo.dataclass.Project

open class ListViewModelInterface(application: Application): AndroidViewModel(application) {
    val productsArrayFiltered: MutableLiveData<ArrayList<Project>> = MutableLiveData()
}