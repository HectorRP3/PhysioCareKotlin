package edu.hectorrodriguez.apiphysiocare.ui.detailRecord

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
import edu.hectorrodriguez.apiphysiocare.databinding.ActivityDetailRecordBinding
import edu.hectorrodriguez.apiphysiocare.ui.adapter.AppointementAdapter
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentActivity
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentActivity.Companion.APPOINTMENT_ID
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentViewModel
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentViewModelFactory
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import edu.hectorrodriguez.apiphysiocare.utils.isPhysio
import edu.hectorrodriguez.apiphysiocare.utils.pastAppointments
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.getValue

class DetailRecord : AppCompatActivity() {
    private val TAG = DetailRecord::class.java.simpleName
    private lateinit var binding: ActivityDetailRecordBinding
    companion object {
        const val RECORD_ID = "RECORD_ID"
        fun navigate(activity: Activity,recordId:String = ""){
            val intent = Intent(activity, DetailRecord::class.java).apply {
                putExtra(RECORD_ID, recordId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity.startActivity(
                intent ,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }
    private val vm: DetailRecordViewModel by viewModels{
        val repository = Repository(SessionManager(dataStore))
        val recordId = intent.getStringExtra(RECORD_ID).toString()
        DetailRecordViewModelFactory(repository,recordId)

    }

    private val adapter by lazy {
        AppointementAdapter(
            onAppointementClick = { appointement ->
                Log.d(TAG, "onClick: ${appointement}")
                DetailAppointmentActivity.navigate(this, appointement)
            },
            onDeleteClick = {
                vm.deleteAppointement(it)
                showAppointments()
            }
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mToolbar.setTitle("Detalles del record")
        binding.mRecycler.adapter = adapter

        runCatching {
            vm.getRecordById()
        }.onFailure {
            if(vm.record.value==null){
                Toast.makeText(
                    this@DetailRecord, "Error no se ha podido coger el RECORD",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if (checkConnection(this)) {
            lifecycleScope.launch {
                showAppointments()
                vm.record.collect {
                    if (it != null) {
                        Log.e("TAG", "onStart: ${it.medicalRecord}")
                        binding.tvObservation.text = "Medial record:"
                        binding.tvEmail.text = "Email ${it.patient?.email}"
                        binding.tvNombreP.text = "Nombre ${it.patient?.name} ${it.patient?.surname}"
                        binding.tvLicenseNumber.text =
                            "Insurance Number: ${it.patient?.insuranceNumber}"
                        binding.tvBirthdate.text = "Birthdata ${it.patient?.birthDate}"
                        if (it?.patient?.birthDate != null) {
                            val date = it.patient.birthDate
                            val dateFormar = Instant.parse(date).atZone(ZoneOffset.UTC)
                            val resultado = dateFormar.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            binding.tvBirthdate.setText(
                                Html.fromHtml(
                                    "Cumpleaños ${resultado}",
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            )

                        }
                        binding.tvObservation.text = "Medical Record: ${it?.medicalRecord}"
                    }
                }

            }
        }
    }
    private fun showAppointments(){
        adapter.submitList(emptyList())
        if(checkConnection(this)) {
            lifecycleScope.launch {
                vm.getAppointmentById()
                vm.appointementsState.collect {
                    Log.d(TAG, "showAppointments: ${it}")
                        adapter.submitList(it)
                    }

                }
            }
        }

    }
