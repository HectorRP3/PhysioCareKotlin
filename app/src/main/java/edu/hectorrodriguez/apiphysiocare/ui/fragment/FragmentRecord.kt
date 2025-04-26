package edu.hectorrodriguez.apiphysiocare.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.hectorrodriguez.apiphysiocare.databinding.AppointementFragmentBinding
import edu.hectorrodriguez.apiphysiocare.databinding.RecordFragmentBinding
import edu.hectorrodriguez.apiphysiocare.ui.main.MainViewModel


class FragmentRecord: Fragment() {
    private val TAG = FragmentRecord::class.java.simpleName

    private lateinit var binding: RecordFragmentBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val adapter by lazy {

    }

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

    }

    override fun onStart() {
        super.onStart()

    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}