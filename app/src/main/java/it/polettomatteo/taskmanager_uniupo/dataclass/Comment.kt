package it.polettomatteo.taskmanager_uniupo.dataclass

import java.io.Serializable

data class Comment(
    var id: String = "",
    var from: String = "",
    val text: String = "",
    val vote: Int = 0
): Serializable