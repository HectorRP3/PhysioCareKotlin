package edu.hectorrodriguez.apiphysiocare.data

import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointementsResponse
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsRequest
import edu.hectorrodriguez.apiphysiocare.model.physios.PhysioIdResponse
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResp
import edu.hectorrodriguez.apiphysiocare.model.records.RecordRespWithPatient
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResponse
import edu.hectorrodriguez.apiphysiocare.model.records.RecordResponseWithPatient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

class Retrofit2Api {
    companion object {
        const val BASE_URL = "https://hectorrp.com/api/"
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
    //coger todo los appointments
    @GET("records/moviles")
    suspend fun getRecords(@Header("Authorization") token: String): Response<RecordResponseWithPatient>
    //coger el record por id patient
    @GET("records/patient/{id}")
    suspend fun getRecordsById(@Header("Authorization") token: String,@Path("id") id: String): Response<RecordRespWithPatient>
    //Coger appointment por id de record
    @GET("records/{id}/appointments")
    suspend fun getAppointmentsByIdRecord(@Header("Authorization") token: String,@Path("id") id: String): Response<AppointementsResponse>
    @POST("records/appointments/{id}")
    @Headers("Content-Type: application/json")
    suspend fun createAppointment(@Header("Authorization") token: String, @Path("id") id: String, @Body appointment: AppointmentsRequest): Response<RecordResp>
    //Coger todos los appointments de patientsId
    @GET("records/appointments/patients/{id}")
    suspend fun getAppointmentsByIdPateint(@Header("Authorization") token: String, @Path("id") id: String): Response<AppointementsResponse>
    //coger todos los appointment por physioId
    @GET("records/appointments/physio/{id}")
    suspend fun getAppointmentsByIdPhysio(@Header("Authorization") token: String, @Path("id") id: String): Response<AppointementsResponse>
    //coger appointment por id
    @GET("records/appointments/{id}")
    suspend fun getAppointmentById(@Header("Authorization") token: String, @Path("id") id: String):Response<AppointementResponse>
    //coger physio por id
    @GET("physios/{id}")
    suspend fun getPhysioById(@Header("Authorization") token: String, @Path("id") id: String):Response<PhysioIdResponse>
    //borrar appointment
    @DELETE("records/appointments/{id}")
    suspend fun deleteAppointment(@Header("Authorization") token: String, @Path("id") id: String): Response<Void>
}
