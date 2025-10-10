package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import it.polettomatteo.taskmanager_uniupo.dataclass.Comment
import it.polettomatteo.taskmanager_uniupo.dataclass.Subtask

class SubtasksDB {
    companion object{
        private lateinit var idPrg: String
        private lateinit var idTask: String


        fun getSubtasks(idProject: String, idTsk: String, callback: (Bundle?) -> Unit){
            idPrg = idProject
            idTask = idTsk

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bundle = Bundle()
                    for((index, doc) in documents.withIndex()){
                        val data = doc.data
                        if (data != null) {
                            val tmp = Subtask(
                                doc.id,
                                idTask,
                                idProject,
                                data["stato"].toString().toInt(),
                                data["subDescr"].toString(),
                                data["priorita"].toString().toInt(),
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

        fun getComments(idSubtask: String, callback: (Bundle?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idPrg)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .document(idSubtask)
                .collection("commenti")
                .get()
                .addOnSuccessListener { results ->
                    val documents = results.documents
                    val bun = Bundle()

                    for((index, doc) in documents.withIndex()) {
                        val data = doc.data
                        if (data != null) {
                            val tmp = Comment(
                                doc.id,
                                data["da"].toString(),
                                data["commento"].toString(),
                                data["voto"].toString().toInt()
                            )
                            bun.putSerializable(index.toString(), tmp)
                        }
                    }

                    callback(bun)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }
        }


        fun addSubtask(
            subDescr: String,
            priority: Int,
            state: Int,
            progresso: Int,
            expiring: Timestamp,
            callback: (Bundle?) -> Unit
        ) {
            val data = hashMapOf(
                "subDescr" to subDescr,
                "priorita" to priority,
                "stato" to state,
                "progress" to progresso,
                "scadenza" to expiring
            )
            val doc = FirebaseFirestore
                    .getInstance()
                    .collection("projects")
                    .document(idPrg)
                    .collection("task")
                    .document(idTask)
                    .collection("sotto_task")
                    .document()

            doc.set(data)
                .addOnSuccessListener {
                    val subtask = Subtask(
                        doc.id,
                        idTask,
                        idPrg,
                        state,
                        subDescr,
                        priority,
                        expiring,
                        0,
                    )
                    val bun = Bundle()
                    bun.putSerializable("data", subtask)
                    bun.putBoolean("result", true)

                    callback(bun)
                }.addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }


        fun modifySubtask(
            idSub: String,
            subDescr: String,
            priority: Int,
            state: Int,
            progresso: Int,
            expiring: Timestamp,
            callback: (Bundle?) -> Unit
        ) {
            val data = hashMapOf(
                "subDescr" to subDescr,
                "priorita" to priority,
                "stato" to state,
                "progress" to progresso,
                "scadenza" to expiring
            )

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idPrg)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .document(idSub)
                .set(data)
                .addOnSuccessListener {
                    val subtask = Subtask(
                        idSub,
                        idTask,
                        idPrg,
                        state,
                        subDescr,
                        priority,
                        expiring,
                        progresso,
                    )
                    val bun = Bundle()
                    bun.putString("done", "mod")
                    bun.putSerializable("data", subtask)
                    bun.putBoolean("result", true)

                    callback(bun)
                }.addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }

        }
        fun deleteSubtask(idProject: String, idTask: String, idSubtask: String, callback: (Boolean?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .collection("task")
                .document(idTask)
                .collection("sotto_task")
                .document(idSubtask)
                .delete()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(false)
                }
        }
    }
}