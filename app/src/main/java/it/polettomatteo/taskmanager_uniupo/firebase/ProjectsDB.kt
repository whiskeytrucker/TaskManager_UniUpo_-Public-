package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import it.polettomatteo.taskmanager_uniupo.dataclass.Project


class ProjectsDB {
    companion object{
        fun getProjects(username: String, usertype: String, callback: (Bundle?) -> Unit) {
            var field: String = ""

            if(usertype.isNotEmpty() && usertype.isNotBlank()) {
                if (usertype.compareTo("pm") == 0) {
                    field = "autore"
                } else if (usertype.compareTo("pl") == 0) {
                    field = "assigned"
                }
            }else{
                callback(null)
            }

            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .whereEqualTo(field.trim(), username)
                .get()
                .addOnSuccessListener {results ->
                    val bundle = Bundle()
                    if(results.size() > 0){
                        val documents = results.documents
                        for((index, doc) in documents.withIndex()){
                            val data = doc.data
                            if (data != null) {
                                val tmp = Project(
                                    doc.id,
                                    data["titolo"].toString(),
                                    data["descr"].toString(),
                                    data["assigned"].toString(),
                                    data["autore"].toString(),
                                    data["progress"].toString().toInt()
                                )

                                bundle.putSerializable(index.toString(), tmp)
                            }
                        }
                    }


                    callback(bundle)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    callback(null)
                }

        }

        fun getIdProjectAsDev(username: String, callback: (String?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("devInProject")
                .whereEqualTo("dev", username)
                .get()
                .addOnSuccessListener { result ->
                    if(result.size() > 0){
                        val document = result.documents
                        callback(document[0]["idProject"].toString())
                    }

                }
                .addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun updateTaskProgress(idProject: String, progressVal: Double){
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .update("progress", progressVal.toInt())
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }

        fun fetchSupervisorWithProjectID(idProject: String, is_PM: Boolean, callback:(String) -> Unit){
            FirebaseFirestore.getInstance()
                .collection("projects")
                .document(idProject)
                .get()
                .addOnSuccessListener { doc ->
                    if(is_PM)callback(doc["autore"].toString())
                    else callback(doc["assigned"].toString())
                }

        }

        fun getProjectTitle(idProject: String, callback: (String?) -> Unit) {
            FirebaseFirestore
                .getInstance()
                .collection("projects")
                .document(idProject)
                .get()
                .addOnSuccessListener { data ->
                    callback(data["titolo"].toString())
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }


}
