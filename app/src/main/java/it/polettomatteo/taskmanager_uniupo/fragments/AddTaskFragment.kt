package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.dataclass.Task
import it.polettomatteo.taskmanager_uniupo.firebase.TasksDB
import java.util.Calendar

class AddTaskFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.new_task, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val submitBtn = view.findViewById<Button>(R.id.submitTaskButton)



        submitBtn.setOnClickListener{
            //piglia i dati
            val title = view.findViewById<EditText>(R.id.addTitle).text
            val descr = view.findViewById<EditText>(R.id.addDescr).text
            val assigned = view.findViewById<EditText>(R.id.addAssigned).text
            val dateP = view.findViewById<DatePicker>(R.id.addDate)
            val timeP = view.findViewById<TimePicker>(R.id.addTime)

            val expiring = getTimestamp(dateP, timeP)


            TasksDB.addTask(title.toString(), descr.toString(), assigned.toString(), expiring){ bundle ->
                if (bundle != null) {
                    if(bundle.getBoolean("result")){
                        requireActivity().supportFragmentManager.setFragmentResult("data", bundle)
                        requireActivity().supportFragmentManager.popBackStack();
                    }
                }else{
                    Toast.makeText(context, "Errore nell'aggiunta dei dati.", Toast.LENGTH_LONG).show()
                }

            }


        }
        return view
    }

    private fun getTimestamp(dp: DatePicker, time: TimePicker): Timestamp {
        // Ottieni la data dal DatePicker
        val day = dp.dayOfMonth
        val month = dp.month
        val year = dp.year

        // Ottieni l'ora dal TimePicker
        val hour = time.hour
        val minute = time.minute

        // Crea un oggetto Calendar e imposta la data e l'ora
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Ottieni l'oggetto Date dalla Calendar
        val date = calendar.time

        // Converti l'oggetto Date in un Timestamp di Firebase
        val timestamp = Timestamp(date)

        return timestamp
    }
}