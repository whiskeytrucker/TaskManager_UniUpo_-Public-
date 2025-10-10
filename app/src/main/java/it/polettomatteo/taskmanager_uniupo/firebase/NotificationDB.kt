package it.polettomatteo.taskmanager_uniupo.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.polettomatteo.taskmanager_uniupo.dataclass.Notification
import it.polettomatteo.taskmanager_uniupo.dataclass.User

class NotificationDB{
    companion object{

        fun getNotifications(user:String, callback: (ArrayList<Notification>?) -> Unit){
            val db = FirebaseFirestore.getInstance()

            db.collection("notifiche")
                .whereEqualTo("user", user)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    for(doc in docs){
                        db.collection("notifiche")
                            .document(doc.id)
                            .collection("unseen")
                            .get()
                            .addOnSuccessListener { result ->
                                val nots = ArrayList<Notification>()
                                for(inDoc in result){
                                    val data = inDoc.data
                                    val tmpNot = Notification(
                                        inDoc.id,
                                        data["title"].toString(),
                                        data["descr"].toString(),
                                        data["channel"].toString(),
                                        data["channelTitle"].toString(),
                                        data["channelDescr"].toString()
                                    )
                                    nots.add(tmpNot)
                                }

                                callback(nots)
                            }
                            .addOnFailureListener {
                                it.printStackTrace()
                                callback(null)
                            }
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun promptUser(user: String, objTitle: String, callback: (Boolean?) -> Unit) {
            val loggedUser = FirebaseAuth.getInstance().currentUser?.email

            val toSend = hashMapOf(
                "channel" to "it.taskmanager.prompt",
                "channelDescr" to "Canale notifiche per i solleciti.",
                "channelTitle" to "Solleciti",
                "descr" to "L'utente ${loggedUser} ti ha sollecitato per l'oggetto: ${objTitle}",
                "title" to "Sollecito"
            )


            val db = FirebaseFirestore.getInstance()

            db.collection("notifiche")
                .whereEqualTo("user", user)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs) {
                        db.collection("notifiche")
                            .document(doc.id)
                            .collection("unseen")
                            .document()
                            .set(toSend, SetOptions.merge())
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                it.printStackTrace()
                                callback(false)
                            }
                    }

                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(false)
                }
        }


        // D Notifica --> PL
        fun notifySuperior(idProject: String, idTask: String){
            TasksDB.getTaskTitle(idProject, idTask){ titleTask ->
                val toSend = hashMapOf(
                    "channel" to "it.taskmanager.task",
                    "channelDescr" to "Canale notifiche per le task.",
                    "channelTitle" to "Task",
                    "descr" to "La Task \"${titleTask.toString()}\" è stata completata.",
                    "title" to "Task Completate"
                )

                ProjectsDB.fetchSupervisorWithProjectID(idProject, false){ pl ->
                    val db = FirebaseFirestore.getInstance()
                    db.collection("notifiche")
                        .whereEqualTo("user", pl)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { docs ->
                            for (doc in docs) {
                                db.collection("notifiche")
                                    .document(doc.id)
                                    .collection("unseen")
                                    .document()
                                    .set(toSend, SetOptions.merge())
                                    .addOnFailureListener {
                                        it.printStackTrace()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                }
            }
        }

        // PL Notifica --> PM
        fun notifySuperior(idProject: String) {
            ProjectsDB.getProjectTitle(idProject) { titleProject ->
                val toSend = hashMapOf(
                    "channel" to "it.taskmanager.project",
                    "channelDescr" to "Canale notifiche per i progetti.",
                    "channelTitle" to "Project",
                    "descr" to "Il progetto \"${titleProject.toString()}\" è stata completato.",
                    "title" to "Progetti Completati"
                )

                ProjectsDB.fetchSupervisorWithProjectID(idProject, true) { pm ->
                    val db = FirebaseFirestore.getInstance()
                    db.collection("notifiche")
                        .whereEqualTo("user", pm)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { docs ->
                            for (doc in docs) {
                                db.collection("notifiche")
                                    .document(doc.id)
                                    .collection("unseen")
                                    .document()
                                    .set(toSend, SetOptions.merge())
                                    .addOnFailureListener {
                                        it.printStackTrace()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                }
            }
        }
    }

}