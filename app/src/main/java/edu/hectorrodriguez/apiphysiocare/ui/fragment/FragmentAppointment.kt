package edu.hectorrodriguez.apiphysiocare.ui.fragment

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
import edu.hectorrodriguez.apiphysiocare.databinding.AppointementFragmentBinding
import edu.hectorrodriguez.apiphysiocare.ui.adapter.AppointementAdapter
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentActivity
import edu.hectorrodriguez.apiphysiocare.ui.main.MainViewModel
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.isPhysio
import edu.hectorrodriguez.apiphysiocare.utils.pastAppointments
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
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        adapter.submitList(emptyList())
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
            }
        }

    }
}