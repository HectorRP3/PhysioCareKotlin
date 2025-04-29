package edu.hectorrodriguez.apiphysiocare.ui.detailAppointment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Html
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
import edu.hectorrodriguez.apiphysiocare.databinding.ActivityDetailAppointmentBinding
import edu.hectorrodriguez.apiphysiocare.ui.login.LoginActivity
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DetailAppointmentActivity : AppCompatActivity() {
    private val TAG = DetailAppointmentActivity::class.java.simpleName
    private lateinit var binding: ActivityDetailAppointmentBinding
    companion object {
        const val APPOINTMENT_ID = "APPOINTMENT_ID"
        fun navigate(activity: Activity,appoinmentId:String = ""){
            val intent = Intent(activity, DetailAppointmentActivity::class.java).apply {
                putExtra(APPOINTMENT_ID, appoinmentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity.startActivity(
                intent ,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }
    private val vm: DetailAppointmentViewModel by viewModels{
        val repository = Repository(SessionManager(dataStore))
        val appointmentId = intent.getStringExtra(APPOINTMENT_ID).toString()
        DetailAppointmentViewModelFactory(repository,appointmentId)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mToolbar.setTitle(getString(R.string.title_cita))

        runCatching {
            vm.getAppointemnt()
        }
        .onFailure {
            if(vm.appointment.value == null){
                Toast.makeText(
                    this@DetailAppointmentActivity, "Error no se ha podido coger el appointemnt",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }


    }

    override fun onStart() {
        super.onStart()
        if(checkConnection(this)) {
            lifecycleScope.launch {
                combine(vm.appointment,vm.physio) { appointment, physio ->
                    if(appointment != null && physio != null) {
                        Log.e("TAG", "onStart: ${appointment}")
                        val date = appointment.date
                        val dateFormar = Instant.parse(date).atZone(ZoneOffset.UTC)
                        val resultado = dateFormar.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        binding.tvDate.setText(
                            Html.fromHtml(
                                getString(R.string.dia, resultado),
                                Html.FROM_HTML_MODE_LEGACY
                            )
                        )
                        binding.tvDate.text = getString(R.string.dia, resultado)
                        binding.tvDiagnosis.text =
                            getString(R.string.diagnosis, appointment.diagnosis)
                        binding.tvObservation.text =
                            getString(R.string.observations, appointment.observations)
                        binding.tvTreatment.text =
                            getString(R.string.treatment, appointment.treatment)
                        Log.e("TAG", "onStart: ${appointment}")
                        Log.e("TAG", "onStart: ${physio}")
                        binding.tvNombreP.text = getString(R.string.name, physio.name)
                        binding.tvSurname.text = getString(R.string.surname, physio.surname)
                        binding.tvEmail.text = getString(R.string.email, physio.email)
                        binding.tvSpecialty.text = getString(R.string.specialty, physio.specialty)
                        binding.tvLicenseNumber.text =
                            getString(R.string.licenseNumber, physio.licenseNumber)
                    }
                }.collect()
            }
        }
    }
}