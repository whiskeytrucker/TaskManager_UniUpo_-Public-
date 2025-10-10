package it.polettomatteo.taskmanager_uniupo.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class DeleteNotification: BroadcastReceiver() {

    override fun onReceive(context: Context, intentIn: Intent) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val notificationID = intentIn.getStringExtra("notificationID")
        intentIn.removeExtra("notificationID")


        if (currentUser != null && notificationID != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("notifiche")
                .whereEqualTo("user", currentUser.email)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs) {
                        db.collection("notifiche")
                            .document(doc.id)
                            .collection("unseen")
                            .document(notificationID)
                            .delete()
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                                it.printStackTrace()
                            }
                    }
                }
        }
    }
}

