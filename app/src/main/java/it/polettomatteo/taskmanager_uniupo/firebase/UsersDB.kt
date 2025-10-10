package it.polettomatteo.taskmanager_uniupo.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class UsersDB {
    companion object{
        fun getUserType(username: String, callback: (Bundle?) -> Unit){
            FirebaseFirestore
                .getInstance()
                .collection("tipo_utenti")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener{result ->
                    val bundle = Bundle()
                    for(data in result.documents){
                        bundle.putSerializable("tipo", data["tipo"].toString())
                    }

                    callback(bundle)
                }
                .addOnFailureListener{
                    it.printStackTrace()
                    callback(null)
                }
        }

        fun setUserType(username: String, type: String, callback: (Boolean?) -> Unit){
            val data = hashMapOf(
                "username" to username,
                "tipo" to type
            )

            FirebaseFirestore
                .getInstance()
                .collection("tipo_utenti")
                .document()
                .set(data)
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
