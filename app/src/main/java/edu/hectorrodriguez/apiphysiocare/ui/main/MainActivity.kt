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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.hectorrodriguez.apiphysiocare.R
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.databinding.ActivityMainBinding
import edu.hectorrodriguez.apiphysiocare.databinding.LayoutUserinfoBinding
import edu.hectorrodriguez.apiphysiocare.ui.login.LoginActivity
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    companion object {
        fun navigate(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
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
        binding.mToolbar.inflateMenu(R.menu.menu_top)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                val rol = vm.getSessionFlowRol().first().second.toString()
                var nombre = vm.getSessionFlow().first().second
                binding.mToolbar.setTitle("PhysioCare | $rol $nombre")
                if(rol == "patient"){
                    blockRolButtonsPatients()
                }else{
                    unBlockRolButtonsPhysios()
                }
            }

        }

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