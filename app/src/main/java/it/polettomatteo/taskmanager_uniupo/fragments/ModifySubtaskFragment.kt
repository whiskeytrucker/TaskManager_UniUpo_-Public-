package it.polettomatteo.taskmanager_uniupo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import it.polettomatteo.taskmanager_uniupo.R
import it.polettomatteo.taskmanager_uniupo.firebase.SubtasksDB
import java.util.Calendar
import java.util.Date

class ModifySubtaskFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modify_subtask, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if(arguments != null){
            val data = this.arguments

            if(data != null) {
                val id = data.getString("id")

                val modSubDescr = view.findViewById<EditText>(R.id.modSubDescr)
                val spinPriority = view.findViewById<Spinner>(R.id.spinPriority)
                val spinState = view.findViewById<Spinner>(R.id.spinState)
                val progressText = view.findViewById<TextView>(R.id.progressSubtask)
                val seekBar = view.findViewById<SeekBar>(R.id.seekbarModifySub)
                val modSubDate = view.findViewById<DatePicker>(R.id.modSubDate)
                val modSubTime = view.findViewById<TimePicker>(R.id.modSubTime)

                modSubDescr.hint = data.getString("subDescr")

                if(data.getInt("priorita") <= 0){spinPriority.setSelection(0)}
                else if(data.getInt("priorita") >= 4){spinPriority.setSelection(4)}
                else spinPriority.setSelection(data.getInt("priorita"))

                if(data.getInt("stato") <= 1){spinState.setSelection(0)}
                else if(data.getInt("stato") >= 3){spinState.setSelection(2)}
                else spinState.setSelection(1)



                // Progress
                progressText.text = "${data.getInt("progress")}%"
                seekBar.progress = data.getInt("progress")

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        progressText.text = "${progress}%"
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // Optional: azione quando l'utente inizia a muovere la SeekBar
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // Optional: azione quando l'utente smette di muovere la SeekBar
                    }
                })



                val ms = data.getLong("expiring")
                val date = Date(ms)

                val calendar = Calendar.getInstance()
                calendar.time = date

                modSubDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

                // Imposta l'ora nel TimePicker
                modSubTime.hour = calendar.get(Calendar.HOUR_OF_DAY)
                modSubTime.minute = calendar.get(Calendar.MINUTE)


                val submitBtn = view.findViewById<Button>(R.id.subModifySubButton)

                submitBtn.setOnClickListener{
                    var subDescr = modSubDescr.text.toString()
                    if(subDescr.compareTo("") == 0)subDescr = modSubDescr.hint.toString()

                    var priority = spinPriority.selectedItemPosition
                    if(priority < 0 || priority > 4)priority = 0

                    var state = spinState.selectedItemPosition + 1
                    if(state < 0 || state > 4)state = 0

                    val progress = seekBar.progress

                    val expiring = getTimestamp(modSubDate, modSubTime)

                    SubtasksDB.modifySubtask(id.toString(), subDescr, priority, state, progress, expiring) { bundle ->
                        if (bundle != null) {
                            if (bundle.getBoolean("result")) {
                                Toast.makeText(context, "Dati aggiornati!", Toast.LENGTH_SHORT).show()
                                bundle.remove("result")
                                requireActivity().supportFragmentManager.setFragmentResult("data",bundle)
                                requireActivity().supportFragmentManager.popBackStack();
                            }
                        } else {
                            Toast.makeText(context,"Errore nella modifica dei dati.",Toast.LENGTH_LONG).show()
                        }
                    }
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