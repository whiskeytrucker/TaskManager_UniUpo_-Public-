package it.polettomatteo.taskmanager_uniupo.dataclass

import java.io.Serializable

data class Notification(
    val id: String,
    val title: String,
    val descr: String,
    val channelID: String,
    val channelTitle: String,
    val channelDescr: String,
): Serializable
