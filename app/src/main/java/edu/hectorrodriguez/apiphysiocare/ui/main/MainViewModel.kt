package edu.hectorrodriguez.apiphysiocare.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginState
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementsResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.Appointments
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResponseWithPatient
import edu.hectorrodriguez.apiphysiocare.model.records.RecordsWithPatient
import edu.hectorrodriguez.apiphysiocare.utils.isRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    ////////// Login ////////////
    //Función para cerrar sesión
    fun logout() {
        isRecord = false
        viewModelScope.launch {
            repository.logout()
        }
    }
    //Funcion para obtener para el Flow de la sesión
    fun getSessionFlow() = repository.getSessionFlowUser()
    //Funcion para obtener el flow del rol
    fun getSessionFlowRol() = repository.getSessionFlowRol()

    //////// Appointements ///////////
    private val _appointementsState = MutableStateFlow<Appointments?>(Appointments())
    val appointementsState: MutableStateFlow<Appointments?>
        get() = _appointementsState

    /**
     * Función para obtener todos los appointments de la api y los guarda en un mutable state flow
     * @author Héctor Rodríguez Planelles
     */
    fun getAllAppointements() {
        try{
            viewModelScope.launch {
                val token = repository.getSessionFlowUser().first().first
                Log.i(TAG," Token: $token")
                val id = repository.getSessionFlowUser().first().second
                Log.i(TAG," Id: $id")
                val rol = repository.getSessionFlowRol().first().second
                Log.i(TAG," Rol: $rol")
                if (token != null) {
                    val appointementsAux = repository.fetchAppointements(token, id.toString(), rol.toString())
                    if(appointementsAux?.appointments != null){
                        _appointementsState.value = appointementsAux.appointments
                    }else{
                        _appointementsState.value = null
                    }
                    Log.i(TAG," Appointements: ${_appointementsState.value}")
                } else {
                    _appointementsState.value = null
                }
            }
        }catch (e:Exception){
            logout()
            appointementsState.value = Appointments()
        }
    }

    /**
     * Función para obtener todos los appointments de la api y los guarda en un mutable state flow
     * @author Héctor Rodríguez Planelles
     * @param id Id del appointment
     */
    //delete appointement by id
    fun deleteAppointement(id: String) {
        viewModelScope.launch {
            val token = repository.getSessionFlowUser().first().first
            Log.i(TAG," Token: $token")
            if (token != null) {
                repository.deleteAppointement(token, id)
                Log.i(TAG," Appointements: ${_appointementsState.value}")
            } else {
                _appointementsState.value = Appointments()
            }
        }
    }
    //////////// Records /////////////////
    private val _recordsState= MutableStateFlow<RecordsWithPatient?>(null)
    val recordState: MutableStateFlow<RecordsWithPatient?>
        get() = _recordsState

    /**
     * Función para obtener todos los records de la api y los guarda en un mutable state flow
     * @author Héctor Rodríguez Planelles
     */
    fun getRecords(){
        viewModelScope.launch {
            val token = repository.getSessionFlowUser().first().first
            Log.i(TAG," Token: $token")
            if (token != null) {
                var recordAux: RecordResponseWithPatient? =
                    repository.fetchRecords(token)
                _recordsState.value = recordAux?.records
                Log.i(TAG," Record: ${_recordsState.value}")
            } else {
                _recordsState.value = RecordsWithPatient()
            }
        }
    }


    //////////// Fragment Showed ////////////
    private var _fragmentShowed: String? = null

    val fragmentShowed: String?
        get() = _fragmentShowed

    /**
     * Función para guardar el nombre del fragmento que se ha mostrado
     * @author Héctor Rodríguez Planelles
     */
    fun setFragmentShowed(fragmentName:String){
        _fragmentShowed = fragmentName
    }

    ///
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}