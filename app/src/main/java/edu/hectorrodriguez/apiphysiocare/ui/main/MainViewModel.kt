package edu.hectorrodriguez.apiphysiocare.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {
    ////////// Login ////////////
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState>
        get() = _loginState

    //Función para inciar sesión y obteneer el token
    fun login(user:String, password:String){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try{
                val response = repository.login(LoginRequest(user,password))
                _loginState.value = LoginState.Success(response)

            }catch (e:Exception){
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }
    //Función para cerrar sesión
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    //Funcion para obtener para el Flow de la sesión
    fun getSessionFlow() = repository.getSessionFlowUser()
    //Funcion para obtener el flow del rol
    fun getSessionFlowRol() = repository.getSessionFlowRol()
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}