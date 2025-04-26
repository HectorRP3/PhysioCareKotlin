package edu.hectorrodriguez.apiphysiocare.model.records


import com.google.gson.annotations.SerializedName
import edu.hectorrodriguez.apiphysiocare.model.appointements.Appointments

data class RecordItem(
    @SerializedName("appointments")
    val appointments: Appointments,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("medicalRecord")
    val medicalRecord: String?,
    @SerializedName("patient")
    val patient: String?
)

data class RecordResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val records: Records,
)