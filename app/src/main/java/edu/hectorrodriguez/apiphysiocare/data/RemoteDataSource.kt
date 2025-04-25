package edu.hectorrodriguez.apiphysiocare.data

import android.util.Log
import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse

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
}