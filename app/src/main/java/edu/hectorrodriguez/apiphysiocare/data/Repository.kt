package edu.hectorrodriguez.apiphysiocare.data

import android.util.Log
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementsResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsRequest
import edu.hectorrodriguez.apiphysiocare.model.physios.PhysioIdResponse
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResp
import edu.hectorrodriguez.apiphysiocare.model.records.RecordRespWithPatient
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

    ////////// Appointement CREAR ///////////

    /**
     * Funcion para crear un appointment
     * @param token Token de la sesión
     * @param id Id del usuario
     * @param appointment Objeto AppointmentsRequest con los datos del appointment
     * @return Devuelve un objeto RecordResp con el appointment creado o null si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    suspend fun createAppointment(token: String,id:String, appointment: AppointmentsRequest): RecordResp? {
         var response: RecordResp?
        response = remoteDataSource.createAppointment(token, id, appointment)
        return response
    }


    ////////// Records ///////////
    /**
     * Funcion para obtener todos los records de la api
     * @param token Token de la sesión
     * @return Devuelve un objeto RecordResponseWithPatient con todos los records o nullo si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    suspend fun fetchRecords(token:String): RecordResponseWithPatient?{
        try{
            lateinit var response: RecordResponseWithPatient
            response = remoteDataSource.fechthRecords(token)
            return response
        }catch (e:Exception) {
            Log.e("Repository", "Error al obtener los records: ${e.message}")
            return null
        }
    }

    /**
     * Funcion para obtener un record por id
     * @param token Token de la sesión
     * @param id Id del record
     * @return Devuelve un objeto RecordRespWithPatient con el record o nullo si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    suspend fun fetchRecordById(token: String,id: String): RecordRespWithPatient?{
        var response = remoteDataSource.fecthRecordById(token,id)
        return response
    }

    //Funcion para obtener appointments desde el id del record
    /**
     * Funcion para obtener appointments desde el id del record
     * @param token Token de la sesión
     * @param id Id del record
     * @return Devuelve un objeto AppointementsResponse con los appointments o nullo si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    suspend fun fetchAppointementsByIdRecord(token:String,id:String) : AppointementsResponse?{
         var response: AppointementsResponse?
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

    /**
     * Funcion para obtener un appointment por id
     * @param token Token de la sesión
     * @param id Id del appointment
     * @return Devuelve un objeto AppointementResponse con el appointment o nullo si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    suspend fun fecthAppointemenById(token:String,id:String) : AppointementResponse? {
        lateinit var response: AppointementResponse
        try{
                response = remoteDataSource.fecthAppointmentsById(token, id)
                return response
        }catch (e:Exception){
            return null
        }
    }

    /**
     * Funcion para obtener un physio por id
     * @param token Token de la sesión
     * @param id Id del physio
     * @return Devuelve un objeto PhysioIdResponse con el physio o nullo si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
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
    /**
     * Funcion para eliminar un appointment por id
     * @param token Token de la sesión
     * @param id Id del appointment
     * @return Devuelve true si se ha eliminado el appointment o false si ha habido un error
     * @author Héctor Rodríguez Planelles
     */
    suspend fun deleteAppointement(token:String,id:String) : Boolean {
        try{
            remoteDataSource.deleteAppointment(token, id)
            return true
        }catch (e:Exception){
            return false
        }
    }

}