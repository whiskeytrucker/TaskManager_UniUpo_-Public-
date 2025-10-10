package it.polettomatteo.taskmanager_uniupo.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.polettomatteo.taskmanager_uniupo.R

class ResultActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userpage)

        Log.d("ResultActivity", "Sono nella resultActivity")
    }
}