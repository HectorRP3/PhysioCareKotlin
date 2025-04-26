package edu.hectorrodriguez.apiphysiocare.model.appointements


import com.google.gson.annotations.SerializedName

data class AppointmentsItem(
    @SerializedName("date")
    val date: String?,
    @SerializedName("diagnosis")
    val diagnosis: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("observations")
    val observations: String?,
    @SerializedName("physio")
    val physio: String?,
    @SerializedName("treatment")
    val treatment: String?
)

data class AppointementsResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val appointments:Appointments,
)

data class AppointmentsRequest(
    @SerializedName("date") val date: String,
    @SerializedName("diagnosis") val diagnosis: String,
    @SerializedName("observations") val observations: String,
    @SerializedName("physio") val physio: String,
    @SerializedName("treatment") val treatment: String
)