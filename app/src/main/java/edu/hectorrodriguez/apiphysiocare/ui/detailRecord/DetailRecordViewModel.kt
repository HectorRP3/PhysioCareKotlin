package edu.hectorrodriguez.apiphysiocare.ui.detailRecord

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.model.appointements.Appointments
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsItem
import edu.hectorrodriguez.apiphysiocare.model.records.RecordItemWithPatient
import edu.hectorrodriguez.apiphysiocare.model.records.RecordRespWithPatient
import edu.hectorrodriguez.apiphysiocare.ui.detailAppointment.DetailAppointmentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailRecordViewModel(private val repository: Repository,private val idRecord:String): ViewModel() {
    private val TAG = DetailRecordViewModel::class.java.simpleName

    private val _record = MutableStateFlow<RecordItemWithPatient?>(null)
    val record: MutableStateFlow<RecordItemWithPatient?>
        get() = _record

    /**
     * Función para obtener todos los appointments de la api y los guarda en un mutable state flow
     * @author Héctor Rodríguez Planelles
     */
    fun getRecordById(){
        viewModelScope.launch {
            val token = repository.getSessionFlowUser().first().first
            if(token!=null){
                val recordAux = repository.fetchRecordById(token,idRecord)
                _record.value = recordAux?.records
                Log.i(TAG,"RECORDDDD ${_record.value}")
            }
        }
    }

    private val _appointementsState = MutableStateFlow<Appointments?>(Appointments())
    val appointementsState: MutableStateFlow<Appointments?>
        get() = _appointementsState

    /**
     * Función para obtener todos los appointments de la api y los guarda en un mutable state flow
     * @author Héctor Rodríguez Planelles
     */
    fun getAppointmentById(){
        viewModelScope.launch {
            val token = repository.getSessionFlowUser().first().first
            if(token!=null){
                val appointementAux = repository.fetchAppointementsByIdRecord(token,idRecord)
                _appointementsState.value = appointementAux?.appointments
                Log.i(TAG,"APPOINTMENT ${_appointementsState.value}")
            }
        }
    }

    /**
     * Función para cerrar sesión
     * @author Héctor Rodríguez Planelles
     */
    //Función para cerrar sesión
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    /**
     * Función para eliminar un appointment
     * @param id Id del appointment
     * @author Héctor Rodríguez Planelles
     */
    fun deleteAppointement(id: String) {
        viewModelScope.launch {
            val token = repository.getSessionFlowUser().first().first
            Log.i(TAG," Token: $token")
            if (token != null) {
                repository.deleteAppointement(token, id)
                Log.i(TAG," Appointements: ${_appointementsState.value}")
                getAppointmentById()
                Log.i(TAG," Appointements: ${_appointementsState.value}")
            } else {
                _appointementsState.value = Appointments()
            }
        }
    }

}
@Suppress("UNCHECKED_CAST")
class DetailRecordViewModelFactory(private val repository: Repository,private val idRecord:String = ""):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailRecordViewModel(repository,idRecord) as T
    }
}