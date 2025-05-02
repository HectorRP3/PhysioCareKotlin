package edu.hectorrodriguez.apiphysiocare.data

import android.util.Log
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementsResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsRequest
import edu.hectorrodriguez.apiphysiocare.model.physios.PhysioIdResponse
import edu.hectorrodriguez.apiphysiocare.model.records.RecordItemWithPatient
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResp
import edu.hectorrodriguez.apiphysiocare.model.records.RecordRespWithPatient
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResponseWithPatient

class RemoteDataSource {
    private val TAG = RemoteDataSource::class.java.simpleName
    private val api = Retrofit2Api.getRetrofit2Api()

    //Función para obtener el login, se pasa el objeto RequestLogin en el body
    //Se devuelve un obketo LoginResponse
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request)
        if(response.isSuccessful){
            Log.e(TAG, "Login correcto")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error en login: ${response.message()}")
        }
    }

    //Funcion para crear un appointment
    suspend fun createAppointment(token: String,id:String, appointment: AppointmentsRequest): RecordResp?{
        val response = api.createAppointment("Bearer $token",id,appointment)
        if(response.isSuccessful){
            Log.e(TAG, "Creado appointment")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
return null
        }
    }

    //Funcion para obtener todos los recordsç
    suspend fun fechthRecords(token:String) : RecordResponseWithPatient{
        val response = api.getRecords("Bearer $token")
        if(response.isSuccessful){
            Log.e(TAG, "Obtenidos records")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error al obeneter records: ${response.message()}")
        }
    }

    suspend fun fecthRecordById(token: String,id:String): RecordRespWithPatient?{
        val response = api.getRecordsById("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Obtenido records")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            return null
        }
    }

    //Funcion para obtener appointments desde el id del record
    suspend fun fechthAppointementsByIdRecord(token:String,id:String) : AppointementsResponse?{
        val response = api.getAppointmentsByIdRecord("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Obtenidos appointments del record")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            return null
        }
    }

    //Función para obtener appointments desde el id del paciente
    suspend fun fechthAppointementsPatient(token:String,id:String) : AppointementsResponse{
        val response = api.getAppointmentsByIdPateint("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Obtenidos appointments del paciente")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error al obeneter appointements: ${response.message()}")
        }
    }

    //Función para obtener appointments desde el id del fisio
    suspend fun fechthAppointementsPhysio(token:String,id:String) : AppointementsResponse{
        val response = api.getAppointmentsByIdPhysio("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Obtenidos appointments del fisio")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error al obeneter appointements: ${response.message()}")
        }
    }

    //Funcion para obtener un appointement desde el id del appointement
    suspend fun fecthAppointmentsById(token: String,id:String): AppointementResponse{
        val response = api.getAppointmentById("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Obtenido appointment")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error al obeneter appointement: ${response.message()}")
        }
    }

    //Coger un physio por id
    suspend fun fecthPhysioById(token: String,id:String): PhysioIdResponse{
        val response = api.getPhysioById("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Obtenido physio")
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error al obeneter physio: ${response.message()}")
        }
    }

    //Funcion para borrar un appointment by id
    suspend fun deleteAppointment(token: String,id:String): Boolean{
        val response = api.deleteAppointment("Bearer $token",id)
        if(response.isSuccessful){
            Log.e(TAG, "Eliminado appointment")
            return true
        }else{
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Error :${response.message()} | $errorBody")
            throw  Exception("Error al eliminar appointement: ${response.message()}")
        }
    }

}