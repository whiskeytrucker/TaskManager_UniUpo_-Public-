package it.polettomatteo.taskmanager_uniupo.interfaces

import android.os.Bundle
import java.io.Serializable

interface TempActivity : Serializable {
    fun onStartNewTempActivity(data: Bundle)
}