package it.polettomatteo.taskmanager_uniupo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser

        val buttLogin = findViewById<Button>(R.id.firstBtn)
        val mailText = findViewById<TextInputEditText>(R.id.email)
        val passText = findViewById<TextInputEditText>(R.id.password)

        buttLogin.setOnClickListener {
            val email = mailText.text.toString()
            val pass = passText.text.toString()

            if(currentUser == null && validPass(pass) && controlloMail(email)){
                login(email, pass){ result ->
                    if(result == true){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }





    fun login(
        email: String,
        password: String,
        callback: (Boolean?) -> Unit
    ){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Toast.makeText(baseContext, "Login effettuato!", Toast.LENGTH_SHORT).show()
                    callback(true)
                }else{
                    Toast.makeText(baseContext, "Autenticazione fallita.", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }
            .addOnFailureListener{
                it.printStackTrace()
                callback(null)
            }
    }

    private fun validPass(password: String): Boolean {
        val regex = "^[a-zA-Z0-9@$!%*?&]+$".toRegex()
        return regex.matches(password)
    }

    private fun controlloMail(input: String): Boolean {
        val regex = "^[^@]*@[^@]*$".toRegex()
        return regex.matches(input)
    }
}
