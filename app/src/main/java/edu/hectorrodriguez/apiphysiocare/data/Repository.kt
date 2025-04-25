package edu.hectorrodriguez.apiphysiocare.data

import android.util.Log
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import edu.hectorrodriguez.apiphysiocare.utils.SessionManager
import kotlinx.coroutines.flow.Flow
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
        sessionManager.saveSession(response.token!!, request.login,response.rol.toString()) // Se guarda la sesión
        Log.i("Repository", sessionManager.sessionFlowUser.map { it }.toString())
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
}