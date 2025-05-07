package edu.hectorrodriguez.apiphysiocare.ui.fragment

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.hectorrodriguez.apiphysiocare.databinding.AppointementFragmentBinding
import edu.hectorrodriguez.apiphysiocare.ui.adapter.AppointementAdapter
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentActivity
import edu.hectorrodriguez.apiphysiocare.ui.login.LoginActivity
import edu.hectorrodriguez.apiphysiocare.ui.main.MainViewModel
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.isPhysio
import edu.hectorrodriguez.apiphysiocare.utils.pastAppointments
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

class FragmentAppointment: Fragment()  {
    private val TAG = FragmentAppointment::class.java.simpleName

    private lateinit var binding: AppointementFragmentBinding
    private val sharedViewModel : MainViewModel by activityViewModels()
    private val adapter by lazy {
        AppointementAdapter(
            onAppointementClick = { appointement ->
                if(pastAppointments){
                    Log.d(TAG, "onClick: ${appointement}")
                    DetailAppointmentActivity.navigate(requireActivity(), appointement)
                }else{
                    Toast.makeText(requireContext(), "No se puede acceder a las citas futuras", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = {
                if(isPhysio){
                    sharedViewModel.deleteAppointement(it)
                    showAppointments()
                }else{
                    Toast.makeText(requireContext(), "No se puede eliminar la cita", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
     override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         Log.d(TAG, "onCreateView: ")
         binding = AppointementFragmentBinding.inflate(inflater, container, false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        binding.mRecycler.layoutManager= LinearLayoutManager(context)
        binding.mRecycler.adapter = adapter
        showAppointments()
    }

    override fun onStart() {
        super.onStart()
        showAppointments()

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            showAppointments()
            binding.swipeRefresh.isRefreshing = false
        }
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        runCatching {
            lifecycleScope.launch {
                delay(2000)
                sharedViewModel.appointementsState.collect {
                    Log.d(TAG, "onResume: ${it}")
                    delay(500)
                    if(it == null){
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("No tienes citas")
                            .setMessage("No tienes citas programadas, pide al physio que te programe una cita")
                            .show()
                        sharedViewModel.logout()
                        LoginActivity.navigate(requireActivity())
                    }
                    val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                    val appointemenFut = it?.filter { d ->
                        val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                        date > dateNow
                    }
                    val appointementPast = it?.filter { d ->
                        val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                        date < dateNow
                    }
                    if(appointementPast.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "No se hay citas pasadas", Toast.LENGTH_SHORT).show()
                    }
                    if(appointemenFut.isNullOrEmpty()){
                        Toast.makeText(requireContext(), "No se hay citas futuras", Toast.LENGTH_SHORT).show()
                    }


                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    private fun showAppointments(){
        adapter.submitList(emptyList())
        if(checkConnection(requireContext())) {
            lifecycleScope.launch {
                sharedViewModel.getAllAppointements()
                sharedViewModel.appointementsState.collect {
                    Log.d(TAG, "showAppointments: ${it}")
                    if (!pastAppointments) {
                        val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                        val appointemenFut = it?.filter { d ->
                            val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                            date > dateNow
                        }
                        adapter.submitList(appointemenFut)
                    } else {
                        val dateNow = Instant.now().atZone(ZoneOffset.UTC)
                        val appointementPast = it?.filter { d ->
                            val date = Instant.parse(d.date).atZone(ZoneOffset.UTC)
                            date < dateNow
                        }
                        adapter.submitList(appointementPast)
                    }
                }
            }
        }

    }
}