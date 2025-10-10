package it.polettomatteo.taskmanager_uniupo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.firebase.UsersDB

class RegisterActivity: AppCompatActivity()  {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        auth = FirebaseAuth.getInstance()



    }


    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser

        val buttRegister = findViewById<Button>(R.id.firstBtn)
        val mailText = findViewById<TextInputEditText>(R.id.email)
        val passText = findViewById<TextInputEditText>(R.id.password)
        val confirmpassText = findViewById<TextInputEditText>(R.id.confirmPassword)

        buttRegister.setOnClickListener {
            val email = mailText.text.toString()
            val pass = passText.text.toString()
            val conf = confirmpassText.text.toString()
            val tipoSpinner = findViewById<Spinner>(R.id.sel_tipo)
            var tipo:String = "d"

            if(currentUser == null && validPass(pass) && controlloMail(email) && pass.compareTo(conf) == 0){
                registerUser(email, pass){ result ->
                    if(result == true){
                        when(tipoSpinner.selectedItemPosition){
                            0 -> tipo = "d"
                            1 -> tipo = "pl"
                            2 -> tipo = "pm"
                            else -> {
                                tipo = "na"
                            }
                        }

                        val user = auth.currentUser
                        user?.let{
                            it.email?.let { it1 ->
                                UsersDB.setUserType(it1, tipo){ res ->
                                    if(res == true){
                                        Toast.makeText(baseContext, "Registrazione effettuata!", Toast.LENGTH_SHORT).show()

                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }

                                }
                            }

                        }

                    }
                }
            }
        }
    }

    fun registerUser(email: String, password: String, callback: (Boolean?) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        callback(true)
                    }
                }
            }.addOnFailureListener {
                it.printStackTrace()
                callback(false)
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
