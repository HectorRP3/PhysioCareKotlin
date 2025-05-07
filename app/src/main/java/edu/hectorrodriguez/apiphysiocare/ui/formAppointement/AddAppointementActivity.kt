package edu.hectorrodriguez.apiphysiocare.ui.formAppointement

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import edu.hectorrodriguez.apiphysiocare.R
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.databinding.ActivityAddAppointementBinding
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementResponse
import edu.hectorrodriguez.apiphysiocare.ui.detailRecord.DetailRecord
import edu.hectorrodriguez.apiphysiocare.ui.detailRecord.DetailRecordViewModel
import edu.hectorrodriguez.apiphysiocare.ui.detailRecord.DetailRecordViewModelFactory
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.getValue

class AddAppointementActivity : AppCompatActivity() {
    private val TAG = AddAppointementActivity::class.java.simpleName

    companion object {
        const val RECORD_ID = "RECORD_ID"
        fun navigate(activity: Activity,recordId:String = ""){
            val intent = Intent(activity, AddAppointementActivity::class.java).apply {
                putExtra(RECORD_ID, recordId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity.startActivity(
                intent ,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }
    private val vm: AddAppointmentViewModel by viewModels{
        val repository = Repository(SessionManager(dataStore))
        val recordId = intent.getStringExtra(RECORD_ID).toString()
        Log.i(TAG,"recordId: $recordId")
        AddAppointmentFactory(repository,recordId)

    }
    lateinit var binding: ActivityAddAppointementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddAppointementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var diagnosis: String
        var date: String
        var observations: String
        var treatment: String
        //accion para agregar una cita
        binding.btnAddAppointment.setOnClickListener {
            diagnosis = binding.tietDiagnosis.text.toString()
            date = binding.etDate.text.toString()
            observations = binding.tietObservation.text.toString()
            treatment = binding.tietTreatment.text.toString()
            var idRecord = intent.getStringExtra(RECORD_ID).toString()

            if(diagnosis.isEmpty() || date.isEmpty() || observations.isEmpty() || treatment.isEmpty()){
                binding.tietDiagnosis.error = getString(R.string.campo_requerido)
                binding.etDate.error =getString(R.string.campo_requerido)
                binding.tietObservation.error = getString(R.string.campo_requerido)
                binding.tietTreatment.error = getString(R.string.campo_requerido)
                return@setOnClickListener
            }
            val dateArray = date.split("/")
            Log.i(TAG,"dateArray: $dateArray")
            if(dateArray[0] > 12.toString() || dateArray[1] > 31.toString()){
                binding.etDate.error = getString(R.string.fecha_incorrecya)
                return@setOnClickListener
            }
            vm.addAppointemnt(
                    date,
                    diagnosis,
                    observations,
                    treatment,
                )
            Toast.makeText(this@AddAppointementActivity,
                "", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(600)
            }
            finish()


        }




    }
}