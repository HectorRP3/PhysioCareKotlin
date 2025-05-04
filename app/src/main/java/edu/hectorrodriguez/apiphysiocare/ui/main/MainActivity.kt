package edu.hectorrodriguez.apiphysiocare.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import edu.hectorrodriguez.apiphysiocare.ui.adapter.AppointementAdapter
import edu.hectorrodriguez.apiphysiocare.ui.fragment.FragmentAppointment
import edu.hectorrodriguez.apiphysiocare.ui.fragment.FragmentRecord
import edu.hectorrodriguez.apiphysiocare.ui.login.LoginActivity
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import edu.hectorrodriguez.apiphysiocare.utils.isPhysio
import edu.hectorrodriguez.apiphysiocare.utils.isRecord
import edu.hectorrodriguez.apiphysiocare.utils.pastAppointments
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var appointementFragment: FragmentAppointment
    private lateinit var recordFragment: FragmentRecord


    private val vm: MainViewModel by viewModels {
       val repository = Repository(SessionManager(dataStore))
        MainViewModelFactory(repository)
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
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
                    isPhysio = false
                }else{
                    unBlockRolButtonsPhysios()
                    isPhysio = true
                }
                if(isRecord){
                    loadFragment(recordFragment)
                }else{
                    loadFragment(appointementFragment)
                }
            }
        }


    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mFrameLayout.id, fragment)
            .commit()
        vm.setFragmentShowed(fragment.javaClass.simpleName)
    }

     fun loadLogin(){
        lifecycleScope.launch {
            delay(500)
            if(vm.getSessionFlow().first().second == ""){
                LoginActivity.navigate(this@MainActivity)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadLogin()

        binding.mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.btn_logOut -> {
                    Log.d(TAG, "LogOut")
                    lifecycleScope.launch {
                        vm.logout()
                        isPhysio = false
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
                    isRecord = true
                    Log.d(TAG, "btnRecords")
                    loadFragment(recordFragment)
                    true
                }
                R.id.btnPastAppointments -> {
                    Log.d(TAG, "btnProfile")
                    isRecord = false

                    pastAppointments=true
                    appointementFragment = FragmentAppointment()
                    loadFragment(appointementFragment)
                    Log.d(TAG, "pastAppointments: $pastAppointments")
                    true
                }
                R.id.btnFutureAppointments -> {
                    Log.d(TAG, "btnProfile")
                    pastAppointments=false
                    isRecord = false
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

}