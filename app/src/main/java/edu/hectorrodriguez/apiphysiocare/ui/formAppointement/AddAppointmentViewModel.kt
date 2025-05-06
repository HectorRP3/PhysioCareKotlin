package edu.hectorrodriguez.apiphysiocare.ui.formAppointement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.hectorrodriguez.apiphysiocare.data.Repository
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsRequest
import edu.hectorrodriguez.apiphysiocare.ui.detailRecord.DetailRecordViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddAppointmentViewModel(private val repository: Repository,private val idRecord: String):ViewModel() {
    private val TAG = AddAppointmentViewModel::class.java.simpleName

    /**
     * Función para añadir un appointment a la api
     * @author Héctor Rodríguez Planelles
     * @param date String date
     * @param diagnosis String diagnosis
     * @param observations String observations
     * @param treatment String treatment
     */
    fun addAppointemnt(
        date: String,
        diagnosis: String,
        observations: String,
        treatment: String,
    ) {
        viewModelScope.launch {
            val token = repository.getSessionFlowUser().first().first
            val idPhysio = repository.getSessionFlowUser().first().second
            if (token != null) {
                var appointementAux = AppointmentsRequest(
                    date=date,
                    diagnosis=diagnosis,
                    observations=observations,
                    physio = idPhysio.toString(),
                    treatment = treatment
                )
                Log.i(TAG, "addAppointemnt: $appointementAux")
                Log.i(TAG, "addAppointemnt: ${idRecord}")



                val response = repository.createAppointment(
                    token,
                    idRecord,
                    AppointmentsRequest(
                        date,
                        diagnosis,
                        observations,
                        idPhysio.toString(),
                        treatment
                    )
                )
                if (response?.ok == true) {
                    Log.d(TAG, "addAppointemnt: Appointment created successfully")
                    Log.d(TAG, "addAppointemnt: Appointment created successfully")
                } else {
                    Log.e(TAG, "addAppointemnt: ${response?.records}")
                }
            }else{
                Log.e(TAG, "addAppointemnt: Token is null")
            }
        }
    }

}
@Suppress("UNCHECKED_CAST")
class AddAppointmentFactory(private val repository: Repository,private val idRecord:String = ""):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddAppointmentViewModel(repository,idRecord) as T
    }
}