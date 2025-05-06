package edu.hectorrodriguez.apiphysiocare.ui.detailAppointment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.model.appointements.Appointments
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsItem
import edu.hectorrodriguez.apiphysiocare.model.physios.PhysioItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailAppointmentViewModel(private val repository: Repository,private val idAppointment:String = ""): ViewModel() {
    private val TAG = DetailAppointmentViewModel::class.java.simpleName
    private val _appointement = MutableStateFlow<AppointmentsItem?>(null)
    val appointment: MutableStateFlow<AppointmentsItem?>
        get() = _appointement

    private val _physio = MutableStateFlow<PhysioItem?>(null)
    val physio: MutableStateFlow<PhysioItem?>
        get() = _physio

    /**
     * Función para obtener todos los appointments de la api y los guarda en un mutable state flow
     * @author Héctor Rodríguez Planelles
     */
    fun getAppointemnt(){
        viewModelScope.launch {
            Log.i(TAG,"GET APPOINTMENTS")
            val token = repository.getSessionFlowUser().first().first
            if(token!=null){
                Log.i(TAG,"Get appointment then token")
                val appointemenAux = repository.fecthAppointemenById(token,idAppointment)
                _appointement.value = appointemenAux?.appointments
                Log.i(TAG,"Get appointment then token ${_appointement.value}")
                val physioAux = repository.fecthPhysioById(token,appointemenAux?.appointments?.physio.toString())
                _physio.value = physioAux?.physio
                Log.i(TAG,"Get appointment then token ${_physio.value}")
            }else{
                Log.i(TAG,"No hay token")
            }
        }

    }

}

@Suppress("UNCHECKED_CAST")
class DetailAppointmentViewModelFactory(private val repository: Repository,private val idAppointemtn:String = ""):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailAppointmentViewModel(repository,idAppointemtn) as T
    }
}