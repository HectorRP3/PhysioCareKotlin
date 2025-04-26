package edu.hectorrodriguez.apiphysiocare.ui.main

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.hectorrodriguez.apiphysiocare.R
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.databinding.ActivityMainBinding
import edu.hectorrodriguez.apiphysiocare.databinding.LayoutUserinfoBinding
import edu.hectorrodriguez.apiphysiocare.model.LoginState
import edu.hectorrodriguez.apiphysiocare.ui.fragment.FragmentAppointment
import edu.hectorrodriguez.apiphysiocare.ui.fragment.FragmentRecord
import edu.hectorrodriguez.apiphysiocare.ui.login.LoginActivity
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import edu.hectorrodriguez.apiphysiocare.utils.pastAppointments
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var appointementFragment: FragmentAppointment
    private lateinit var recordFragment: FragmentRecord

    private val adapter by lazy {
        AppointementAdapter(
            onAppointementClick = { appointement ->
                Log.d(TAG, "onClick: ${appointement}")
            },

        )
    }


    private val vm: MainViewModel by viewModels {
       val repository = Repository(SessionManager(dataStore))
        MainViewModelFactory(repository)
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        LoginActivity.navigate(this@MainActivity)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        appointementFragment = FragmentAppointment()
        recordFragment = FragmentRecord()
        binding.mToolbar.inflateMenu(R.menu.menu_top)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                val rol = vm.getSessionFlowRol().first().second.toString()
                binding.mToolbar.setTitle("PhysioCare | $rol ")
                val date = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE )
                binding.mToolbar.setSubtitle("Dia de hoy: "+date)
                if(rol == "patient"){
                    blockRolButtonsPatients()
                }else{
                    unBlockRolButtonsPhysios()
                }
                loadFragment(appointementFragment)
            }
        }


    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mFrameLayout.id, fragment)
            .commit()
        vm.setFragmentShowed(fragment.javaClass.simpleName)
    }

    override fun onStart() {
        super.onStart()
        binding.mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.btn_logOut -> {
                    Log.d(TAG, "LogOut")
                    lifecycleScope.launch {
                        vm.logout()
                        LoginActivity.navigate(this@MainActivity)
                    }
                    true
                }
                R.id.btn_acercaDe -> {
                    Log.d(TAG, "Acerca")
                    mostrarAcerca()
                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.mBottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btnRecords -> {
                    Log.d(TAG, "btnRecords")
                    loadFragment(recordFragment)
                    true
                }
                R.id.btnPastAppointments -> {
                    Log.d(TAG, "btnProfile")
                    pastAppointments=true
                    appointementFragment = FragmentAppointment()
                    loadFragment(appointementFragment)
                    Log.d(TAG, "pastAppointments: $pastAppointments")
                    true
                }
                R.id.btnFutureAppointments -> {
                    Log.d(TAG, "btnProfile")
                    pastAppointments=false
                    appointementFragment = FragmentAppointment()
                    loadFragment(appointementFragment)
                    Log.d(TAG, "pastAppointments: $pastAppointments")
                    true
                }
                else -> false
            }
        }

    }
    private fun mostrarAcerca(){
        val bindDialog = LayoutUserinfoBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this).apply {
            setView(bindDialog.root)
            setCancelable(false)
            setPositiveButton("Aceptar"){ dialog, _ ->
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun blockRolButtonsPatients(){
        binding.mBottomNavView.menu.findItem(R.id.btnRecords).isVisible = false

    }

    private fun unBlockRolButtonsPhysios(){
        binding.mBottomNavView.menu.findItem(R.id.btnRecords).isVisible = true
    }

    private fun showAppointments(){
        adapter.submitList(emptyList())
        if(checkConnection(this)) {
            lifecycleScope.launch {
                vm.getAllAppointements()
                vm.appointementsState.collect {
                    if (!pastAppointments) {
                        val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                        val appointemenFut = it.filter { d ->
                            val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                            date > dateNow
                        }
                        adapter.submitList(appointemenFut)
                    } else {
                        val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                        val appointementPast = it.filter { d ->
                            val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                            date < dateNow
                        }
                        adapter.submitList(appointementPast)
                    }

                }
                /* vm.loginState.collect {
                    when (it) {
                        is LoginState.Idle ->{
                            Log.i(TAG, "Idle")
                            vm.getAllAppointements()
                            vm.appointementsState.collect {
                                if(pastAppointments){
                                    val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                                    var appointemenFut = it.filter { d->
                                        val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                                        date > dateNow
                                    }
                                    adapter.submitList(appointemenFut)
                                }else{
                                    val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                                    var appointementPast = it.filter { d->
                                        val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                                        date < dateNow
                                    }
                                    adapter.submitList(appointementPast)
                                }

                            }
                        }
                        is LoginState.Loading -> Log.i(TAG, "Loading")
                        is LoginState.Success -> {
                            Log.i(TAG, "Success: ${it.response.token}")
                            vm.getAllAppointements()
                            vm.appointementsState.collect {
                                if(pastAppointments){
                                    val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                                    var appointemenFut = it.filter { d->
                                        val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                                        date > dateNow
                                    }
                                    adapter.submitList(appointemenFut)
                                }else{
                                    val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                                    var appointementPast = it.filter { d->
                                        val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                                        date < dateNow
                                    }
                                    adapter.submitList(appointementPast)
                                }

                            }
                        }

                        is LoginState.Error -> {
                            Log.e(TAG, "Error: ${it.message}")
                        }
                    }
                    return@collect
                }
                return@launch
            }*/
            }
        }

    }
}