package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.polettomatteo.taskmanager_uniupo.dataclass.Message

class ChatDB {
    companion object{
        fun sendMessage(messageText: String, sender: String,callback: (Message?) -> Unit){
            val db = FirebaseFirestore
                    .getInstance()
            val msg = hashMapOf(
                "sender" to true,
                "text" to messageText,
                "timestamp" to Timestamp.now()
            )

            val currentUser = FirebaseAuth.getInstance().currentUser

            if(currentUser != null){
                db.collection("chat")
                    .whereEqualTo("user0", currentUser.email)
                    .whereEqualTo("user1", sender)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs.documents) {
                            db.collection("chat")
                                .document(doc.id)
                                .collection("messages")
                                .document()
                                .set(msg)
                                .addOnSuccessListener {
                                    val tmp = Message(
                                        true,
                                        messageText,
                                        Timestamp.now()
                                    )

                                    callback(tmp)
                                }
                                .addOnFailureListener { callback(null) }
                        }
                    }
                    .addOnFailureListener { it.printStackTrace(); callback(null) }
            }

        }

        fun getReceivers(userMail: String, callback: (Bundle?) -> Unit) {
            val db = FirebaseFirestore
            .getInstance()

            db.collection("chat")
                .whereEqualTo("user0", userMail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if(!querySnapshot.isEmpty){
                        val docs = querySnapshot.documents
                        val receivers = Bundle()

                        for((i, doc) in docs.withIndex()){
                            val data = doc.data
                            if(data != null){
                                receivers.putString(i.toString(), data["user1"].toString())
                            }
                        }

                        callback(receivers)
                    }
                }


                .addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }



        fun getOldMessages(user0: String, user1: String, callback: (Bundle?) -> Unit){
            val db = FirebaseFirestore
                .getInstance()

            db.collection("chat")
                .whereEqualTo("user0", user0)
                .whereEqualTo("user1", user1)
                .get()
                .addOnSuccessListener { docs ->
                    for(doc in docs.documents){
                        db.collection("chat")
                            .document(doc.id)
                            .collection("messages")
                            .orderBy("timestamp", Query.Direction.ASCENDING)
                            .get()
                            .addOnSuccessListener { result ->
                                val documents = result.documents
                                val bundle = Bundle()

                                for ((i, docIn) in documents.withIndex()) {
                                    val data = docIn.data
                                    if(data != null){
                                        val tmp = Message(
                                            data["sender"].toString().toBoolean(),
                                            data["text"].toString(),
                                            data["timestamp"] as Timestamp
                                        )

                                        bundle.putSerializable(i.toString(), tmp)
                                    }
                                }
                                callback(bundle)
                            }

                            .addOnFailureListener{
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
    }
}









