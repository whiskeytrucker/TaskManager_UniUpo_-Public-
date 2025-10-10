package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import java.lang.NumberFormatException
import kotlin.reflect.typeOf

class TasksDB {

    companion object {
        private lateinit var idPrj: String
        fun getTasks(idProject: String, callback: (Bundle?) -> Unit) {
            idPrj = idProject

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for ((index, doc) in documents.withIndex()) {
                        val data = doc.data
                        if (data != null) {
                            val tmp = Task(
                                doc.id,
                                idProject,
                                data["nome"].toString(),
                                data["descr"].toString(),
                                data["dev"].toString(),
                                data["scadenza"] as Timestamp,
                                data["progress"].toString().toInt()
                            )

                            bundle.putSerializable(index.toString(), tmp)
                        }
                    }
                    callback(bundle)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }


        fun getTasksAsDev(idProject: String, developer: String, callback: (Bundle?) -> Unit) {

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .whereEqualTo("dev", developer)
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for ((index, doc) in documents.withIndex()) {
                        val data = doc.data
                        if (data != null) {

                            val tmp = Task(
                                doc.id,
                                idProject,
                                data["nome"].toString(),
                                data["descr"].toString(),
                                data["dev"].toString(),
                                data["scadenza"] as Timestamp,
                                data["progress"].toString().toInt()
                            )

                            bundle.putSerializable(index.toString(), tmp)
                        }
                    }
                    callback(bundle)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun getTaskTitle(idProject: String, idTask: String, callback: (String?) -> Unit){
            val db = FirebaseFirestore.getInstance()

            db.collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .get()
                .addOnSuccessListener { doc ->
                    callback(doc["nome"].toString())
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }


        fun addTask(
            title: String,
            descr: String,
            assigned: String,
            expiring: Timestamp,
            callback: (Bundle?) -> Unit
        ){
            val data = hashMapOf(
                "nome" to title,
                "descr" to descr,
                "dev" to assigned,
                "scadenza" to expiring,
                "progress" to 0
            )

            val doc =  FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idPrj)
                .collection("task")
                .document()

            doc.set(data)
            .addOnSuccessListener {
                val task = Task(
                    doc.id,
                    idPrj,
                    title,
                    descr,
                    assigned,
                    expiring,
                    0,
                )
                val bun = Bundle()
                bun.putSerializable("data", task)
                bun.putBoolean("result", true)

                callback(bun)
            }.addOnFailureListener{
                it.printStackTrace()
                callback(null)
            }


        }

        fun modifyTask(
            idTask: String,
            title: String,
            descr: String,
            assigned: String,
            progress: Int,
            expiring: Timestamp,
            callback: (Bundle?) -> Unit
        ){
            val data = hashMapOf(
                "nome" to title,
                "descr" to descr,
                "dev" to assigned,
                "progress" to progress,
                "scadenza" to expiring
            )

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idPrj)
                .collection("task")
                .document(idTask)
                .set(data)
                .addOnSuccessListener {
                    val task = Task(
                        idTask,
                        idPrj,
                        title,
                        descr,
                        assigned,
                        expiring,
                        progress,
                    )
                    val bun = Bundle()
                    bun.putString("done", "mod")
                    bun.putSerializable("data", task)
                    bun.putBoolean("result", true)

                    callback(bun)
                }.addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun deleteTask(idProject: String, idTask: String, callback: (Boolean?) -> Unit) {
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .delete()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(false)
                }
        }


        fun updateTaskProgress(idProject: String, idTask: String, progressVal: Double){
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .update("progress", progressVal.toInt())
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }
}