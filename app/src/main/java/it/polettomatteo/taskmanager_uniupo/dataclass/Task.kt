package it.polettomatteo.taskmanager_uniupo.dataclass

import com.google.firebase.Timestamp
import java.io.Serializable

data class Task(
    var id: String = "",
    var idPrg: String = "",
    var nome: String = "",
    var descr: String = "",
    var dev: String = "",
    var expire: Timestamp,
    var progress: Int = 0
):Serializable
