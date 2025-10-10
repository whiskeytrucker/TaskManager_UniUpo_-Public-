package it.polettomatteo.taskmanager_uniupo.dataclass

import com.google.firebase.Timestamp
import java.io.Serializable

data class Subtask(
    val id: String = "",
    val idTask: String = "",
    val idPrg: String = "",
    val stato: Int = 0,
    val subDescr: String = "",
    val priorita: Int = 0,
    val scadenza: Timestamp,
    val progress: Int
): Serializable
