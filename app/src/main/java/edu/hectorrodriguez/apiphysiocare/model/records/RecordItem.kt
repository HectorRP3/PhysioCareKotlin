package edu.hectorrodriguez.apiphysiocare.model.records


import com.google.gson.annotations.SerializedName
import edu.hectorrodriguez.apiphysiocare.model.appointements.Appointments
import edu.hectorrodriguez.apiphysiocare.model.patients.Patient

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
data class RecordResp(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val records: RecordItem,
)

data class RecordItemWithPatient(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("medicalRecord")
    val medicalRecord: String?,
    @SerializedName("patient")
    val patient: Patient?
)
data class RecordRespWithPatient(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val records: RecordItemWithPatient,
)
data class RecordResponseWithPatient(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val records: RecordsWithPatient,
)
