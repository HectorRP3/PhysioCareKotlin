package edu.hectorrodriguez.apiphysiocare.model.patients


import com.google.gson.annotations.SerializedName

data class Patient(
    @SerializedName("address")
    val address: String?,
    @SerializedName("birthDate")
    val birthDate: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("insuranceNumber")
    val insuranceNumber: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("surname")
    val surname: String?
)