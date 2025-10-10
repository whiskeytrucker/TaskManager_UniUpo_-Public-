package it.polettomatteo.taskmanager_uniupo.dataclass

import java.io.Serializable

data class Project(
    val id: String = "",
    val titolo: String = "",
    val descr: String = "",
    val assigned: String = "",
    val autore: String = "",
    val progress: Int = 0
): Serializable