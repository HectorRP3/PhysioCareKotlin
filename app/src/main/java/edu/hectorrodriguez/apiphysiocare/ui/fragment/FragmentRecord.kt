package edu.hectorrodriguez.apiphysiocare.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import edu.hectorrodriguez.apiphysiocare.databinding.AppointementFragmentBinding
import edu.hectorrodriguez.apiphysiocare.databinding.RecordFragmentBinding
import edu.hectorrodriguez.apiphysiocare.model.records.RecordsWithPatient
import edu.hectorrodriguez.apiphysiocare.ui.adapter.RecordAdapter
import edu.hectorrodriguez.apiphysiocare.ui.detailRecord.DetailRecord
import edu.hectorrodriguez.apiphysiocare.ui.login.LoginActivity
import edu.hectorrodriguez.apiphysiocare.ui.main.MainViewModel
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentRecord: Fragment() {
    private val TAG = FragmentRecord::class.java.simpleName

    private lateinit var binding: RecordFragmentBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val adapter by lazy {
        RecordAdapter(
            onRecordClick = { record->
                Log.i(TAG,"ON CLICK: ${record}")
                DetailRecord.navigate(requireActivity(),record)
            }
        )
    }
    private var query: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        binding = RecordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        val plateId = binding.Search.context
            .resources.getIdentifier("android:id/search_plate", null, null)
        val plate = binding.Search.findViewById<View>(plateId)
        plate.setBackgroundColor(Color.TRANSPARENT)
        binding.mRecicler.layoutManager= LinearLayoutManager(context)
        binding.mRecicler.setHasFixedSize(true)
        binding.mRecicler.adapter = adapter
        showRecords()
        binding.mRecicler.itemAnimator!!.apply {
            changeDuration = 0
        }
    }

    override fun onStart() {
        super.onStart()

        binding.Search.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "OnQuieryTextChannge $newText")
                    query = newText
                    showRecords(query!!)
                    return true
                }
            }
        )
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        runCatching {
            lifecycleScope.launch {
                    showRecords()
                delay(800)
                if(adapter.currentList.isEmpty()){
                    sharedViewModel.logout()
                    LoginActivity.navigate(requireActivity())
                }
            }
        }.onFailure {
            sharedViewModel.logout()
            LoginActivity.navigate(requireActivity())
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

    private fun showRecords(busqueda:String=""){
        adapter.submitList(emptyList())
        try{
            if(checkConnection(requireContext())) {
                lifecycleScope.launch {
                    if(busqueda.isBlank()){
                        sharedViewModel.getRecords()
                        sharedViewModel.recordState.collect {
                            adapter.submitList(it)
                        }
                    }else{
                        sharedViewModel.getRecords()
                        sharedViewModel.recordState.collect {
                            adapter.submitList(it?.filter { d-> d.patient?.name?.toLowerCase()?.contains(query!!) == true ||d.patient?.insuranceNumber?.toLowerCase()?.contains(query!!) ==true
                            })
                        }
                    }
                }
            }
        }catch (e:Exception){
            sharedViewModel.logout()
            LoginActivity.navigate(requireActivity())
        }

    }

}