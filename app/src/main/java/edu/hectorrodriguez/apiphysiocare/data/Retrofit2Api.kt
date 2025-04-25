package edu.hectorrodriguez.apiphysiocare.data

import edu.hectorrodriguez.apiphysiocare.model.LoginRequest
import edu.hectorrodriguez.apiphysiocare.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

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

}
