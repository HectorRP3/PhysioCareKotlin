package edu.hectorrodriguez.apiphysiocare.ui.login

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
import edu.hectorrodriguez.apiphysiocare.databinding.ActivityLoginBinding
import edu.hectorrodriguez.apiphysiocare.model.LoginState
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import edu.hectorrodriguez.apiphysiocare.utils.checkConnection
import edu.hectorrodriguez.apiphysiocare.utils.dataStore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    private lateinit var binding:ActivityLoginBinding
    companion object {
        fun navigate(activity: Activity){
            val intent = Intent(activity,LoginActivity::class.java)
            activity.startActivity(
                intent ,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }

    private val vm:LoginViewModel by viewModels{
        val repository = Repository(SessionManager(dataStore))
        LoginViewModelFactory(repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //accion de logearse
        binding.btnLogin.setOnClickListener{
            if(comprobarCampos()){
                if(checkConnection(this)){
                    vm.login(binding.tietName.text.toString(),binding.tietPassword.text.toString())
                    lifecycleScope.launch {
                        vm.loginState.collect {
                            when (it) {
                                is LoginState.Idle -> Log.i(TAG, "Idle")
                                is LoginState.Loading -> Log.i(TAG, "Loading")
                                is LoginState.Success -> {
                                    Log.i(TAG, "Success: ${it.response.token}")
                                    Toast.makeText(this@LoginActivity,getString(R.string.correctLogIn,binding.tietName.text.toString()),Toast.LENGTH_SHORT).show()
                                    finish()
                                }

                                is LoginState.Error -> {
                                    Log.e(TAG, "Error: ${it.message}")
                                    Toast.makeText(this@LoginActivity,
                                        getString(R.string.errorLogIn),Toast.LENGTH_LONG).show()

                                }
                            }
                            return@collect
                        }
                        return@launch
                    }
                }
            }
        }
    }

    fun comprobarCampos():Boolean{
        val textName = binding.tietName.text.toString().trim()
        val textPassword = binding.tietPassword.text.toString().trim()
        if(textName.isBlank() || textName.isEmpty()){
            binding.tietName.error = getString(R.string.ErrorNombre)
            return false
        }
        if(textPassword.isBlank() || textPassword.isEmpty()){
            binding.tietPassword.error = getString(R.string.errorPassword)
            return false
        }
        return true;
    }
}