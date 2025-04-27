package edu.hectorrodriguez.apiphysiocare.data

import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementsResponse
import edu.hectorrodriguez.apiphysiocare.model.physios.PhysioIdResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

class Retrofit2Api {
    companion object {
        const val BASE_URL = "https://hectorrp.com/api/"
        //const val BASE_URL = "http://localhost:8080"
        fun getRetrofit2Api(): Retrofit2ApiInterface {
            return retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create()).build()
                .create(Retrofit2ApiInterface::class.java)
        }
    }
}

interface Retrofit2ApiInterface {

    @POST("auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body request: LoginRequest) : Response<LoginResponse>

    @GET("records/appointments/patients/{id}")
    suspend fun getAppointmentsByIdPateint(@Header("Authorization") token: String, @Path("id") id: String): Response<AppointementsResponse>

    @GET("records/appointments/physio/{id}")
    suspend fun getAppointmentsByIdPhysio(@Header("Authorization") token: String, @Path("id") id: String): Response<AppointementsResponse>

    @GET("records/appointments/{id}")
    suspend fun getAppointmentById(@Header("Authorization") token: String, @Path("id") id: String):Response<AppointementResponse>

    @GET("physios/{id}")
    suspend fun getPhysioById(@Header("Authorization") token: String, @Path("id") id: String):Response<PhysioIdResponse>

    @DELETE("records/appointments/{id}")
    suspend fun deleteAppointment(@Header("Authorization") token: String, @Path("id") id: String): Response<Void>
}
