package edu.hectorrodriguez.apiphysiocare.data

import android.util.Log
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementsResponse
import edu.hectorrodriguez.apiphysiocare.model.physios.PhysioIdResponse
import edu.hectorrodriguez.apiphysiocare.model.records.RecordRespWithPatient
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResponse
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResponseWithPatient
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class Repository(private val sessionManager: SessionManager){
    /**
     * Se crear una instacia de RemoteDataSource para poder hacer las llamadas para obtener
     * los datos del remote data source
     */
    private val remoteDataSource = RemoteDataSource()
    ////////// Login ////////////
    //Función para obtener el login
    /**
     * Funcion para hacer el login del usuarios
     * @param request LoginRequest con el usuario y la contraseña
     * @author Héctor Rodríguez Planelles
     */
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = remoteDataSource.login(request)
        sessionManager.saveSession(response.token!!, response.id.toString(),response.rol.toString()) // Se guarda la sesión
        Log.i("Repository", sessionManager.sessionFlowUser.first().second.toString())
        Log.i("Repository", sessionManager.sessionFlowRol.map { it }.toString())
        return response
    }

     fun getSessionFlowUser(): Flow<Pair<String?, String?>> = sessionManager.sessionFlowUser
     fun getSessionFlowRol(): Flow<Pair<String?, String?>> = sessionManager.sessionFlowRol
    /**
     * Funcion para limpiar la sesión
     * @author Héctor Rodríguez Planelles
     */
    suspend fun logout(){
        sessionManager.clearSession()
    }


    ////////// Records ///////////
    suspend fun fetchRecords(token:String): RecordResponseWithPatient{
        lateinit var response: RecordResponseWithPatient
        response = remoteDataSource.fechthRecords(token)

        return response
    }

    suspend fun fetchRecordById(token: String,id: String): RecordRespWithPatient{
        var response = remoteDataSource.fecthRecordById(token,id)
        return response
    }

    //Funcion para obtener appointments desde el id del record
    suspend fun fetchAppointementsByIdRecord(token:String,id:String) : AppointementsResponse{
        lateinit var response: AppointementsResponse
        response = remoteDataSource.fechthAppointementsByIdRecord(token, id)
        return response
    }


    ////////// Appointements ///////////
    /**
     * Funcion para obtener todos los appointments de la api
     * @param token Token de la sesión
     * @param id Id del usuario
     * @param rol Rol del usuario
     * @return Devuelve un objeto AppointementsResponse con todos los appointments o nullo si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    //Funcion junta y pasandole el rol tmabien para separar las cosas
    suspend fun fetchAppointements(token:String,id:String,rol:String) : AppointementsResponse? {
        lateinit var response: AppointementsResponse
        try{
            if(rol == "patient") {
                response = remoteDataSource.fechthAppointementsPatient(token, id)
                return response
            }else{
                response = remoteDataSource.fechthAppointementsPhysio(token, id)
                return response
            }
        }catch (e:Exception){
         return null
        }
    }

    suspend fun fecthAppointemenById(token:String,id:String) : AppointementResponse? {
        lateinit var response: AppointementResponse
        try{
                response = remoteDataSource.fecthAppointmentsById(token, id)
                return response
        }catch (e:Exception){
            return null
        }
    }

    suspend fun fecthPhysioById(token:String,id:String) : PhysioIdResponse? {
        lateinit var response: PhysioIdResponse
        try{
            response = remoteDataSource.fecthPhysioById(token, id)
            return response
        }catch (e:Exception){
            return null
        }
    }

    //delte appointment by id
    suspend fun deleteAppointement(token:String,id:String) : Boolean {
        try{
            remoteDataSource.deleteAppointment(token, id)
            return true
        }catch (e:Exception){
            return false
        }
    }

}